package com.vk.sarthi.ui.screen

import android.content.Context
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
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import kotlinx.coroutines.launch
import java.io.File


@Composable
fun AddDailyVisit(workID: String, navigatorController: NavHostController?) {
    var dailyModel: DailyVisitModel? = null
    if (workID != "0") {
        dailyModel = Cache.getDailyVisitModel(workID.toInt())
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
        var selectionFileType by remember { mutableStateOf("") }

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
                    Cache.villageData!!.villages.single {model-> dailyModel.villageid == model.id }
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


            personVisitedList.forEach {model->
                PersonVisitedUI(model = model, labelColor, personVisitedList, (dailyModel == null))
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
                    personVisitedList.forEach { person->
                        Log.d("@@", "AddDailyVisit: $person")
                        resetErrorFlag(person)
                        if (person.name.value.isNotEmpty() || person.subject.value.isNotEmpty() || person.information.value.isNotEmpty() || person.survey.value.isNotEmpty()) {
                            if (person.name.value.isEmpty()) {
                                person.isN.value = true
                                return@Button
                            } else if (person.subject.value.isEmpty()) {
                                person.isS.value = true
                                return@Button
                            } else if (person.information.value.isEmpty()) {
                                person.isIn.value = true
                                return@Button
                            } else if (person.survey.value.isEmpty()) {
                                person.isSu.value = true
                                return@Button
                            } else {
                                personalList.add(
                                    PersonsVisited(
                                        information = person.information.value,
                                        name = person.name.value,
                                        servey = person.survey.value,
                                        subject = person.subject.value,
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

                    map["coordinator_id"] = Cache.loginUser!!.id.toString()
                    map["villageid"] = villageName!!.id.toString()
                    map["persons_visited"] = Gson().toJson(personalList)
                    if (dailyModel != null) {
                        map["visitid"] =
                            dailyModel.id.toString()
                    }
                if (rationShopInfo.value.isNotEmpty() && rationInfoFile == null && dailyModel == null) {
                    current.toast(current.getString(R.string.ration_info)+" Photo Mandatory")
                } else if (electricInfo.value.isNotEmpty() && electricInfoFile == null && dailyModel == null) {
                    current.toast(current.getString(R.string.electric_info)+" Photo Mandatory")
                } else if (schoolInfo.value.isNotEmpty() && schoolInfoFile == null && dailyModel == null) {
                    current.toast(current.getString(R.string.school_info)+" Photo Mandatory")
                } else if (prathamikInfo.value.isNotEmpty() && primaryHealthInfoFile == null && dailyModel == null) {
                    current.toast(current.getString(R.string.prathamik_info)+" Photo Mandatory")
                } else if (developmentInfo.value.isNotEmpty() && devFile == null && dailyModel == null) {
                    current.toast(current.getString(R.string.development_info)+" Photo Mandatory")
                }   else if (pashuInfo.value.isNotEmpty() && vetarnityHealthInfoFile == null && dailyModel == null) {
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

                        viewModel.setDailyVisitReq(
                            dailyModel?.id ?: 0, map,
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



fun showImage(devFile: File, current: Context) {
    val uriForFile =
        FileProvider.getUriForFile(current, BuildConfig.APPLICATION_ID + ".fileprovider", devFile)

    val intent = Intent(Intent.ACTION_VIEW)
        .setDataAndType(uriForFile,
            "image/*"
        ).addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    current.startActivity(intent)
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
fun AddDailyPreview() {
    AddDailyVisit("0", null)
}