package com.vk.sarthi.ui.screen

import android.util.Log
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.vk.sarthi.model.ForgetPasswordOTPReq
import com.vk.sarthi.model.UserModel
import com.vk.sarthi.model.VerifyPassword
import com.vk.sarthi.ui.nav.Screens
import com.vk.sarthi.service.Service
import com.vk.sarthi.ui.theme.SarthiTheme
import com.vk.sarthi.utli.SettingPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@Composable
fun ForgetPassword(navigator: NavHostController?) {

    val viewModel: ForgetPasswordViewModel = hiltViewModel()
    val mobileNumber = remember {
        mutableStateOf("")
    }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

        val collectAsState = viewModel.state.collectAsState()
        val state = remember {
            collectAsState
        }
        val context = LocalContext.current

        val focusManager = LocalFocusManager.current

        when (state.value) {
            is ForgetPWSState.Empty -> {
                ShowMobileUI(focusManager, viewModel,mobileNumber)
            }

            is ForgetPWSState.Progress -> {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(modifier = Modifier.padding(10.dp))
                }
            }

            is ForgetPWSState.OTPSend -> {
                OTPPwsUI(viewModel, focusManager,mobileNumber)

            }
            is ForgetPWSState.SuccessFull -> {
                LaunchedEffect(key1 =state ){
                    SettingPreferences.saveUser((state.value as ForgetPWSState.SuccessFull).user, context)
                    navigator?.navigate(Screens.Dashboard.route) {
                        popUpTo(Screens.Login.route) {
                            inclusive = true
                        }
                    }
                }

            }

            else -> {

            }
        }


    }
}

@Composable
fun OTPPwsUI(
    viewModel: ForgetPasswordViewModel?,
    focusManager: FocusManager?,
    mobileNumber: MutableState<String>
) {

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val otpOne = remember {
            mutableStateOf("")
        }
        val otpTwo = remember {
            mutableStateOf("")
        }
        val otpThree = remember {
            mutableStateOf("")
        }
        val otpFour = remember {
            mutableStateOf("")
        }

        val newPassword = remember {
            mutableStateOf("")
        }

        val reEnterPassword = remember {
            mutableStateOf("")
        }

        Column(modifier = Modifier.fillMaxWidth(0.8f), horizontalAlignment = Alignment.Start) {

            Text(text = "OTP", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {

                CommonOtpTextField(otp = otpOne, focusManager)
                CommonOtpTextField(otp = otpTwo, focusManager = focusManager)
                CommonOtpTextField(otp = otpThree, focusManager = focusManager)
                CommonOtpTextField(otp = otpFour, focusManager = focusManager, true)
            }

            Text(
                modifier = Modifier.padding(top = 10.dp),
                text = "New Password",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            OutlinedTextField(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .fillMaxWidth(),
                value = newPassword.value,
                onValueChange = { newPassword.value = it },
                label = { Text(text = "New Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            OutlinedTextField(
                modifier = Modifier
                    .padding(vertical = 5.dp)
                    .fillMaxWidth(),
                value = reEnterPassword.value,
                onValueChange = { reEnterPassword.value = it },
                label = { Text(text = "Re Enter Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val otp =  otpOne.value.plus(otpTwo.value).plus(otpThree.value).plus(otpFour.value)
                    Log.d("@@", "OTPPwsUI: $otp")
                    viewModel?.verifyOTPPassword(mobileNumber.value,otp,newPassword.value)
                },
                enabled = (newPassword.value.isNotEmpty() && reEnterPassword.value.isNotEmpty() && newPassword.value == reEnterPassword.value)
            ) {
                Text(text = "Reset Password")
            }


        }


    }
}

@Composable
private fun ShowMobileUI(
    focusManager: FocusManager,
    viewModel: ForgetPasswordViewModel,
    mobileNumber: MutableState<String>
) {
    val scrollState = rememberScrollState()


    val btnEnable = remember {
        mutableStateOf(false)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .wrapContentHeight()
            .scrollable(scrollState, Orientation.Vertical)

    ) {

        Text(
            text = "Forget Password", fontSize = 22.sp,
            modifier = Modifier
                .padding(vertical = 5.dp)
                .fillMaxWidth(),
            fontWeight = FontWeight.Bold
        )
        OutlinedTextField(
            modifier = Modifier
                .padding(vertical = 5.dp)
                .fillMaxWidth(),
            value = mobileNumber.value,

            onValueChange = {
                if (it.length > 10) {
                    btnEnable.value = mobileNumber.value.isNotEmpty()
                    focusManager.moveFocus(FocusDirection.Down)
                } else {
                    mobileNumber.value = it
                    if(it.length == 10) {
                        btnEnable.value = true
                    }
                }
            },
            label = { Text(text = "Enter mobile Number ") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )


        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                btnEnable.value = false
                focusManager.clearFocus()
                viewModel.sendOTP(mobileNumber.value)
            },
            enabled = (btnEnable.value)
        ) {
            Text(text = "Send")
        }
    }
}

@Composable
fun CommonOtpTextField(
    otp: MutableState<String>,
    focusManager: FocusManager?,
    isLast: Boolean = false
) {
    val max = 1
    OutlinedTextField(
        value = otp.value,
        singleLine = true,
        onValueChange = {
            if (it.length <= max) {
                otp.value = it
                if (!isLast) {
                    focusManager?.moveFocus(FocusDirection.Next)
                }

            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .width(60.dp)
            .height(60.dp),
        maxLines = 1,
        textStyle = LocalTextStyle.current.copy(
            textAlign = TextAlign.Center
        ),

        placeholder = { Text(text = "*", textAlign = TextAlign.Center) },
    )
}


@HiltViewModel
class ForgetPasswordViewModel @Inject constructor(var service: Service) : ViewModel() {
    private val _state = MutableStateFlow<ForgetPWSState>(ForgetPWSState.Empty)
    val state = _state.asStateFlow()
    fun sendOTP(value: String) {
        _state.value = ForgetPWSState.Progress
        viewModelScope.launch(Dispatchers.IO) {
            val forgetPassword = service.forgetPassword(ForgetPasswordOTPReq(value))
            viewModelScope.launch(Dispatchers.Main) {
                if (forgetPassword.isSuccessful) {
                    if (forgetPassword.body() != null && forgetPassword.body()!!.data != null) {
                        _state.value = ForgetPWSState.OTPSend
                    } else {
                        _state.value = ForgetPWSState.Failed
                    }
                }
            }
        }
    }

    fun verifyOTPPassword(mobileNumber:String,otp: String, value: String) {
        _state.value = ForgetPWSState.Progress
        viewModelScope.launch(Dispatchers.IO) {
            val forgetPassword = service.verifyPassword(VerifyPassword(mobileno = mobileNumber,otp = otp, password = value))
            viewModelScope.launch(Dispatchers.Main) {
                if (forgetPassword.isSuccessful) {
                    if (forgetPassword.body() != null) {
                        if (forgetPassword.body()!!.data != null) {
                            _state.value =
                                ForgetPWSState.SuccessFull(forgetPassword.body()!!.data!!)
                        } else {
                            _state.value =
                                ForgetPWSState.FailedSuccess(forgetPassword.body()!!.messages)
                        }
                    }else{
                        _state.value =
                            ForgetPWSState.FailedSuccess(forgetPassword.message())
                    }

                }
            }
        }

    }


}

sealed interface ForgetPWSState {
    object Progress : ForgetPWSState
    object OTPSend : ForgetPWSState
    object Failed : ForgetPWSState
    object Empty : ForgetPWSState
    class SuccessFull(val user:UserModel) : ForgetPWSState
    class FailedSuccess(val error:String) : ForgetPWSState
}


@Composable
@Preview(device = Devices.PIXEL_4_XL, showBackground = true)
fun PrevForget() {
    SarthiTheme {
        //OTPPwsUI(null, null, )
    }

}