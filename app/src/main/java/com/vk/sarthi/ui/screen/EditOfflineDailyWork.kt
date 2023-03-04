package com.vk.sarthi.ui.screen

import android.database.Cursor
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.HighlightOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.vk.sarthi.R
import com.vk.sarthi.cache.Cache
import com.vk.sarthi.model.OfficeWorkModel
import com.vk.sarthi.utli.Util
import com.vk.sarthi.viewmodel.AddDailyVisitViewModel
import com.vk.sarthi.viewmodel.OfficeWorkOfflineModel
import java.io.File
import java.io.FileInputStream

@Composable
fun EditOffLineDailyWork(workID: String, navigatorController: NavHostController?) {

    val fileUrl = remember {
        mutableStateOf<File?>(null)
    }
    val context = LocalContext.current

    val pickPictureLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { imageUri ->
        if (imageUri != null) {

            val cursor: Cursor? = imageUri.let {
                context.applicationContext.contentResolver.query(it, null, null, null, null)
            }
            if (cursor != null) {
                cursor.moveToFirst()
                val fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                val fileSize = cursor.getLong(cursor.getColumnIndex(OpenableColumns.SIZE))
                cursor.close()
                if (Util.checkForFileSize(fileSize)) {
                    val fileInputStream = context.contentResolver.openInputStream(imageUri) as FileInputStream?
                    val dst = File(context.cacheDir, fileName)
                    Util.copyFile(fileInputStream!!, dst)
                    fileUrl.value = dst
                } else {
                    Toast.makeText(
                        context,
                        "File size should not greater than 10 MB",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text(
                    text = LocalContext.current.getString(R.string.add_daily_work),
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
            }
        )
    }) { padding ->

        var dataModel: OfficeWorkOfflineModel? = null
        if (workID != "0") {
            try {
                dataModel = Cache.officeWorkOfflineList.single { it.id == workID }
                dataModel?.let {
                    if (it.filePath.isNotEmpty()) {
                    //    fileUrl.value = File(it.filePath)
                     //   fileName.value = File(it.filePath).name ?: ""
                    }
                }
            } catch (e: Exception) {
            }
        }

        val model: AddDailyVisitViewModel = hiltViewModel()
        val viewModel = remember { model }
        val commentTxt = remember {
            mutableStateOf(dataModel?.commentTxt ?: "")
        }

        val showProgress = remember {
            mutableStateOf(false)
        }
        when (val workState = viewModel.stateExpose.collectAsState().value) {
            is WorkState.Success -> {
                showProgress.value = false
                LaunchedEffect(workState) {
                    navigatorController?.popBackStack()
                }
                LaunchedEffect(viewModel.stateExpose.collectAsState().value) {
                    Toast.makeText(context, workState.msg, Toast.LENGTH_SHORT).show()
                }

            }

            is WorkState.Failed -> {
                showProgress.value = false
                LaunchedEffect(viewModel.stateExpose.collectAsState().value) {
                    Toast.makeText(context, workState.msg, Toast.LENGTH_SHORT).show()
                }
            }

            else -> {

            }
        }




        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(10.dp)
        ) {


            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.TopStart)
            ) {

                OutlinedTextField(
                    value = commentTxt.value,
                    onValueChange = {
                        if (it.length < 501) {
                            commentTxt.value = it
                        }
                    },
                    label = { Text(text = "Type message here....") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.35f)
                )

                Text(
                    text = "${commentTxt.value.length}/500",
                    modifier = Modifier
                        .padding(5.dp)
                        .align(Alignment.End)
                )


                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = {
                            pickPictureLauncher.launch("*/*")
                        },
                        modifier = Modifier
                            .padding(10.dp)
                            .align(Alignment.CenterVertically)
                    ) {
                        Text(
                            text =  if(fileUrl.value!=null){
                                fileUrl.value?.name?:"Attachment"
                            }else if(dataModel!=null && dataModel.filePath.isNotEmpty()){
                               File( dataModel.filePath).name
                            }else{
                                "Attachment"
                            }
                        )
                    }

                    if (fileUrl.value!=null) {
                        Icon(
                            imageVector = Icons.Outlined.HighlightOff,
                            contentDescription = "remove",
                            tint = Color.Red,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .clickable {
                                    fileUrl.value = null
                                }
                        )
                    }
                }


                AnimatedVisibility(visible = showProgress.value) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Center) {
                        CircularProgressIndicator()
                    }
                }
            }


            Button(
                onClick = {
                    showProgress.value = true
                    dataModel!!.commentTxt = commentTxt.value
                    if (fileUrl.value != null) {
                        dataModel.filePath = fileUrl.value!!.absolutePath
                    }
                    Cache.updatedOffLineModel(dataModel, context)
                    navigatorController?.navigateUp()
                },
                enabled = commentTxt.value.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart),
            ) {
                Text(
                    text = "Submit",
                )

            }
        }

    }


}


