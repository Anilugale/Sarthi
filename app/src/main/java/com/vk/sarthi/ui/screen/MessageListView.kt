package com.vk.sarthi.utli.com.vk.sarthi.ui.screen

import android.text.format.DateUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.vk.sarthi.R
import com.vk.sarthi.ui.nav.Screens
import com.vk.sarthi.ui.screen.DrawerView
import com.vk.sarthi.ui.theme.Teal200
import com.vk.sarthi.ui.theme.WindowColor
import com.vk.sarthi.utli.com.vk.sarthi.model.MessageModel
import com.vk.sarthi.utli.com.vk.sarthi.viewmodel.MessageViewModel
import com.vk.sarthi.utli.com.vk.sarthi.viewmodel.MsgListStatus
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

@Composable
fun MessageListView(navigator: NavHostController) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.msg_list)) },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            scope.launch {
                                scaffoldState.drawerState.open()
                            }
                        },
                    ) {
                        Icon(
                            Icons.Rounded.Menu,
                            contentDescription = ""
                        )
                    }
                },
            )
        },
        backgroundColor = if (isSystemInDarkTheme()) Color.Black else WindowColor,
        drawerContent = { DrawerView(navigator, Screens.MessageList.route) },
        modifier = Modifier.background(color = MaterialTheme.colors.background)
    ) {
        val vModel: MessageViewModel = hiltViewModel()
        val rememberVm = remember {
            vModel
        }

        when (val uiState = rememberVm.stateExpose.collectAsState().value) {
            MsgListStatus.Empty -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "No data Found",
                        textAlign = TextAlign.Center
                    )
                }
            }

            MsgListStatus.Progress -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is MsgListStatus.SuccessMsgList -> {
                ShowMessageList(uiState.msgList.reversed(), it)
            }
            is MsgListStatus.Error->{
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = uiState.msg,
                        textAlign = TextAlign.Center
                    )
                }
            }

            else -> {}
        }
    }


}

@Composable
fun ShowMessageList(msgList: List<MessageModel>, paddingValues: PaddingValues) {
    val isShowMessageDialog = remember {
        mutableStateOf(false)
    }

    val currentMessageShow = remember {
        mutableStateOf<MessageModel?>(null)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(paddingValues)
    ) {
        items(count = msgList.size, key = { it }) {
            Card(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
                    .clickable {
                        isShowMessageDialog.value = true
                        currentMessageShow.value = msgList[it]
                    }
            ) {
                msgList[it].apply {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row {
                            Text(
                                text = "From :",
                                fontSize = 14.sp,
                                modifier = Modifier.align(CenterVertically)
                            )
                            Text(
                                text = admin_name?:"Admin", fontSize = 14.sp,
                                modifier = Modifier
                                    .padding(10.dp)
                                    .align(CenterVertically)
                                    .background(
                                        color = Teal200.copy(0.3f),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .padding(horizontal = 5.dp, vertical = 3.dp)
                            )

                        }
                        Text(text = message, overflow = TextOverflow.Ellipsis, maxLines = 4)
                        val fmt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        val date = try {
                            fmt.parse(createddate)?.let { it1 ->
                                DateUtils.getRelativeTimeSpanString(
                                    it1.time,
                                    System.currentTimeMillis(),
                                    DateUtils.SECOND_IN_MILLIS
                                )
                            }
                        } catch (e: Exception) {
                            createddate
                        }
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = date.toString(),
                                fontSize = 12.sp,
                                color = Color.Gray,
                                modifier = Modifier.align(Alignment.CenterEnd)
                            )
                        }

                    }
                }

            }
        }
    }

    if (isShowMessageDialog.value) {
        ShowMessageDialog(msg = currentMessageShow.value) {
            isShowMessageDialog.value = !isShowMessageDialog.value
        }
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ShowMessageDialog(msg: MessageModel?, function: () -> Unit) {
    msg?.apply {
        val color = if (isSystemInDarkTheme()) {
            Color.White
        } else {
            Color.Black
        }
        Dialog(
            onDismissRequest = function,
            properties = DialogProperties(usePlatformDefaultWidth = true),
            content = {
                Column(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colors.surface,
                            shape = RoundedCornerShape(5.dp)
                        )
                        .padding(vertical = 10.dp, horizontal = 15.dp)
                ) {
                    Row {
                        Text(
                            text = "From:",
                            color = color,
                            fontSize = 14.sp,
                            modifier = Modifier.align(CenterVertically)
                        )
                        Text(
                            text = msg.admin_name?:"Admin", color = color, fontSize = 14.sp,
                            modifier = Modifier
                                .padding(10.dp)
                                .align(CenterVertically)
                                .background(
                                    color = Teal200.copy(0.3f),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .padding(horizontal = 5.dp, vertical = 3.dp)
                        )

                        val fmt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        val date = try {
                            fmt.parse(createddate)?.let { it1 ->
                                DateUtils.getRelativeTimeSpanString(
                                    it1.time,
                                    System.currentTimeMillis(),
                                    DateUtils.SECOND_IN_MILLIS
                                )
                            }
                        } catch (e: Exception) {
                            createddate
                        }
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Max)) {
                            Text(
                                text = date.toString(),
                                fontSize = 12.sp,
                                color = Color.Gray,
                                modifier = Modifier.align(Alignment.CenterEnd)
                            )
                        }


                    }


                    Text(text = message, color = color, fontSize = 16.sp)



                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Close",
                            fontSize = 16.sp,
                            color = Teal200,
                            modifier = Modifier
                                .padding(10.dp)
                                .align(Alignment.CenterEnd)
                                .clickable {
                                    function()
                                }
                        )
                    }
                }

            },
        )
    }

}
