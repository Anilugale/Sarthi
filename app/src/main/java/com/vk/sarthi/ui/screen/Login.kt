package com.vk.sarthi.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.vk.sarthi.R
import com.vk.sarthi.model.LoginReq
import com.vk.sarthi.ui.nav.Screens
import com.vk.sarthi.ui.theme.SarthiTheme
import com.vk.sarthi.utli.SettingPreferences
import com.vk.sarthi.viewmodel.LoginViewModel
import com.vk.sarthi.viewmodel.Status

@Composable
fun LoginScreen(navigator: NavHostController?) {

    val model: LoginViewModel = hiltViewModel()
    val viewModel = remember { model }
    val value1 = viewModel.stateExpose.collectAsState().value
    val context = LocalContext.current
    val userName = remember {
        mutableStateOf("")
    }

    val password = remember {
        mutableStateOf("")
    }
    val error = remember { mutableStateOf("") }

    when (value1) {
        is Status.Process -> ShowLoginProcess()
        is Status.SuccessUser -> {
            LaunchedEffect(key1 =value1 ){
                SettingPreferences.saveUser(value1.user, context)
                navigator?.navigate(Screens.Dashboard.route) {
                    popUpTo(Screens.Login.route) {
                        inclusive = true
                    }
                }
            }

        }
        is Status.ErrorLogin->{
            error.value = value1.error
            LoginUi(navigator, model, userName, password, error)
        }
        else -> LoginUi(navigator,model,userName,password,error)
    }


}

@Composable
fun ShowLoginProcess() {
    Box(modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        CircularProgressIndicator()
    }
}

@Composable
fun LoginUi(
    navigator: NavHostController?,
    model: LoginViewModel,
    userName: MutableState<String>,
    password: MutableState<String>,
    error: MutableState<String>
) {


    val context = LocalContext.current
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

        val scrollState = rememberScrollState()

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .wrapContentHeight()
                .scrollable(scrollState, Orientation.Vertical)
        ) {

            Text(
                text = "Login", fontSize = 24.sp,
                modifier = Modifier
                    .padding(vertical = 5.dp)
                    .fillMaxWidth(),
                fontWeight = FontWeight.Bold
            )
            OutlinedTextField(
                modifier = Modifier
                    .padding(vertical = 5.dp)
                    .fillMaxWidth(),
                value = userName.value,
                onValueChange = { userName.value = it },
                label = { Text(text = "Username") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            OutlinedTextField(
                modifier = Modifier
                    .padding(vertical = 5.dp)
                    .fillMaxWidth(),
                value = password.value,
                onValueChange = { password.value = it },
                label = { Text(text = "Password") },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image = if (passwordVisible)
                        Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff
                    val description = if (passwordVisible) "Hide password" else "Show password"
                    IconButton(onClick = {passwordVisible = !passwordVisible}){
                        Icon(imageVector  = image, description)
                    }
                }
            )



            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val fcmToken = SettingPreferences.getFCMToken(context)
                    model.login(LoginReq(userName.value,password.value,fcmToken))
                },

                enabled = (userName.value.isNotEmpty() && password.value.isNotEmpty())
            ) {
                Text(text = "Login")
            }

            Text(
                text = stringResource(id = R.string.forget_pws),
                modifier = Modifier
                    .padding(5.dp)
                    .align(Alignment.End)
                    .clickable {
                        navigator?.navigate(Screens.ForgetPassword.route)
                    },
                color = MaterialTheme.colors.primary,
                textAlign = TextAlign.End,
                fontSize = 12.sp
            )

            if (error.value.isNotEmpty()) {
                Text(
                    text = error.value, fontSize = 14.sp,
                    color = Color.Red,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(vertical = 5.dp)
                        .fillMaxWidth(),
                    fontWeight = FontWeight.Bold
                )
            }

        }
    }
}



@Composable
@Preview(device = Devices.PIXEL_4_XL, showBackground = true)
fun Prev() {
    SarthiTheme {
        ShowLoginProcess()
    }

}