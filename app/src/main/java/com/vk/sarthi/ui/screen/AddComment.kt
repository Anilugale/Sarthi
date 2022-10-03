package com.vk.sarthi.ui.screen

import android.database.Cursor
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.HighlightOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.vk.sarthi.R
import com.vk.sarthi.cache.Cache
import com.vk.sarthi.model.Comment
import com.vk.sarthi.model.ComplaintModel
import com.vk.sarthi.service.Service
import com.vk.sarthi.utli.Util
import com.vk.sarthi.viewmodel.AddCommentViewModel
import com.vk.sarthi.viewmodel.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileInputStream
import javax.inject.Inject

@Composable
fun AddComment(id:String,commentid:String,navigatorController: NavHostController?){

    val complaintModel:ComplaintModel? = try{
            Cache.commentList.single { it.ticket_id == id.toInt() }
        }catch (e:Exception){
            null
        }
    val comment: Comment? = try{
        complaintModel?.comments?.single { it.comment_id == commentid.toInt() }
    }catch (e:Exception){
        null
    }


    val fileUrl = remember {
        mutableStateOf<File?>(null)
    }
    val fileName = remember {
        mutableStateOf("")
    }
    val fileSize = remember {
        mutableStateOf<Long>(0)
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
                fileName.value =
                    cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                cursor.moveToFirst()
                fileSize.value = cursor.getLong(cursor.getColumnIndex(OpenableColumns.SIZE))
                cursor.close()
                if (Util.checkForFileSize(fileSize.value)) {
                    val fileInputStream =
                        context.contentResolver.openInputStream(imageUri) as FileInputStream?
                    val dst = File(context.cacheDir, fileName.value)
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
        TopAppBar(title = { Text(text = context.getString(R.string.new_comment)) },
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
            })
    }) { paddingValue ->

        val commentTxt = remember {
            mutableStateOf(comment?.comment ?: "")
        }
        val model: AddCommentViewModel = hiltViewModel()
        val viewModel = remember { model }
        when(val value1 = viewModel.stateExpose.collectAsState().value){
            is Status.Process -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    ShowLoginProcess()
                }
            }

            is Status.SuccessComment -> {
                LaunchedEffect(key1 = value1){
                    Toast.makeText(context,value1.message,Toast.LENGTH_SHORT).show()
                    navigatorController?.popBackStack()
                }
            }
            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValue)
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
                                onClick = { pickPictureLauncher.launch("*/*") },
                                modifier = Modifier
                                    .padding(10.dp)
                                    .align(Alignment.CenterVertically)
                            ) {
                                Text(
                                    text = if (fileUrl.value != null) {
                                        fileName.value
                                    } else {
                                        "Attachment"
                                    }
                                )
                            }

                            if (fileUrl.value != null) {
                                Icon(
                                    imageVector = Icons.Outlined.HighlightOff,
                                    contentDescription = "remove",
                                    tint = Color.Red,
                                    modifier = Modifier
                                        .align(Alignment.CenterVertically)
                                        .clickable {
                                            fileUrl.value = null
                                            fileName.value = ""
                                            fileSize.value = 0
                                        }
                                )
                            }
                        }

                    }

                    Button(
                        onClick = {
                            if (fileUrl.value != null) {
                                Log.d("@@", "File path: " + fileUrl.value!!.path)
                            }
                            Log.d("@@", "comment text: " + commentTxt.value)
                            model.createComment(
                                id,
                                Cache.loginUser!!.id.toString(),
                                commentTxt.value,
                                fileUrl.value,
                                complaintModel,
                                commentid
                            )

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

    }

}



@Composable
@Preview
fun PreviewAddComment() {
    AddComment(id= "d","",navigatorController = null)
}


