package com.vk.sarthi.ui.screen

import android.content.Intent
import android.net.Uri
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.vk.sarthi.R
import com.vk.sarthi.cache.Cache
import com.vk.sarthi.model.DailyVisitModel
import com.vk.sarthi.model.DailyVisitReqModel
import com.vk.sarthi.model.PersonsVisited
import com.vk.sarthi.model.Village
import com.vk.sarthi.ui.theme.FontColor1
import com.vk.sarthi.ui.theme.FontColor1Dark
import com.vk.sarthi.ui.theme.FontColor2
import com.vk.sarthi.utli.*
import com.vk.sarthi.viewmodel.AddDailyVisitVM
import com.vk.sarthi.viewmodel.DailyVisitState
import java.io.File
import java.util.*

@OptIn(ExperimentalMaterialApi::class)
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
            current.toast(uri!!.path!!)
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
                    Constants.RASATION_INFO->{
                        rationInfoFile = File(uri.path)
                    }
                    Constants.ELECTRIC_INFO->{
                        electricInfoFile = File(uri.path)
                    }
                    Constants.DRINKING_WATER->{
                        drinkingInfoFile = File(uri.path)
                    }
                    Constants.WATER_CANAL->{
                        waterCanalInfoFile = File(uri.path)
                    }

                    Constants.SCHOOL_INFO->{
                        schoolInfoFile = File(uri.path)
                    }

                    Constants.PRIMARAY_HELATH->{
                        primaryHealthInfoFile = File(uri.path)
                    }

                    Constants.VETARNARY_HEALTH->{
                        vetarnityHealthInfoFile = File(uri.path)
                    }
                    Constants.GOV_INFO->{
                        govInfoInfoFile = File(uri.path)
                    }
                    Constants.POLITICAL_INFO->{
                        politicalInfoFile = File(uri.path)
                    }

                    Constants.DEATH_PERSON_INFO->{
                        deathPersonInfoFile = File(uri.path)
                    }
                    Constants.BIRTHDAY_INFO->{
                        birthdayInfoFile = File(uri.path)
                    }

                    Constants.YOJNA_INFO_INFO->{
                        yojnaInfoFile = File(uri.path)
                    }

                    Constants.OTHER_INFO->{
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
                .verticalScroll(rememberScrollState())
        ) {
            var selectedVillage: Village? = null
            if (dailyModel != null && Cache.villageData != null) {
                selectedVillage =
                    Cache.villageData!!.villages.single { dailyModel.villageid == it.id }
            }

            var exp by remember { mutableStateOf(false) }
            var talukaName by remember { mutableStateOf(selectedVillage?.taluka ?: "") }
            var gavName by remember { mutableStateOf(selectedVillage) }
            var villageName by remember { mutableStateOf(selectedVillage?.village ?: "") }

            ExposedDropdownMenuBox(expanded = exp, onExpandedChange = { exp = !exp }) {
                TextField(
                    value = talukaName,
                    onValueChange = { talukaName = it },
                    label = { Text(stringResource(id = R.string.taluka)) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = exp)
                    },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                )
                if (Cache.villageData!!.taluka.isNotEmpty()) {
                    ExposedDropdownMenu(expanded = exp, onDismissRequest = { exp = false }) {
                        Cache.villageData!!.taluka.forEach { option ->
                            DropdownMenuItem(onClick = {
                                talukaName = option
                                gavName = null
                                villageName = ""
                                exp = false
                            }) {
                                Text(text = option)
                            }
                        }
                    }
                }
            }

            if (talukaName.isNotEmpty()) {

                val optionsGav = Cache.villageData!!.villages.filter { talukaName == it.taluka }
                var expGav by remember { mutableStateOf(false) }


                ExposedDropdownMenuBox(expanded = expGav, onExpandedChange = { expGav = !expGav }) {
                    TextField(
                        value = villageName,
                        onValueChange = { villageName = it },
                        label = { Text(stringResource(id = R.string.taluka)) },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expGav)
                        },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                    if (optionsGav.isNotEmpty()) {
                        ExposedDropdownMenu(expanded = expGav,
                            onDismissRequest = { expGav = false }) {
                            optionsGav.forEach { optionsGav ->
                                DropdownMenuItem(onClick = {
                                    gavName = optionsGav
                                    villageName = optionsGav.village
                                    expGav = false
                                }) {
                                    Text(text = optionsGav.village)
                                }
                            }
                        }
                    }
                }
                if (gavName != null) {
                    Button(
                        onClick = {
                            var intent = Intent(Intent.ACTION_VIEW)
                            intent.setDataAndType(
                                Uri.parse("https://shirdiyuva.in/" + gavName!!.infourl),
                                "application/pdf"
                            )
                            intent = Intent.createChooser(intent, "Open File")
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            current.startActivity(intent)
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(text = "Detail")
                    }
                }


            }

            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Persons Visited",
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


            personVisitedList.forEach {
                PersonVisitedUI(model = it, labelColor, personVisitedList, (dailyModel == null))
            }


            OutlinedTextField(
                value = developmentInfo.value,
                onValueChange = { developmentInfo.value = it },
                label = { Text(text = stringResource(R.string.development_info)) },
                modifier = Modifier.fillMaxWidth()
            )

            if (developmentInfo.value.isNotEmpty()) {
                Button(onClick = {
                    val newPhotoUri = current.createImageFile().getUriForFile(current)
                    currentFile = newPhotoUri
                    SelectionFileType = Constants.DEV_INFO
                    cameraLauncher.launch(newPhotoUri)
                }) {
                    Text(text = "Dev Info Image")
                }
            }




            OutlinedTextField(
                value = rationShopInfo.value,
                onValueChange = { rationShopInfo.value = it },
                label = { Text(text = stringResource(R.string.ration_info)) },
                modifier = Modifier.fillMaxWidth()
            )

            if (rationShopInfo.value.isNotEmpty()) {
                Button(onClick = {
                    val newPhotoUri = current.createImageFile().getUriForFile(current)
                    currentFile = newPhotoUri
                    SelectionFileType = Constants.RASATION_INFO
                    cameraLauncher.launch(newPhotoUri)
                }) {
                    Text(text = "Ration Info Image")
                }
            }
            OutlinedTextField(
                value = electricInfo.value,
                onValueChange = { electricInfo.value = it },
                label = { Text(text = stringResource(R.string.electric_info)) },
                modifier = Modifier.fillMaxWidth()
            )
            if (electricInfo.value.isNotEmpty()) {
                Button(onClick = {
                    val newPhotoUri = current.createImageFile().getUriForFile(current)
                    currentFile = newPhotoUri
                    SelectionFileType = Constants.ELECTRIC_INFO
                    cameraLauncher.launch(newPhotoUri)
                }) {
                    Text(text = "Ration Info Image")

                }
            }

            OutlinedTextField(
                value = drinkingWaterInfo.value,
                onValueChange = { drinkingWaterInfo.value = it },
                label = { Text(text = stringResource(R.string.drinking_water_info)) },
                modifier = Modifier.fillMaxWidth()
            )

            if (drinkingWaterInfo.value.isNotEmpty()) {
                Button(onClick = {
                    val newPhotoUri = current.createImageFile().getUriForFile(current)
                    currentFile = newPhotoUri
                    SelectionFileType = Constants.DRINKING_WATER
                    cameraLauncher.launch(newPhotoUri)
                }) {
                    Text(text = "Drinking water Image")

                }
            }



            OutlinedTextField(
                value = waterCanalInfo.value,
                onValueChange = { waterCanalInfo.value = it },
                label = { Text(text = stringResource(R.string.water_canal_info)) },
                modifier = Modifier.fillMaxWidth()
            )

            if (waterCanalInfo.value.isNotEmpty()) {
                Button(onClick = {
                    val newPhotoUri = current.createImageFile().getUriForFile(current)
                    currentFile = newPhotoUri
                    SelectionFileType = Constants.WATER_CANAL
                    cameraLauncher.launch(newPhotoUri)
                }) {
                    Text(text = "Water Canal Image")

                }
            }



            OutlinedTextField(
                value = schoolInfo.value,
                onValueChange = { schoolInfo.value = it },
                label = { Text(text = stringResource(R.string.school_info)) },
                modifier = Modifier.fillMaxWidth()
            )

            if (schoolInfo.value.isNotEmpty()) {
                Button(onClick = {
                    val newPhotoUri = current.createImageFile().getUriForFile(current)
                    currentFile = newPhotoUri
                    SelectionFileType = Constants.SCHOOL_INFO
                    cameraLauncher.launch(newPhotoUri)
                }) {
                    Text(text = "Water Canal Image")

                }
            }

            OutlinedTextField(
                value = prathamikInfo.value,
                onValueChange = { prathamikInfo.value = it },
                label = { Text(text = stringResource(R.string.prathamik_info)) },
                modifier = Modifier.fillMaxWidth()
            )

            if (prathamikInfo.value.isNotEmpty()) {
                Button(onClick = {
                    val newPhotoUri = current.createImageFile().getUriForFile(current)
                    currentFile = newPhotoUri
                    SelectionFileType = Constants.PRIMARAY_HELATH
                    cameraLauncher.launch(newPhotoUri)
                }) {
                    Text(text = "Primary Health Care Image")
                }
            }

            OutlinedTextField(
                value = pashuInfo.value,
                onValueChange = { pashuInfo.value = it },
                label = { Text(text = stringResource(R.string.pashu_info)) },
                modifier = Modifier.fillMaxWidth()
            )

            if (pashuInfo.value.isNotEmpty()) {
                Button(onClick = {
                    val newPhotoUri = current.createImageFile().getUriForFile(current)
                    currentFile = newPhotoUri
                    SelectionFileType = Constants.VETARNARY_HEALTH
                    cameraLauncher.launch(newPhotoUri)
                }) {
                    Text(text = stringResource(R.string.pashu_info)+ "Image")
                }
            }

            OutlinedTextField(
                value = govEmpInfo.value,
                onValueChange = { govEmpInfo.value = it },
                label = { Text(text = stringResource(R.string.gov_emp_info)) },
                modifier = Modifier.fillMaxWidth()
            )

            if (govEmpInfo.value.isNotEmpty()) {
                Button(onClick = {
                    val newPhotoUri = current.createImageFile().getUriForFile(current)
                    currentFile = newPhotoUri
                    SelectionFileType = Constants.GOV_INFO
                    cameraLauncher.launch(newPhotoUri)
                }) {
                    Text(text = stringResource(R.string.gov_emp_info)+ "Image")
                }
            }

            OutlinedTextField(
                value = politicsInfo.value,
                onValueChange = { politicsInfo.value = it },
                label = { Text(text = stringResource(R.string.politics_info)) },
                modifier = Modifier.fillMaxWidth()
            )


            if (politicsInfo.value.isNotEmpty()) {
                Button(onClick = {
                    val newPhotoUri = current.createImageFile().getUriForFile(current)
                    currentFile = newPhotoUri
                    SelectionFileType = Constants.POLITICAL_INFO
                    cameraLauncher.launch(newPhotoUri)
                }) {
                    Text(text = stringResource(R.string.gov_emp_info)+ "Image")
                }
            }

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
                    Text(text = stringResource(R.string.death_person_info)+ "Image")
                }
            }

            OutlinedTextField(
                value = birthdayInfo.value,
                onValueChange = { birthdayInfo.value = it },
                label = { Text(text = stringResource(R.string.birthday_info)) },
                modifier = Modifier.fillMaxWidth()
            )

            if (birthdayInfo.value.isNotEmpty()) {
                Button(onClick = {
                    val newPhotoUri = current.createImageFile().getUriForFile(current)
                    currentFile = newPhotoUri
                    SelectionFileType = Constants.BIRTHDAY_INFO
                    cameraLauncher.launch(newPhotoUri)
                }) {
                    Text(text = stringResource(R.string.birthday_info)+ "Image")
                }
            }

            OutlinedTextField(
                value = gatLabhYojna.value,
                onValueChange = { gatLabhYojna.value = it },
                label = { Text(text = stringResource(R.string.gat_labh_yojna)) },
                modifier = Modifier.fillMaxWidth()
            )

            if (gatLabhYojna.value.isNotEmpty()) {
                Button(onClick = {
                    val newPhotoUri = current.createImageFile().getUriForFile(current)
                    currentFile = newPhotoUri
                    SelectionFileType = Constants.YOJNA_INFO_INFO
                    cameraLauncher.launch(newPhotoUri)
                }) {
                    Text(text = stringResource(R.string.gat_labh_yojna)+ "Image")
                }
            }

            OutlinedTextField(
                value = otherInfo.value,
                onValueChange = { otherInfo.value = it },
                label = { Text(text = stringResource(R.string.other_info)) },
                modifier = Modifier.fillMaxWidth(),

                )

            if (otherInfo.value.isNotEmpty()) {
                Button(onClick = {
                    val newPhotoUri = current.createImageFile().getUriForFile(current)
                    currentFile = newPhotoUri
                    SelectionFileType = Constants.OTHER_INFO
                    cameraLauncher.launch(newPhotoUri)
                }) {
                    Text(text = stringResource(R.string.other_info)+ "Image")
                }
            }


            Button(
                onClick = {
                    if (gavName == null) {
                        Toast.makeText(current, "Select Village", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    val list = arrayListOf<PersonsVisited>()
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
                                list.add(
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
                    val model = DailyVisitReqModel(
                        birthdayinfo = birthdayInfo.value,
                        coordinatorid = Cache.loginUser!!.id.toString(),
                        deathpersoninfo = deathPersonInfo.value,
                        drinkingwaterinfo = drinkingWaterInfo.value,
                        electricityinfo = electricInfo.value,
                        govservantinfo = govEmpInfo.value,
                        latitude = latitude,
                        longitude = longitude,
                        newschemes = gatLabhYojna.value,
                        politicalinfo = politicsInfo.value,
                        primarycarecenterinfo = prathamikInfo.value,
                        schoolinfo = schoolInfo.value,
                        rashanshopinfo = rationShopInfo.value,
                        veterinarymedicineinfo = pashuInfo.value,
                        watercanelinfo = waterCanalInfo.value,
                        persons_visited = list,
                        villageid = gavName!!.id.toString(),
                        visitid = dailyModel?.id?.toString() ?: "",
                        otherinfo = otherInfo.value,
                        devinfo = developmentInfo.value

                    )
                    viewModel.setDailyVisitReq(model)


                }, modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 15.dp, horizontal = 10.dp)
            ) {
                Text(text = stringResource(R.string.submit))
            }
        }


    }


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