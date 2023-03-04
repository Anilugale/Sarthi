package com.vk.sarthi.ui.screen

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Attachment
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vk.sarthi.R
import com.vk.sarthi.cache.Cache
import com.vk.sarthi.createImageFile
import com.vk.sarthi.getUriForFile
import com.vk.sarthi.model.PersonsVisited
import com.vk.sarthi.model.Village
import com.vk.sarthi.ui.theme.FontColor1
import com.vk.sarthi.ui.theme.FontColor1Dark
import com.vk.sarthi.utli.Constants
import com.vk.sarthi.utli.SettingPreferences
import com.vk.sarthi.utli.SquireCropImage
import com.vk.sarthi.viewmodel.AddDailyVisitVM
import com.vk.sarthi.viewmodel.DailyVisitState
import com.vk.sarthi.viewmodel.PersonVisitedModel
import com.vk.sarthi.viewmodel.VisitOffLineModel
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import kotlinx.coroutines.launch
import java.io.File


@Composable
fun EditDailyVisit(workID: String, navigatorController: NavHostController?) {
    var dailyModel: VisitOffLineModel? = null
    if (workID != "0") {
        dailyModel = Cache.dailyVisitOfflineList.single { it.id == workID }
    }

    val viewModel: AddDailyVisitVM = hiltViewModel()
    val viewModelRem = remember { viewModel }
    var isDialogShowing by remember {
        mutableStateOf(false)
    }
    if (isDialogShowing) {
        ShowProgressDialog()
    }

    val collectAsState = viewModelRem.stateExpose.collectAsState()

    val current = LocalContext.current
    val rememberCoroutineScope = rememberCoroutineScope()
    if (Cache.villageData == null) {
        Cache.restoreVillageData(current)
    }
    when (collectAsState.value) {

        is DailyVisitState.Success -> {
            isDialogShowing = false
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
            isDialogShowing = true
        }

        is DailyVisitState.Failed -> {
            isDialogShowing = false
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
        if (dailyModel != null &&  dailyModel.hashMap.containsKey("persons_visited")) {
            val strPersonData = dailyModel.hashMap["persons_visited"]
            val list = Gson().fromJson<ArrayList<PersonsVisited>>(strPersonData,
                object : TypeToken<ArrayList<PersonsVisited>>() {}.type)
            if (list.isNotEmpty()) {
                personVisitedList.clear()

                list.forEachIndexed { index, user ->
                    val personVisitedModel = PersonVisitedModel(index.inc().toString())
                    personVisitedModel.name.value = user.name
                    personVisitedModel.subject.value = user.subject
                    personVisitedModel.survey.value = user.servey
                    personVisitedModel.information.value = user.information
                    personVisitedList.add(personVisitedModel)
                }
            }

        }

        var currentFile by remember { mutableStateOf<Uri?>(null) }
        var selectionFileType by remember { mutableStateOf("") }

        var devFile by remember { mutableStateOf(
            if (dailyModel!!.devinfofileBody.isEmpty()) {
                null
            }else{
                File(dailyModel.devinfofileBody)
            }
        ) }
        var rationInfoFile by remember { mutableStateOf(
            if (dailyModel!!.rashanshopinfoBody.isEmpty()) {
            null
        }else{
            File(dailyModel.rashanshopinfoBody)
        }) }

        var electricInfoFile by remember { mutableStateOf(
            if (dailyModel!!.electricInfoFileBody.isEmpty()) {
                null
            }else{
                File(dailyModel.electricInfoFileBody)
            }
        ) }

        var drinkingInfoFile by remember { mutableStateOf(
            if (dailyModel!!.drinkingwaterinfofileBody.isEmpty()) {
                null
            }else{
                File(dailyModel.drinkingwaterinfofileBody)
            }
        ) }
        var waterCanalInfoFile by remember { mutableStateOf(
            if (dailyModel!!.watercanelinfofileBody.isEmpty()) {
                null
            }else{
                File(dailyModel.watercanelinfofileBody)
            }
        ) }
        var schoolInfoFile by remember { mutableStateOf(
            if (dailyModel!!.schoolinfofileBody.isEmpty()) {
                null
            }else{
                File(dailyModel.schoolinfofileBody)
            }
        ) }
        var primaryHealthInfoFile by remember { mutableStateOf(
            if (dailyModel!!.primaryHealthInfoFileBody.isEmpty()) {
                null
            }else{
                File(dailyModel.primaryHealthInfoFileBody)
            }
        ) }
        var vetarnityHealthInfoFile by remember { mutableStateOf(
            if (dailyModel!!.vetarnityHealthInfoFileBody.isEmpty()) {
                null
            }else{
                File(dailyModel.vetarnityHealthInfoFileBody)
            }
        ) }
        var govInfoInfoFile by remember { mutableStateOf(
            if (dailyModel!!.govInfoInfoFileBody.isEmpty()) {
                null
            }else{
                File(dailyModel.govInfoInfoFileBody)
            }
        ) }
        var politicalInfoFile by remember { mutableStateOf(
            if (dailyModel!!.politicalInfoFileBody.isEmpty()) {
                null
            }else{
                File(dailyModel.politicalInfoFileBody)
            }
        ) }
        var deathPersonInfoFile by remember { mutableStateOf(
            if (dailyModel!!.deathPersonInfoFileBody.isEmpty()) {
                null
            }else{
                File(dailyModel.deathPersonInfoFileBody)
            }
        ) }
        var birthdayInfoFile by remember { mutableStateOf(
            if (dailyModel!!.birthdayFileBody.isEmpty()) {
                null
            }else{
                File(dailyModel.birthdayFileBody)
            }
        ) }
        var yojnaInfoFile by remember { mutableStateOf(
            if (dailyModel!!.newschemesfileBody.isEmpty()) {
                null
            }else{
                File(dailyModel.newschemesfileBody)
            }
        ) }
        var otherInfoFile by remember { mutableStateOf(

            if (dailyModel!!.otherInfoFileBody.isEmpty()) {
                null
            }else{
                File(dailyModel.otherInfoFileBody)
            }
        ) }


        val uCropLauncher = rememberLauncherForActivityResult(SquireCropImage()) { uri ->
            currentFile = uri
            uri?.apply {
                // upload file
                path?.let {path->
                    rememberCoroutineScope.launch {
                        val compressedImageFile = Compressor.compress(current, File(path)){
                            quality(50)
                        }

                        when (selectionFileType) {
                            Constants.DEV_INFO -> {
                                devFile = compressedImageFile
                            }
                            Constants.RASATION_INFO -> {
                                rationInfoFile = compressedImageFile
                            }
                            Constants.ELECTRIC_INFO -> {
                                electricInfoFile = compressedImageFile
                            }
                            Constants.DRINKING_WATER -> {
                                drinkingInfoFile = compressedImageFile
                            }
                            Constants.WATER_CANAL -> {
                                waterCanalInfoFile = compressedImageFile
                            }

                            Constants.SCHOOL_INFO -> {
                                schoolInfoFile = compressedImageFile
                            }

                            Constants.PRIMARAY_HELATH -> {
                                primaryHealthInfoFile = compressedImageFile
                            }

                            Constants.VETARNARY_HEALTH -> {
                                vetarnityHealthInfoFile = compressedImageFile
                            }
                            Constants.GOV_INFO -> {
                                govInfoInfoFile = compressedImageFile
                            }
                            Constants.POLITICAL_INFO -> {
                                politicalInfoFile = compressedImageFile
                            }

                            Constants.DEATH_PERSON_INFO -> {
                                deathPersonInfoFile =compressedImageFile
                            }
                            Constants.BIRTHDAY_INFO -> {
                                birthdayInfoFile = compressedImageFile
                            }

                            Constants.YOJNA_INFO_INFO -> {
                                yojnaInfoFile =compressedImageFile
                            }

                            Constants.OTHER_INFO -> {
                                otherInfoFile = compressedImageFile
                            }
                            else -> {}
                        }
                    }

                }
            }
        }
        val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
                if (success) {
                    currentFile?.let { uri ->
                        uCropLauncher.launch(
                            Pair(
                                first = uri,
                                second = Uri.fromFile(
                                    File(current.cacheDir, "temp_image_file_${System.currentTimeMillis()}")
                                )
                            )
                        )
                    }
                }
            }

        val imagePickerLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                uCropLauncher.launch(
                    Pair(
                        first = uri,
                        second = Uri.fromFile(
                            File(current.cacheDir, "temp_image_file_${System.currentTimeMillis()}")
                        )
                    )
                )
            }
        }

        val developmentInfo = remember { mutableStateOf(if (dailyModel?.hashMap?.get("devinfo") != null) dailyModel.hashMap["devinfo"]!! else "") }
        val rationShopInfo = remember { mutableStateOf(if (dailyModel?.hashMap?.get("rashanshopinfo") != null) dailyModel.hashMap["rashanshopinfo"]!! else "") }
        val electricInfo = remember { mutableStateOf(if (dailyModel?.hashMap?.get("electricityinfo") != null) dailyModel.hashMap["electricityinfo"]!! else "") }
        val drinkingWaterInfo = remember { mutableStateOf(if (dailyModel?.hashMap?.get("drinkingwaterinfo") != null) dailyModel.hashMap["drinkingwaterinfo"]!! else "") }
        val deathPersonInfo = remember { mutableStateOf(if (dailyModel?.hashMap?.get("deathpersoninfo") != null) dailyModel.hashMap["deathpersoninfo"]!! else "") }
        val waterCanalInfo = remember { mutableStateOf(if (dailyModel?.hashMap?.get("watercanelinfo") != null) dailyModel.hashMap["watercanelinfo"]!! else "") }
        val schoolInfo = remember { mutableStateOf(if (dailyModel?.hashMap?.get("schoolinfo") != null) dailyModel.hashMap["schoolinfo"]!! else "") }
        val prathamikInfo = remember { mutableStateOf(if (dailyModel?.hashMap?.get("primarycarecenterinfo") != null) dailyModel.hashMap["primarycarecenterinfo"]!! else "") }
        val pashuInfo = remember { mutableStateOf(if (dailyModel?.hashMap?.get("veterinarymedicineinfo") != null) dailyModel.hashMap["veterinarymedicineinfo"]!! else "") }
        val govEmpInfo = remember { mutableStateOf(if (dailyModel?.hashMap?.get("govservantinfo") != null) dailyModel.hashMap["govservantinfo"]!! else "") }
        val politicsInfo = remember { mutableStateOf(if (dailyModel?.hashMap?.get("politicalinfo") != null) dailyModel.hashMap["politicalinfo"]!! else "") }
        val birthdayInfo = remember { mutableStateOf(if (dailyModel?.hashMap?.get("birthdayinfo") != null) dailyModel.hashMap["birthdayinfo"]!! else "") }
        val gatLabhYojna = remember { mutableStateOf(if (dailyModel?.hashMap?.get("newschemes") != null) dailyModel.hashMap["newschemes"]!! else "") }
        val otherInfo = remember { mutableStateOf(if (dailyModel?.hashMap?.get("otherinfo") != null) dailyModel.hashMap["otherinfo"]!! else "") }

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
                val villageID = dailyModel.hashMap["villageid"] ?:""
                selectedVillage =
                    Cache.villageData!!.villages.single {model-> villageID.toInt() == model.id }
            }

            var ganName by remember { mutableStateOf(selectedVillage?.gan ?: "") }
            var villageName by remember { mutableStateOf(selectedVillage) }

            val list = Cache.villageData!!.villages.groupBy { model-> model.gan }
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

            val personDataValue = arrayListOf<Array<MutableState<String>>>()

            personVisitedList.forEachIndexed { _, model ->

                val name = remember {
                    model.name
                }

                val subject = remember {
                    model.subject
                }
                val survey = remember {
                    model.survey
                }
                val information = remember {
                    model.information
                }
                val data = arrayOf(name, subject, survey, information)
                personDataValue.add(data)
                PersonVisitedUI(
                    model = model,
                    labelColor,
                    personVisitedList,
                    (dailyModel == null),
                    name,
                    subject,
                    survey,
                    information
                )
            }


            OutlinedTextField(
                value = developmentInfo.value,
                onValueChange = {model-> developmentInfo.value = model },
                label = { Text(text = stringResource(R.string.development_info)) },
                modifier = Modifier.fillMaxWidth()
            )

            Row {
                if (developmentInfo.value.isNotEmpty()) {
                    Button(onClick = {
                        val newPhotoUri = current.createImageFile().getUriForFile(current)
                        currentFile = newPhotoUri
                        selectionFileType = Constants.DEV_INFO
                        cameraLauncher.launch(newPhotoUri)
                    }) {
                        Text(text = stringResource(R.string.development_info) + " photo *")
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
                onValueChange = {model-> rationShopInfo.value = model },
                label = { Text(text = stringResource(R.string.ration_info)) },
                modifier = Modifier.fillMaxWidth()
            )
            Row {
                if (rationShopInfo.value.isNotEmpty()) {
                    Button(onClick = {
                        val newPhotoUri = current.createImageFile().getUriForFile(current)
                        currentFile = newPhotoUri
                        selectionFileType = Constants.RASATION_INFO
                        cameraLauncher.launch(newPhotoUri)
                    }) {
                        Text(text = stringResource(R.string.ration_info) + " photo *")
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
                onValueChange = {model-> electricInfo.value = model },
                label = { Text(text = stringResource(R.string.electric_info)) },
                modifier = Modifier.fillMaxWidth()
            )
            Row {
            if (electricInfo.value.isNotEmpty()) {
                Button(onClick = {
                    val newPhotoUri = current.createImageFile().getUriForFile(current)
                    currentFile = newPhotoUri
                    selectionFileType = Constants.ELECTRIC_INFO
                    cameraLauncher.launch(newPhotoUri)
                }) {
                    Text(text = stringResource(R.string.electric_info) + " photo * ")

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
                onValueChange = { model-> drinkingWaterInfo.value = model },
                label = { Text(text = stringResource(R.string.drinking_water_info)) },
                modifier = Modifier.fillMaxWidth()
            )
            Row {
            if (drinkingWaterInfo.value.isNotEmpty()) {
                Button(onClick = {
                    val newPhotoUri = current.createImageFile().getUriForFile(current)
                    currentFile = newPhotoUri
                    selectionFileType = Constants.DRINKING_WATER
                    imagePickerLauncher.launch("image/*")
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
                onValueChange = { model-> waterCanalInfo.value = model },
                label = { Text(text = stringResource(R.string.water_canal_info)) },
                modifier = Modifier.fillMaxWidth()
            )
            Row {
            if (waterCanalInfo.value.isNotEmpty()) {
                Button(onClick = {
                    val newPhotoUri = current.createImageFile().getUriForFile(current)
                    currentFile = newPhotoUri
                    selectionFileType = Constants.WATER_CANAL
                    imagePickerLauncher.launch("image/*")
                }) {
                    Text(text = stringResource(R.string.water_canal_info) + " photo ")

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
                onValueChange = { model-> schoolInfo.value = model },
                label = { Text(text = stringResource(R.string.school_info)) },
                modifier = Modifier.fillMaxWidth()
            )
            Row {
            if (schoolInfo.value.isNotEmpty()) {
                Button(onClick = {
                    val newPhotoUri = current.createImageFile().getUriForFile(current)
                    currentFile = newPhotoUri
                    selectionFileType = Constants.SCHOOL_INFO
                    cameraLauncher.launch(newPhotoUri)
                }) {
                    Text(text = stringResource(R.string.school_info) + " photo * ")

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
                onValueChange = {model-> prathamikInfo.value = model },
                label = { Text(text = stringResource(R.string.prathamik_info)) },
                modifier = Modifier.fillMaxWidth()
            )
            Row {
            if (prathamikInfo.value.isNotEmpty()) {
                Button(onClick = {
                    val newPhotoUri = current.createImageFile().getUriForFile(current)
                    currentFile = newPhotoUri
                    selectionFileType = Constants.PRIMARAY_HELATH
                    cameraLauncher.launch(newPhotoUri)
                }) {
                    Text(text = stringResource(R.string.prathamik_info) + " photo * ")
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
                onValueChange = {model-> pashuInfo.value = model },
                label = { Text(text = stringResource(R.string.pashu_info)) },
                modifier = Modifier.fillMaxWidth()
            )
            Row {
            if (pashuInfo.value.isNotEmpty()) {
                Button(onClick = {
                    val newPhotoUri = current.createImageFile().getUriForFile(current)
                    currentFile = newPhotoUri
                    selectionFileType = Constants.VETARNARY_HEALTH
                    cameraLauncher.launch(newPhotoUri)
                }) {
                    Text(text = stringResource(R.string.pashu_info) + " Photo *")
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
                onValueChange = { model-> govEmpInfo.value = model },
                label = { Text(text = stringResource(R.string.gov_emp_info)) },
                modifier = Modifier.fillMaxWidth()
            )
            Row {
            if (govEmpInfo.value.isNotEmpty()) {
                Button(onClick = {
                    val newPhotoUri = current.createImageFile().getUriForFile(current)
                    currentFile = newPhotoUri
                    selectionFileType = Constants.GOV_INFO
                    imagePickerLauncher.launch("image/*")
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
                onValueChange = { model-> politicsInfo.value = model },
                label = { Text(text = stringResource(R.string.politics_info)) },
                modifier = Modifier.fillMaxWidth()
            )

            Row {
                if (politicsInfo.value.isNotEmpty()) {
                    Button(onClick = {
                        val newPhotoUri = current.createImageFile().getUriForFile(current)
                        currentFile = newPhotoUri
                        selectionFileType = Constants.POLITICAL_INFO
                        imagePickerLauncher.launch("image/*")
                    }) {
                        Text(text = stringResource(R.string.politics_info) + " Photo")
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

                OutlinedTextField(
                    value = deathPersonInfo.value,
                    onValueChange = {model-> deathPersonInfo.value = model },
                    label = { Text(text = stringResource(R.string.death_person_info)) },
                    modifier = Modifier.fillMaxWidth()
                )
            Row {
                if (deathPersonInfo.value.isNotEmpty()) {
                    Button(onClick = {
                        val newPhotoUri = current.createImageFile().getUriForFile(current)
                        currentFile = newPhotoUri
                        selectionFileType = Constants.DEATH_PERSON_INFO
                        imagePickerLauncher.launch("image/*")
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
                onValueChange = { model-> birthdayInfo.value = model },
                label = { Text(text = stringResource(R.string.birthday_info)) },
                modifier = Modifier.fillMaxWidth()
            )
            Row {
                if (birthdayInfo.value.isNotEmpty()) {
                    Button(onClick = {
                        val newPhotoUri = current.createImageFile().getUriForFile(current)
                        currentFile = newPhotoUri
                        selectionFileType = Constants.BIRTHDAY_INFO
                        imagePickerLauncher.launch("image/*")
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
                onValueChange = { model-> gatLabhYojna.value = model },
                label = { Text(text = stringResource(R.string.gat_labh_yojna)) },
                modifier = Modifier.fillMaxWidth()
            )
            Row {
                if (gatLabhYojna.value.isNotEmpty()) {
                    Button(onClick = {
                        val newPhotoUri = current.createImageFile().getUriForFile(current)
                        currentFile = newPhotoUri
                        selectionFileType = Constants.YOJNA_INFO_INFO
                        imagePickerLauncher.launch("image/*")
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
                onValueChange = {model-> otherInfo.value = model },
                label = { Text(text = stringResource(R.string.other_info)) },
                modifier = Modifier.fillMaxWidth(),

                )
            Row {
            if (otherInfo.value.isNotEmpty()) {
                Button(onClick = {
                    val newPhotoUri = current.createImageFile().getUriForFile(current)
                    currentFile = newPhotoUri
                    selectionFileType = Constants.OTHER_INFO
                    imagePickerLauncher.launch("image/*")
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
                    personDataValue.forEachIndexed { index,person->

                        Log.d("@@", "AddDailyVisit: $person")
                        resetErrorFlag(   personVisitedList[index])
                        if (person[0].value.isNotEmpty() || person[1].value.isNotEmpty() ||  person[2].value.isNotEmpty() || person[3].value.isNotEmpty()) {
                            if (person[0].value.isEmpty()) {
                                personVisitedList[index].isN.value = true
                                return@Button
                            } else if (person[1].value.isEmpty()) {
                                personVisitedList[index].isS.value = true
                                return@Button
                            } else if (person[3].value.isEmpty()) {
                                personVisitedList[index].isIn.value = true
                                return@Button
                            } else if (person[2].value.isEmpty()) {
                                personVisitedList[index].isSu.value = true
                                return@Button
                            } else {
                                personalList.add(
                                    PersonsVisited(
                                        information = person[3].value,
                                        name = person[0].value,
                                        servey = person[2].value,
                                        subject = person[1].value,
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

                    val map: HashMap<String, String> = HashMap()
                    map["persons_visited"] = Gson().toJson(personalList)
                    map["coordinator_id"] = Cache.loginUser!!.id.toString()
                    map["villageid"] = villageName!!.id.toString()


                    Log.d("##", "eeeee: $devFile")
                    Log.d("##", "eeeee: $rationInfoFile")
                if (rationShopInfo.value.isNotEmpty() && rationInfoFile == null) {
                    current.toast(current.getString(R.string.ration_info)+" Photo Mandatory")
                } else if (electricInfo.value.isNotEmpty() && electricInfoFile == null ) {
                    current.toast(current.getString(R.string.electric_info)+" Photo Mandatory")
                } else if (schoolInfo.value.isNotEmpty() && schoolInfoFile == null ) {
                    current.toast(current.getString(R.string.school_info)+" Photo Mandatory")
                } else if (prathamikInfo.value.isNotEmpty() && primaryHealthInfoFile == null ) {
                    current.toast(current.getString(R.string.prathamik_info)+" Photo Mandatory")
                } else if (developmentInfo.value.isNotEmpty() && devFile == null) {
                    current.toast(current.getString(R.string.development_info)+" Photo Mandatory")
                }   else if (pashuInfo.value.isNotEmpty() && vetarnityHealthInfoFile == null ) {
                        current.toast(current.getString(R.string.pashu_info)+" Photo Mandatory")
                } else {


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
                        map["latitude"] =  latitude
                        map["longitude"] =  longitude
                        map["birthdayinfo"] = birthdayInfo.value
                        map["rashanshopinfo"] = rationShopInfo.value
                        map["electricityinfo"] = electricInfo.value
                        map["drinkingwaterinfo"] = drinkingWaterInfo.value
                        map["watercanelinfo"] = waterCanalInfo.value
                        map["schoolinfo"] = schoolInfo.value
                        map["primarycarecenterinfo"] = prathamikInfo.value
                        map["veterinarymedicineinfo"] = pashuInfo.value
                        map["govservantinfo"] = govEmpInfo.value
                        map["politicalinfo"] = politicsInfo.value
                        map["deathpersoninfo"] = deathPersonInfo.value
                        map["newschemes"] = gatLabhYojna.value
                        map["devinfo"] = developmentInfo.value
                        map["otherinfo"] = otherInfo.value
                        Cache.removeOfflineDaily(current,dailyModel!!)

                        viewModel.setDailyVisitReq(
                             0, map,
                            birthdayInfoFile,
                            rationInfoFile,
                            electricInfoFile,
                            drinkingInfoFile,
                            waterCanalInfoFile,
                            schoolInfoFile,
                            primaryHealthInfoFile,
                            vetarnityHealthInfoFile,
                            govInfoInfoFile,
                            politicalInfoFile,
                            deathPersonInfoFile,
                            yojnaInfoFile,
                            devFile,
                            otherInfoFile,
                            villageName!!.village
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





