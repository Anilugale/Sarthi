package com.vk.sarthi.ui.screen

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.vk.sarthi.R
import com.vk.sarthi.cache.Cache
import com.vk.sarthi.model.DailyVisitModel
import com.vk.sarthi.model.DeleteDailyVisitModel
import com.vk.sarthi.model.PersonsVisited
import com.vk.sarthi.ui.nav.Screens
import com.vk.sarthi.ui.theme.FontColor1
import com.vk.sarthi.ui.theme.FontColor1Dark
import com.vk.sarthi.ui.theme.FontColor2
import com.vk.sarthi.ui.theme.WindowColor
import com.vk.sarthi.viewmodel.DailyVisitStateList
import com.vk.sarthi.viewmodel.DailyVisitVM

@Composable
fun DailyVisitDetailsUI(workID: String, navigatorController: NavHostController?) {
    val dailyModel =  Cache.getDailyVisitModel(workID.toInt())
    val context = LocalContext.current
    val showDialog = remember { mutableStateOf(false) }
    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text(
                    text = LocalContext.current.getString(R.string.daily_visit),
                    fontSize = 18.sp
                )
            },
            navigationIcon = if (navigatorController?.previousBackStackEntry != null) {
                {
                    IconButton(onClick = { navigatorController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            } else {
                null
            },
            actions = {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "Edit",
                    modifier = Modifier
                        .padding(10.dp)
                        .clickable {
                            navigatorController!!.navigate(Screens.AddDailyVisit.route + "/" + dailyModel!!.id)
                        }
                )

                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Delete",
                    modifier = Modifier
                        .padding(10.dp)
                        .clickable {
                            showDialog.value = true
                        }
                )
            }
        )
    },
        backgroundColor = if(isSystemInDarkTheme()) Color.Black else WindowColor) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(vertical = 10.dp, horizontal = 15.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            dailyModel?.apply {

                val titleColor = if (isSystemInDarkTheme()) FontColor1Dark else FontColor1
                Text(
                    text = "Person Visited ",
                    fontWeight = FontWeight.SemiBold,
                    color = titleColor,
                    fontSize = 16.sp
                )
                PersonVisitedDetailsUI(titleColor,dailyModel.persons_visited)
                Divider(
                    Modifier
                        .height(1.dp)
                        .padding(vertical = 5.dp)
                )

                ShowInfo(dailyModel.devinfo,R.string.development_info, titleColor)
                ShowInfo(dailyModel.rashanshopinfo,R.string.ration_info, titleColor)
                ShowInfo(dailyModel.electricityinfo,R.string.electric_info, titleColor)
                ShowInfo(dailyModel.drinkingwaterinfo,R.string.drinking_water_info, titleColor)
                ShowInfo(dailyModel.watercanelinfo,R.string.water_canal_info, titleColor)
                ShowInfo(dailyModel.schoolinfo,R.string.school_info, titleColor)
                ShowInfo(dailyModel.primarycarecenterinfo,R.string.prathamik_info, titleColor)
                ShowInfo(dailyModel.veterinarymedicineinfo,R.string.pashu_info, titleColor)
                ShowInfo(dailyModel.govservantinfo,R.string.gov_emp_info, titleColor)
                ShowInfo(dailyModel.politicalinfo,R.string.politics_info, titleColor)
                ShowInfo(dailyModel.deathpersoninfo,R.string.death_person_info, titleColor)
                ShowInfo(dailyModel.newschemes,R.string.gat_labh_yojna, titleColor)
                ShowInfo(dailyModel.otherinfo,R.string.other_info, titleColor)

            }
        }
    }


    val model: DailyVisitVM = hiltViewModel()
    val viewModel = remember { model }

    val value = viewModel.stateExpose.collectAsState().value

    if (showDialog.value) {
        showDelete(showDialog, dailyModel, model)
    }

    when (value) {
        is DailyVisitStateList.FailedDelete -> {
            Toast.makeText(context,value.msg,Toast.LENGTH_SHORT).show()
        }
        is DailyVisitStateList.Process ->{

        }

        is DailyVisitStateList.SuccessDelete -> {
            Toast.makeText(context,value.msg,Toast.LENGTH_SHORT).show()
            LaunchedEffect(key1 = value){
                navigatorController?.popBackStack()
            }
        }
        else ->{

        }
    }
}

@Composable
fun PersonVisitedDetailsUI(titleColor: Color, personsVisited: List<PersonsVisited>) {
    personsVisited.forEach {
        Card(modifier = Modifier.padding(vertical = 10.dp)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                if (it.name.isNotEmpty()) {
                    Text(
                        text = "Name - ${it.name} ",
                        fontWeight = FontWeight.SemiBold,
                        color = titleColor,
                        fontSize = 14.sp
                    )
                }
                if (it.subject.isNotEmpty()) {
                    Text(
                        text = "Subject - ${it.subject} ",
                        fontWeight = FontWeight.SemiBold,
                        color = titleColor,
                        fontSize = 14.sp
                    )
                }
                if (it.information.isNotEmpty()) {
                    Text(
                        text = "Information - ${it.information} ",
                        fontWeight = FontWeight.SemiBold,
                        color = titleColor,
                        fontSize = 14.sp
                    )
                }
                if (it.servey.isNotEmpty()) {
                    Text(
                        text = "Survey - ${it.servey} ",
                        fontWeight = FontWeight.SemiBold,
                        color = titleColor,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ShowInfo(
    msg: String?,
    placeholderID :Int,
    titleColor: Color
) {

    if (!msg.isNullOrEmpty()) {
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = stringResource(id = placeholderID),
            fontWeight = FontWeight.SemiBold,
            color = FontColor2,
            fontSize = 14.sp
        )

        Text(
            text = msg,
            color = titleColor,
            fontSize = 16.sp
        )
    }
}


@Composable
fun showDelete(
    showDialog: MutableState<Boolean>,
    dailyModel: DailyVisitModel?,
    model: DailyVisitVM
) {
    AlertDialog(
        onDismissRequest = {
            showDialog.value = false
        },
        confirmButton = {
            TextButton(onClick = {
                showDialog.value = false
                model.deleteDailyWork(DeleteDailyVisitModel(visitid = dailyModel!!.id, coordinatorid = Cache.loginUser!!.id))
            })
            { Text(text = stringResource(id = android.R.string.ok)) }
        },
        text = { Text(text = "Do you want to Delete this?") },
        dismissButton = {
            TextButton(onClick = {
                showDialog.value = false
            })
            { Text(text = stringResource(id = android.R.string.cancel)) }
        }
    )
}
