package com.vk.sarthi.ui.screen

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Attachment
import androidx.compose.material.icons.outlined.RemoveCircle
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.vk.sarthi.BuildConfig
import com.vk.sarthi.R
import com.vk.sarthi.cache.Cache
import com.vk.sarthi.model.DailyVisitModel
import com.vk.sarthi.model.PersonsVisited
import com.vk.sarthi.model.Village
import com.vk.sarthi.ui.theme.FontColor1
import com.vk.sarthi.ui.theme.FontColor1Dark
import com.vk.sarthi.ui.theme.FontColor2
import com.vk.sarthi.utli.*
import com.vk.sarthi.viewmodel.AddDailyVisitVM
import com.vk.sarthi.viewmodel.DailyVisitState
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.*
import java.util.*


@Composable
fun AddDailyVisit(workID: String, navigatorController: NavHostController?) {
    var dailyModel: DailyVisitModel? = null
    if (workID != "0") {
        dailyModel = Cache.getDailyVisitModel(workID.toInt())
    }

    val viewModel: AddDailyVisitVM = hiltViewModel()
    val viewModelRem = remember { viewModel }


    val collectAsState = viewModelRem.stateExpose.collectAsState()

    val current = LocalContext.current

    when (collectAsState.value) {

        is DailyVisitState.Success -> {
            Toast.makeText(
                current,
                (collectAsState.value as DailyVisitState.Success).msg,
                Toast.LENGTH_SHORT
            ).show()
            LaunchedEffect(key1 = collectAsState.value) {
                navigatorController?.popBackStack()
            }

        }
        DailyVisitState.Process -> {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is DailyVisitState.Failed -> {
            current.toast((collectAsState.value as DailyVisitState.Failed).msg)
        }
        else -> {

        }
    }

    Scaffold(topBar = {
        TopAppBar(title = {
            Text(
                text = LocalContext.current.getString(R.string.visit_Detail),
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )
        }, navigationIcon = if (navigatorController?.previousBackStackEntry != null) {
            {
                IconButton(onClick = { navigatorController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack, contentDescription = "Back"
                    )
                }
            }
        } else {
            null
        })
    }) {
        val personVisitedList = remember { mutableStateListOf(PersonVisitedModel("1")) }
        if (dailyModel != null) {
            personVisitedList.clear()
            dailyModel.persons_visited.forEachIndexed { index, user ->
                val personVisitedModel = PersonVisitedModel(index.inc().toString())
                personVisitedModel.name.value = user.name
                personVisitedModel.subject.value = user.subject
                personVisitedModel.survey.value = user.servey
                personVisitedModel.information.value = user.information
                personVisitedList.add(personVisitedModel)
            }
        }

        var currentFile by remember { mutableStateOf<Uri?>(null) }
        var SelectionFileType by remember { mutableStateOf("") }

        var devFile by remember { mutableStateOf<File?>(null) }
        var rationInfoFile by remember { mutableStateOf<File?>(null) }
        var electricInfoFile by remember { mutableStateOf<File?>(null) }
        var drinkingInfoFile by remember { mutableStateOf<File?>(null) }
        var waterCanalInfoFile by remember { mutableStateOf<File?>(null) }
        var schoolInfoFile by remember { mutableStateOf<File?>(null) }
        var primaryHealthInfoFile by remember { mutableStateOf<File?>(null) }
        var vetarnityHealthInfoFile by remember { mutableStateOf<File?>(null) }
        var govInfoInfoFile by remember { mutableStateOf<File?>(null) }
        var politicalInfoFile by remember { mutableStateOf<File?>(null) }
        var deathPersonInfoFile by remember { mutableStateOf<File?>(null) }
        var birthdayInfoFile by remember { mutableStateOf<File?>(null) }
        var yojnaInfoFile by remember { mutableStateOf<File?>(null) }
        var otherInfoFile by remember { mutableStateOf<File?>(null) }


        val uCropLauncher = rememberLauncherForActivityResult(SquireCropImage()) { uri ->
            currentFile = uri
            uri?.apply {
                // upload file
                if (SelectionFileType == Constants.DEV_INFO) {
                    devFile = File(uri.path)
                    Log.d("@@", "dev info file : $devFile")
                } else if (Constants.RASATION_INFO == SelectionFileType) {
                    rationInfoFile = File(uri.path)
                    Log.d("@@", "dev info file : $devFile")
                }

                when (SelectionFileType) {
                    Constants.DEV_INFO -> {
                        devFile = File(uri.path)
                    }
                    Constants.RASATION_INFO -> {
                        rationInfoFile = File(uri.path)
                    }
                    Constants.ELECTRIC_INFO -> {
                        electricInfoFile = File(uri.path)
                    }
                    Constants.DRINKING_WATER -> {
                        drinkingInfoFile = File(uri.path)
                    }
                    Constants.WATER_CANAL -> {
                        waterCanalInfoFile = File(uri.path)
                    }

                    Constants.SCHOOL_INFO -> {
                        schoolInfoFile = File(uri.path)
                    }

                    Constants.PRIMARAY_HELATH -> {
                        primaryHealthInfoFile = File(uri.path)
                    }

                    Constants.VETARNARY_HEALTH -> {
                        vetarnityHealthInfoFile = File(uri.path)
                    }
                    Constants.GOV_INFO -> {
                        govInfoInfoFile = File(uri.path)
                    }
                    Constants.POLITICAL_INFO -> {
                        politicalInfoFile = File(uri.path)
                    }

                    Constants.DEATH_PERSON_INFO -> {
                        deathPersonInfoFile = File(uri.path)
                    }
                    Constants.BIRTHDAY_INFO -> {
                        birthdayInfoFile = File(uri.path)
                    }

                    Constants.YOJNA_INFO_INFO -> {
                        yojnaInfoFile = File(uri.path)
                    }

                    Constants.OTHER_INFO -> {
                        otherInfoFile = File(uri.path)
                    }
                    else -> {}
                }


            }
        }
        val cameraLauncher =
            rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
                if (success) {
                    currentFile?.let { uri ->
                        uCropLauncher.launch(
                            Pair(
                                first = uri,
                                second = Uri.fromFile(
                                    File(current.cacheDir, "temp_image_file_${Date().time}")
                                )
                            )
                        )
                    }
                } else {
                    current.toast("Cannot save the image!")
                }
            }

        val developmentInfo =
            remember { mutableStateOf(if (dailyModel?.devinfo != null) dailyModel.devinfo else "") }

        val rationShopInfo =
            remember { mutableStateOf(if (dailyModel?.rashanshopinfo != null) dailyModel.rashanshopinfo else "") }
        val electricInfo =
            remember { mutableStateOf(if (dailyModel?.electricityinfo != null) dailyModel.electricityinfo else "") }
        val drinkingWaterInfo =
            remember { mutableStateOf(if (dailyModel?.drinkingwaterinfo != null) dailyModel.drinkingwaterinfo else "") }
        val deathPersonInfo =
            remember { mutableStateOf(if (dailyModel?.deathpersoninfo != null) dailyModel.deathpersoninfo else "") }
        val waterCanalInfo =
            remember { mutableStateOf(if (dailyModel?.watercanelinfo != null) dailyModel.watercanelinfo else "") }
        val schoolInfo =
            remember { mutableStateOf(if (dailyModel?.schoolinfo != null) dailyModel.schoolinfo else "") }
        val prathamikInfo =
            remember { mutableStateOf(if (dailyModel?.primarycarecenterinfo != null) dailyModel.primarycarecenterinfo else "") }
        val pashuInfo =
            remember { mutableStateOf(if (dailyModel?.veterinarymedicineinfo != null) dailyModel.veterinarymedicineinfo else "") }
        val govEmpInfo =
            remember { mutableStateOf(if (dailyModel?.govservantinfo != null) dailyModel.govservantinfo else "") }
        val politicsInfo =
            remember { mutableStateOf(if (dailyModel?.politicalinfo != null) dailyModel.politicalinfo else "") }
        val birthdayInfo =
            remember { mutableStateOf(if (dailyModel?.birthdayinfo != null) dailyModel.birthdayinfo else "") }
        val gatLabhYojna =
            remember { mutableStateOf(if (dailyModel?.newschemes != null) dailyModel.newschemes else "") }
        val otherInfo =
            remember { mutableStateOf(if (dailyModel?.otherinfo != null) dailyModel.otherinfo else "") }

        val labelColor = if (isSystemInDarkTheme()) {
            FontColor1Dark
        } else {
            FontColor1
        }
        Column(
            modifier = Modifier
                .padding(start = 10.dp, end = 10.dp)
                .padding(it)
                .verticalScroll(rememberScrollState())
        ) {
            var selectedVillage: Village? = null
            if (dailyModel != null && Cache.villageData != null) {
                selectedVillage =
                    Cache.villageData!!.villages.single { dailyModel.villageid == it.id }
            }

            var ganName by remember { mutableStateOf(selectedVillage?.gan ?: "") }
            var villageName by remember { mutableStateOf(selectedVillage) }

            val list = Cache.villageData!!.villages.groupBy { it.gan }
            val gatList = list.keys

            DropDownSpinner(
                modifier = Modifier.padding(10.dp),
                defaultText = "गण निवडा",
                selectedItem = ganName,
                onItemSelected = { _, item ->
                    ganName = item
                    villageName = null
                },
                itemList = gatList.toList()
            )

            DropDownSpinner(
                modifier = Modifier.padding(10.dp),
                defaultText = "गाव निवडा",
                selectedItem = villageName,
                onItemSelected = { _, item ->
                    villageName = item
                },
                itemList = list[ganName]
            )





            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.person_visited),
                    modifier = Modifier
                        .padding(10.dp)
                        .align(Alignment.CenterStart),
                    fontWeight = FontWeight.SemiBold,
                    color = labelColor
                )

                if (dailyModel == null) {
                    Icon(Icons.Outlined.AddCircle,
                        contentDescription = "",
                        tint = labelColor,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(10.dp)
                            .clickable {
                                val inc = personVisitedList.size.inc()
                                personVisitedList.add(PersonVisitedModel(inc.toString()))
                            }

                    )
                }

            }


            personVisitedList.forEach {model->
                PersonVisitedUI(model = model, labelColor, personVisitedList, (dailyModel == null))
            }


            OutlinedTextField(
                value = developmentInfo.value,
                onValueChange = { developmentInfo.value = it },
                label = { Text(text = stringResource(R.string.development_info)) },
                modifier = Modifier.fillMaxWidth()
            )

            Row {
                if (developmentInfo.value.isNotEmpty()) {
                    Button(onClick = {
                        val newPhotoUri = current.createImageFile().getUriForFile(current)
                        currentFile = newPhotoUri
                        SelectionFileType = Constants.DEV_INFO
                        cameraLauncher.launch(newPhotoUri)
                    }) {
                        Text(text = stringResource(R.string.development_info) + " photo")
                    }
                }
                if(devFile!=null) {
                    Icon(Icons.Outlined.Attachment,
                        contentDescription = "",
                        tint = labelColor,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(10.dp)
                            .clickable {
                                showImage(devFile!!, current)
                            }
                    )
                }
            }





            OutlinedTextField(
                value = rationShopInfo.value,
                onValueChange = { rationShopInfo.value = it },
                label = { Text(text = stringResource(R.string.ration_info)) },
                modifier = Modifier.fillMaxWidth()
            )
            Row {
                if (rationShopInfo.value.isNotEmpty()) {
                    Button(onClick = {
                        val newPhotoUri = current.createImageFile().getUriForFile(current)
                        currentFile = newPhotoUri
                        SelectionFileType = Constants.RASATION_INFO
                        cameraLauncher.launch(newPhotoUri)
                    }) {
                        Text(text = stringResource(R.string.ration_info) + " photo")
                    }
                }
                if (rationInfoFile != null) {
                    Icon(Icons.Outlined.Attachment,
                        contentDescription = "",
                        tint = labelColor,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(10.dp)
                            .clickable {
                                showImage(rationInfoFile!!, current)
                            }
                    )
                }
            }
            OutlinedTextField(
                value = electricInfo.value,
                onValueChange = { electricInfo.value = it },
                label = { Text(text = stringResource(R.string.electric_info)) },
                modifier = Modifier.fillMaxWidth()
            )
            Row {
            if (electricInfo.value.isNotEmpty()) {
                Button(onClick = {
                    val newPhotoUri = current.createImageFile().getUriForFile(current)
                    currentFile = newPhotoUri
                    SelectionFileType = Constants.ELECTRIC_INFO
                    cameraLauncher.launch(newPhotoUri)
                }) {
                    Text(text = stringResource(R.string.electric_info) + " photo")

                }
            }
            if (electricInfoFile != null) {
                Icon(Icons.Outlined.Attachment,
                    contentDescription = "",
                    tint = labelColor,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(10.dp)
                        .clickable {
                            showImage(electricInfoFile!!, current)
                        }
                )
            }
        }

            OutlinedTextField(
                value = drinkingWaterInfo.value,
                onValueChange = { drinkingWaterInfo.value = it },
                label = { Text(text = stringResource(R.string.drinking_water_info)) },
                modifier = Modifier.fillMaxWidth()
            )
            Row {
            if (drinkingWaterInfo.value.isNotEmpty()) {
                Button(onClick = {
                    val newPhotoUri = current.createImageFile().getUriForFile(current)
                    currentFile = newPhotoUri
                    SelectionFileType = Constants.DRINKING_WATER
                    cameraLauncher.launch(newPhotoUri)
                }) {
                    Text(text = stringResource(R.string.drinking_water_info) + " photo")

                }
            }
            if (drinkingInfoFile != null) {
                Icon(Icons.Outlined.Attachment,
                    contentDescription = "",
                    tint = labelColor,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(10.dp)
                        .clickable {
                            showImage(drinkingInfoFile!!, current)
                        }
                )
            }
        }



            OutlinedTextField(
                value = waterCanalInfo.value,
                onValueChange = { waterCanalInfo.value = it },
                label = { Text(text = stringResource(R.string.water_canal_info)) },
                modifier = Modifier.fillMaxWidth()
            )
            Row {
            if (waterCanalInfo.value.isNotEmpty()) {
                Button(onClick = {
                    val newPhotoUri = current.createImageFile().getUriForFile(current)
                    currentFile = newPhotoUri
                    SelectionFileType = Constants.WATER_CANAL
                    cameraLauncher.launch(newPhotoUri)
                }) {
                    Text(text = stringResource(R.string.water_canal_info) + " photo")

                }
            }
                if (waterCanalInfoFile != null) {
                    Icon(Icons.Outlined.Attachment,
                        contentDescription = "",
                        tint = labelColor,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(10.dp)
                            .clickable {
                                showImage(waterCanalInfoFile!!, current)
                            }
                    )
                }
            }


            OutlinedTextField(
                value = schoolInfo.value,
                onValueChange = { schoolInfo.value = it },
                label = { Text(text = stringResource(R.string.school_info)) },
                modifier = Modifier.fillMaxWidth()
            )
            Row {
            if (schoolInfo.value.isNotEmpty()) {
                Button(onClick = {
                    val newPhotoUri = current.createImageFile().getUriForFile(current)
                    currentFile = newPhotoUri
                    SelectionFileType = Constants.SCHOOL_INFO
                    cameraLauncher.launch(newPhotoUri)
                }) {
                    Text(text = stringResource(R.string.school_info) + " photo")

                }
            }
            if (schoolInfoFile != null) {
                Icon(Icons.Outlined.Attachment,
                    contentDescription = "",
                    tint = labelColor,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(10.dp)
                        .clickable {
                            showImage(schoolInfoFile!!, current)
                        }
                )
            }
        }

            OutlinedTextField(
                value = prathamikInfo.value,
                onValueChange = { prathamikInfo.value = it },
                label = { Text(text = stringResource(R.string.prathamik_info)) },
                modifier = Modifier.fillMaxWidth()
            )
            Row {
            if (prathamikInfo.value.isNotEmpty()) {
                Button(onClick = {
                    val newPhotoUri = current.createImageFile().getUriForFile(current)
                    currentFile = newPhotoUri
                    SelectionFileType = Constants.PRIMARAY_HELATH
                    cameraLauncher.launch(newPhotoUri)
                }) {
                    Text(text = stringResource(R.string.prathamik_info) + " photo")
                }
            }
            if (primaryHealthInfoFile != null) {
                Icon(Icons.Outlined.Attachment,
                    contentDescription = "",
                    tint = labelColor,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(10.dp)
                        .clickable {
                            showImage(primaryHealthInfoFile!!, current)
                        }
                )
            }
        }
            OutlinedTextField(
                value = pashuInfo.value,
                onValueChange = { pashuInfo.value = it },
                label = { Text(text = stringResource(R.string.pashu_info)) },
                modifier = Modifier.fillMaxWidth()
            )
            Row {
            if (pashuInfo.value.isNotEmpty()) {
                Button(onClick = {
                    val newPhotoUri = current.createImageFile().getUriForFile(current)
                    currentFile = newPhotoUri
                    SelectionFileType = Constants.VETARNARY_HEALTH
                    cameraLauncher.launch(newPhotoUri)
                }) {
                    Text(text = stringResource(R.string.pashu_info) + " Photo")
                }
            }
            if (vetarnityHealthInfoFile != null) {
                Icon(Icons.Outlined.Attachment,
                    contentDescription = "",
                    tint = labelColor,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(10.dp)
                        .clickable {
                            showImage(vetarnityHealthInfoFile!!, current)
                        }
                )
            }
        }

            OutlinedTextField(
                value = govEmpInfo.value,
                onValueChange = { govEmpInfo.value = it },
                label = { Text(text = stringResource(R.string.gov_emp_info)) },
                modifier = Modifier.fillMaxWidth()
            )
            Row {
            if (govEmpInfo.value.isNotEmpty()) {
                Button(onClick = {
                    val newPhotoUri = current.createImageFile().getUriForFile(current)
                    currentFile = newPhotoUri
                    SelectionFileType = Constants.GOV_INFO
                    cameraLauncher.launch(newPhotoUri)
                }) {
                    Text(text = stringResource(R.string.gov_emp_info) + " Photo")
                }
            }
            if (govInfoInfoFile != null) {
                Icon(Icons.Outlined.Attachment,
                    contentDescription = "",
                    tint = labelColor,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(10.dp)
                        .clickable {
                            showImage(govInfoInfoFile!!, current)
                        }
                )
            }
        }

            OutlinedTextField(
                value = politicsInfo.value,
                onValueChange = { politicsInfo.value = it },
                label = { Text(text = stringResource(R.string.politics_info)) },
                modifier = Modifier.fillMaxWidth()
            )

            Row {
                if (politicsInfo.value.isNotEmpty()) {
                    Button(onClick = {
                        val newPhotoUri = current.createImageFile().getUriForFile(current)
                        currentFile = newPhotoUri
                        SelectionFileType = Constants.POLITICAL_INFO
                        cameraLauncher.launch(newPhotoUri)
                    }) {
                        Text(text = stringResource(R.string.gov_emp_info) + " Photo")
                    }
                }
                if (politicalInfoFile != null) {
                    Icon(Icons.Outlined.Attachment,
                        contentDescription = "",
                        tint = labelColor,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(10.dp)
                            .clickable {
                                showImage(politicalInfoFile!!, current)
                            }
                    )
                }
            }
            Row {
                OutlinedTextField(
                    value = deathPersonInfo.value,
                    onValueChange = { deathPersonInfo.value = it },
                    label = { Text(text = stringResource(R.string.death_person_info)) },
                    modifier = Modifier.fillMaxWidth()
                )

                if (deathPersonInfo.value.isNotEmpty()) {
                    Button(onClick = {
                        val newPhotoUri = current.createImageFile().getUriForFile(current)
                        currentFile = newPhotoUri
                        SelectionFileType = Constants.DEATH_PERSON_INFO
                        cameraLauncher.launch(newPhotoUri)
                    }) {
                        Text(text = stringResource(R.string.death_person_info) + " Photo")
                    }
                }
                if (deathPersonInfoFile != null) {
                    Icon(Icons.Outlined.Attachment,
                        contentDescription = "",
                        tint = labelColor,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(10.dp)
                            .clickable {
                                showImage(deathPersonInfoFile!!, current)
                            }
                    )
                }
            }

            OutlinedTextField(
                value = birthdayInfo.value,
                onValueChange = { birthdayInfo.value = it },
                label = { Text(text = stringResource(R.string.birthday_info)) },
                modifier = Modifier.fillMaxWidth()
            )
            Row {
                if (birthdayInfo.value.isNotEmpty()) {
                    Button(onClick = {
                        val newPhotoUri = current.createImageFile().getUriForFile(current)
                        currentFile = newPhotoUri
                        SelectionFileType = Constants.BIRTHDAY_INFO
                        cameraLauncher.launch(newPhotoUri)
                    }) {
                        Text(text = stringResource(R.string.birthday_info) + " Photo")
                    }
                }
                if (birthdayInfoFile != null) {
                    Icon(Icons.Outlined.Attachment,
                        contentDescription = "",
                        tint = labelColor,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(10.dp)
                            .clickable {
                                showImage(birthdayInfoFile!!, current)
                            }
                    )
                }
            }

            OutlinedTextField(
                value = gatLabhYojna.value,
                onValueChange = { gatLabhYojna.value = it },
                label = { Text(text = stringResource(R.string.gat_labh_yojna)) },
                modifier = Modifier.fillMaxWidth()
            )
            Row {
                if (gatLabhYojna.value.isNotEmpty()) {
                    Button(onClick = {
                        val newPhotoUri = current.createImageFile().getUriForFile(current)
                        currentFile = newPhotoUri
                        SelectionFileType = Constants.YOJNA_INFO_INFO
                        cameraLauncher.launch(newPhotoUri)
                    }) {
                        Text(text = stringResource(R.string.gat_labh_yojna) + " Photo")
                    }
                }
                if (yojnaInfoFile != null) {
                    Icon(Icons.Outlined.Attachment,
                        contentDescription = "",
                        tint = labelColor,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(10.dp)
                            .clickable {
                                showImage(yojnaInfoFile!!, current)
                            }
                    )
                }
            }

            OutlinedTextField(
                value = otherInfo.value,
                onValueChange = { otherInfo.value = it },
                label = { Text(text = stringResource(R.string.other_info)) },
                modifier = Modifier.fillMaxWidth(),

                )
            Row {
            if (otherInfo.value.isNotEmpty()) {
                Button(onClick = {
                    val newPhotoUri = current.createImageFile().getUriForFile(current)
                    currentFile = newPhotoUri
                    SelectionFileType = Constants.OTHER_INFO
                    cameraLauncher.launch(newPhotoUri)
                }) {
                    Text(text = stringResource(R.string.other_info) + " Photo")
                }
            }
            if (otherInfoFile != null) {
                Icon(Icons.Outlined.Attachment,
                    contentDescription = "",
                    tint = labelColor,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(10.dp)
                        .clickable {
                            showImage(otherInfoFile!!, current)
                        }
                )
            }
        }


            Button(
                onClick = {
                    if (ganName.isEmpty()) {
                        Toast.makeText(current, "Select Village", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    val personalList = arrayListOf<PersonsVisited>()
                    personVisitedList.forEach {
                        Log.d("@@", "AddDailyVisit: $it")
                        resetErrorFlag(it)
                        if (it.name.value.isNotEmpty() || it.subject.value.isNotEmpty() || it.information.value.isNotEmpty() || it.survey.value.isNotEmpty()) {
                            if (it.name.value.isEmpty()) {
                                it.isN.value = true
                                return@Button
                            } else if (it.subject.value.isEmpty()) {
                                it.isS.value = true
                                return@Button
                            } else if (it.information.value.isEmpty()) {
                                it.isIn.value = true
                                return@Button
                            } else if (it.survey.value.isEmpty()) {
                                it.isSu.value = true
                                return@Button
                            } else {
                                personalList.add(
                                    PersonsVisited(
                                        information = it.information.value,
                                        name = it.name.value,
                                        servey = it.survey.value,
                                        subject = it.subject.value,
                                    )
                                )
                            }
                        }
                    }
                    val mPref = SettingPreferences.get(current)

                    val locationStr = mPref.getString(Constants.LOCATION, "")
                    var latitude = ""
                    var longitude = ""
                    if (locationStr != null) {
                        if (locationStr.isNotEmpty()) {
                            val split = locationStr.split("|")
                            latitude = split[0]
                            longitude = split[1]
                        }
                    }

                    val map: HashMap<String, RequestBody> = HashMap()
                    var birthdayFileBody: MultipartBody.Part? = null
                    var rashanshopinfoBody: MultipartBody.Part? = null
                    var electricInfoFileBody: MultipartBody.Part? = null
                    var drinkingwaterinfofileBody: MultipartBody.Part? = null
                    var watercanelinfofileBody: MultipartBody.Part? = null
                    var schoolinfofileBody: MultipartBody.Part? = null
                    var primaryHealthInfoFileBody: MultipartBody.Part? = null
                    var vetarnityHealthInfoFileBody: MultipartBody.Part? = null
                    var govInfoInfoFileBody: MultipartBody.Part? = null
                    var politicalInfoFileBody: MultipartBody.Part? = null
                    var deathPersonInfoFileBody: MultipartBody.Part? = null
                    var newschemesfileBody: MultipartBody.Part? = null
                    var devinfofileBody: MultipartBody.Part? = null
                    var otherInfoFileBody: MultipartBody.Part? = null
                    map["coordinator_id"] = Cache.loginUser!!.id.toString()
                        .toRequestBody("text/plain".toMediaTypeOrNull())
                    map["villageid"] =
                        villageName!!.id.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                    map["persons_visited"] =
                        Gson().toJson(personalList).toRequestBody("text/plain".toMediaTypeOrNull())
                    if (dailyModel != null) {
                        map["visitid"] =
                            dailyModel.id.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                    }
                    /* if (birthdayInfo.value.isNotEmpty() && birthdayInfoFile == null && dailyModel == null) {
                         current.toast("Photo Mandatory")
                     } else*/ if (rationShopInfo.value.isNotEmpty() && rationInfoFile == null && dailyModel == null) {
                    current.toast("Photo Mandatory")
                } else if (electricInfo.value.isNotEmpty() && electricInfoFile == null && dailyModel == null) {
                    current.toast("Photo Mandatory")
                } /* else if (drinkingWaterInfo.value.isNotEmpty() && drinkingInfoFile == null && dailyModel == null) {
                        current.toast("Photo Mandatory")
                    } */ else if (waterCanalInfo.value.isNotEmpty() && waterCanalInfoFile == null && dailyModel == null) {
                    current.toast("Photo Mandatory")
                } else if (schoolInfo.value.isNotEmpty() && schoolInfoFile == null && dailyModel == null) {
                    current.toast("Photo Mandatory")
                } else if (prathamikInfo.value.isNotEmpty() && primaryHealthInfoFile == null && dailyModel == null) {
                    current.toast("Photo Mandatory")
                }/*  else if (pashuInfo.value.isNotEmpty() && vetarnityHealthInfoFile == null && dailyModel == null) {
                        current.toast("Photo Mandatory")
                    } else if (govEmpInfo.value.isNotEmpty() && govInfoInfoFile == null) {
                        current.toast("Photo Mandatory")
                    }   else if (politicsInfo.value.isNotEmpty() && politicalInfoFile == null && dailyModel == null) {
                        current.toast("Photo Mandatory")
                    }  else if (deathPersonInfo.value.isNotEmpty() && deathPersonInfoFile == null && dailyModel == null) {
                        current.toast("Photo Mandatory")
                    }   else if (gatLabhYojna.value.isNotEmpty() && yojnaInfoFile == null && dailyModel == null) {
                        current.toast("Photo Mandatory")
                    }     else if (developmentInfo.value.isNotEmpty() && devFile == null && dailyModel == null) {
                        current.toast("Photo Mandatory")
                    }     else if (otherInfo.value.isNotEmpty() && otherInfoFile == null && dailyModel == null) {
                        current.toast("Photo Mandatory")
                    }*/ else {
                    map["birthdayinfo"] =
                        birthdayInfo.value.toRequestBody("text/plain".toMediaTypeOrNull())
                    if (birthdayInfoFile != null) {
                        val requestFile =
                            birthdayInfoFile!!.asRequestBody("image/jpg".toMediaType())
                        birthdayFileBody = MultipartBody.Part.createFormData(
                            "birthdayinfofile",
                            birthdayInfoFile!!.name,
                            requestFile
                        )
                    }
                    map["rashanshopinfo"] =
                        rationShopInfo.value.toRequestBody("text/plain".toMediaTypeOrNull())
                    if (rationInfoFile != null) {
                        val requestFile = rationInfoFile!!.asRequestBody("image/jpg".toMediaType())
                        rashanshopinfoBody = MultipartBody.Part.createFormData(
                            "rashanshopinfofile",
                            rationInfoFile!!.name,
                            requestFile
                        )
                    }

                    map["electricityinfo"] =
                        electricInfo.value.toRequestBody("text/plain".toMediaTypeOrNull())
                    if (electricInfoFile != null) {
                        val requestFile =
                            electricInfoFile!!.asRequestBody("image/jpg".toMediaType())
                        rashanshopinfoBody = MultipartBody.Part.createFormData(
                            "electricityinfofile",
                            electricInfoFile!!.name,
                            requestFile
                        )
                    }

                    map["drinkingwaterinfo"] =
                        drinkingWaterInfo.value.toRequestBody("text/plain".toMediaTypeOrNull())
                    if (drinkingInfoFile != null) {
                        val requestFile =
                            drinkingInfoFile!!.asRequestBody("image/jpg".toMediaType())
                        drinkingwaterinfofileBody = MultipartBody.Part.createFormData(
                            "drinkingwaterinfofile",
                            drinkingInfoFile!!.name,
                            requestFile
                        )
                    }

                    map["watercanelinfo"] =
                        waterCanalInfo.value.toRequestBody("text/plain".toMediaTypeOrNull())
                    if (waterCanalInfoFile != null) {
                        val requestFile =
                            waterCanalInfoFile!!.asRequestBody("image/jpg".toMediaType())
                        watercanelinfofileBody = MultipartBody.Part.createFormData(
                            "watercanelinfofile",
                            waterCanalInfoFile!!.name,
                            requestFile
                        )
                    }

                    map["schoolinfo"] =
                        schoolInfo.value.toRequestBody("text/plain".toMediaTypeOrNull())
                    if (schoolInfoFile != null) {
                        val requestFile = schoolInfoFile!!.asRequestBody("image/jpg".toMediaType())
                        schoolinfofileBody = MultipartBody.Part.createFormData(
                            "schoolinfofile",
                            schoolInfoFile!!.name,
                            requestFile
                        )
                    }

                    map["primarycarecenterinfo"] =
                        prathamikInfo.value.toRequestBody("text/plain".toMediaTypeOrNull())
                    if (primaryHealthInfoFile != null) {
                        val requestFile =
                            primaryHealthInfoFile!!.asRequestBody("image/jpg".toMediaType())
                        primaryHealthInfoFileBody = MultipartBody.Part.createFormData(
                            "primarycarecenterinfofile",
                            primaryHealthInfoFile!!.name,
                            requestFile
                        )
                    }

                    map["veterinarymedicineinfo"] =
                        pashuInfo.value.toRequestBody("text/plain".toMediaTypeOrNull())
                    if (vetarnityHealthInfoFile != null) {
                        val requestFile =
                            vetarnityHealthInfoFile!!.asRequestBody("image/jpg".toMediaType())
                        vetarnityHealthInfoFileBody = MultipartBody.Part.createFormData(
                            "veterinarymedicineinfoinfo",
                            vetarnityHealthInfoFile!!.name,
                            requestFile
                        )
                    }

                    map["govservantinfo"] =
                        govEmpInfo.value.toRequestBody("text/plain".toMediaTypeOrNull())
                    if (govInfoInfoFile != null) {
                        val requestFile = govInfoInfoFile!!.asRequestBody("image/jpg".toMediaType())
                        govInfoInfoFileBody = MultipartBody.Part.createFormData(
                            "govservantinfofile",
                            govInfoInfoFile!!.name,
                            requestFile
                        )
                    }


                    map["politicalinfo"] =
                        politicsInfo.value.toRequestBody("text/plain".toMediaTypeOrNull())
                    if (politicalInfoFile != null) {
                        val requestFile =
                            politicalInfoFile!!.asRequestBody("image/jpg".toMediaType())
                        politicalInfoFileBody = MultipartBody.Part.createFormData(
                            "politicalinfofile",
                            politicalInfoFile!!.name,
                            requestFile
                        )
                    }

                    map["deathpersoninfo"] =
                        deathPersonInfo.value.toRequestBody("text/plain".toMediaTypeOrNull())
                    if (deathPersonInfoFile != null) {
                        val requestFile =
                            deathPersonInfoFile!!.asRequestBody("image/jpg".toMediaType())
                        deathPersonInfoFileBody = MultipartBody.Part.createFormData(
                            "deathpersoninfofile",
                            deathPersonInfoFile!!.name,
                            requestFile
                        )
                    }
                    map["newschemes"] =
                        gatLabhYojna.value.toRequestBody("text/plain".toMediaTypeOrNull())
                    if (yojnaInfoFile != null) {
                        val requestFile = yojnaInfoFile!!.asRequestBody("image/jpg".toMediaType())
                        newschemesfileBody = MultipartBody.Part.createFormData(
                            "newschemesfile",
                            yojnaInfoFile!!.name,
                            requestFile
                        )
                    }
                    map["devinfo"] =
                        developmentInfo.value.toRequestBody("text/plain".toMediaTypeOrNull())
                    if (devFile != null) {
                        val requestFile = devFile!!.asRequestBody("image/jpg".toMediaType())
                        devinfofileBody = MultipartBody.Part.createFormData(
                            "devinfofile",
                            devFile!!.name,
                            requestFile
                        )
                    }
                    map["otherinfo"] =
                        otherInfo.value.toRequestBody("text/plain".toMediaTypeOrNull())

                    if (otherInfoFile != null) {
                        val requestFile = otherInfoFile!!.asRequestBody("image/jpg".toMediaType())
                        otherInfoFileBody = MultipartBody.Part.createFormData(
                            "otherinfofile",
                            otherInfoFile!!.name,
                            requestFile
                        )
                    }

                    val isEmpty = otherInfo.value.isEmpty() &&
                            developmentInfo.value.isEmpty() &&
                            gatLabhYojna.value.isEmpty() &&
                            deathPersonInfo.value.isEmpty() &&
                            politicsInfo.value.isEmpty() &&
                            govEmpInfo.value.isEmpty() &&
                            pashuInfo.value.isEmpty() &&
                            prathamikInfo.value.isEmpty() &&
                            schoolInfo.value.isEmpty() &&
                            waterCanalInfo.value.isEmpty() &&
                            drinkingWaterInfo.value.isEmpty() &&
                            electricInfo.value.isEmpty() &&
                            rationShopInfo.value.isEmpty() &&
                            birthdayInfo.value.isEmpty()


                    if (!isEmpty || personalList.isNotEmpty()) {
                        map["latitude"] =
                            latitude.toRequestBody("text/plain".toMediaTypeOrNull())
                        map["longitude"] =
                            longitude.toRequestBody("text/plain".toMediaTypeOrNull())

                        viewModel.setDailyVisitReq(
                            dailyModel?.id ?: 0, map,
                            birthdayFileBody,
                            rashanshopinfoBody,
                            electricInfoFileBody,
                            drinkingwaterinfofileBody,
                            watercanelinfofileBody,
                            schoolinfofileBody,
                            primaryHealthInfoFileBody,
                            vetarnityHealthInfoFileBody,
                            govInfoInfoFileBody,
                            politicalInfoFileBody,
                            deathPersonInfoFileBody,
                            newschemesfileBody,
                            devinfofileBody,
                            otherInfoFileBody
                        )
                    } else {
                        current.toast("कृपया किमान 1 कार्य पूर्ण करा!")
                    }
                }


                }, modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 15.dp, horizontal = 10.dp)
            ) {
                Text(text = stringResource(R.string.submit))
            }
        }


    }


}

fun showImage(devFile: File, current: Context) {
    val uriForFile =
        FileProvider.getUriForFile(current, BuildConfig.APPLICATION_ID + ".fileprovider", devFile)

    val intent = Intent(Intent.ACTION_VIEW)
        .setDataAndType(uriForFile,
            "image/*"
        ).addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    current.startActivity(intent)
}

@Composable
fun showToast(current: Context) {
    current.toast("Photo Mandatory")
}

fun resetErrorFlag(it: PersonVisitedModel) {
    it.isN.value = false
    it.isS.value = false
    it.isSu.value = false
    it.isIn.value = false
}

@Composable
fun PersonVisitedUI(
    model: PersonVisitedModel,
    labelColor: Color,
    personVisitedList: SnapshotStateList<PersonVisitedModel>,
    isAddNew: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 10.dp)
    ) {
        if (isAddNew) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Person ${model.id}")
                if (model.id != "1") {
                    Icon(Icons.Outlined.RemoveCircle,
                        contentDescription = "",
                        tint = labelColor,
                        modifier = Modifier
                            .padding(10.dp)
                            .clickable {
                                personVisitedList.remove(model)
                            }

                    )
                }
            }
        }




        OutlinedTextField(
            value = model.name.value,
            onValueChange = { model.name.value = it },
            label = { Text(text = stringResource(R.string.name)) },
            modifier = Modifier.fillMaxWidth(),
            isError = model.isN.value,

            )
        OutlinedTextField(
            value = model.subject.value,
            onValueChange = { model.subject.value = it },
            label = { Text(text = stringResource(R.string.subject)) },
            modifier = Modifier.fillMaxWidth(),
            isError = model.isS.value
        )
        OutlinedTextField(
            value = model.information.value,
            onValueChange = { model.information.value = it },
            label = { Text(text = stringResource(R.string.info)) },
            modifier = Modifier.fillMaxWidth(),
            isError = model.isIn.value
        )
        OutlinedTextField(
            value = model.survey.value,
            onValueChange = { model.survey.value = it },
            label = { Text(text = stringResource(R.string.survey)) },
            modifier = Modifier.fillMaxWidth(),
            isError = model.isSu.value
        )
        Divider(color = FontColor2, modifier = Modifier.padding(vertical = 10.dp))
    }
}

class PersonVisitedModel(val id: String) {
    var name = mutableStateOf("")
    var subject = mutableStateOf("")
    var information = mutableStateOf("")
    var survey = mutableStateOf("")

    var isN = mutableStateOf(false)
    var isS = mutableStateOf(false)
    var isIn = mutableStateOf(false)
    var isSu = mutableStateOf(false)
}


@Composable
@Preview
fun addDailyPreview() {
    AddDailyVisit("0", null)
}