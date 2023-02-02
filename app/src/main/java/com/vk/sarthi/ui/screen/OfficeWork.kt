package com.vk.sarthi.ui.screen

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Attachment
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.vk.sarthi.R
import com.vk.sarthi.WifiService
import com.vk.sarthi.cache.Cache
import com.vk.sarthi.model.OfficeWorkModel
import com.vk.sarthi.ui.nav.BottomNavigationBar
import com.vk.sarthi.ui.nav.Screens
import com.vk.sarthi.ui.theme.*
import com.vk.sarthi.viewmodel.OfficeWorkViewModel
import com.vk.sarthi.viewmodel.Status
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun OfficeWork(navigatorController: NavHostController?) {
    val listState = rememberLazyListState()

    val isProgressShow = remember {
        mutableStateOf(false)
    }
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val current = LocalContext.current
    val model: OfficeWorkViewModel = hiltViewModel()
    val viewModel = remember { model }

    val syncState = model.synchStateExpose.collectAsState().value
    var showProgressDialog  = remember {
        mutableStateOf(false)
    }
    when (syncState) {
        is Status.ProcessDialog -> {
            showProgressDialog.value =  syncState.isShow
            if (syncState.isShow == false) {

            Cache.clearOfflineOfficeWork(current)
            }
        }
        else->{

        }
    }
    if (showProgressDialog.value) {
        ShowProgressDialog()
    }
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = {
                    Row {
                        Text(
                            text = current.getString(R.string.office_work),
                            fontSize = 18.sp
                        )
                        AnimatedVisibility(visible = isProgressShow.value) {
                            HeaderBarProgress(
                                Modifier
                                    .size(30.dp)
                                    .align(CenterVertically)
                                    .padding(start = 10.dp)
                            )
                        }
                    }

                },
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
                }, actions = {
                    if(Cache.isOfflineOfficeWork(current) && WifiService.instance.isOnline()) {
                        IconButton(onClick = {
                            scope.launch {
                                viewModel.syncData(Cache.officeWorkOfflineList)
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Refresh,
                                contentDescription = "sync"
                            )
                        }
                    }
                }
            )
        },
        drawerContent = { DrawerView(navigatorController, Screens.DailyVisit.route) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navigatorController?.navigate(Screens.AddDailyWork.route + "/0")
            }
            ) {
                Icon(Icons.Filled.Add, "")
            }
        },
        bottomBar = {
            BottomNavigationBar(navController = navigatorController!!)
        }
    ) {
        Box(modifier = Modifier.padding(paddingValues = it)) {
            ShowOfficeListUI(navigatorController, listState, isProgressShow,viewModel)
        }

    }
}

@Composable
fun ShowOfficeListUI(
    navigatorController: NavHostController?,
    listState: LazyListState,
    isProgressShow: MutableState<Boolean>,
    model: OfficeWorkViewModel
) {

    val context = LocalContext.current
    val targetState = model.stateExpose.collectAsState().value


    LaunchedEffect(key1 = Cache.officeWorkModelList) {
        model.getList()
    }

    when (targetState) {

        is Status.Process -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                ShowListProgress()
            }
        }

        is Status.ProcessHeader -> {
            isProgressShow.value = targetState.isShow
        }

        is Status.Error->{
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = targetState.error,
                    textAlign = TextAlign.Center
                )
            }
        }


        is Status.SuccessOffice -> {
            ShowListOffice(targetState.list, navigatorController, listState, model)
        }

        is Status.SuccessDelete -> {
            Toast.makeText(context, targetState.msg, Toast.LENGTH_SHORT).show()
            isProgressShow.value = false
            ShowListOffice(targetState.list, navigatorController, listState, model)
        }

        is Status.FailedDelete -> {
            isProgressShow.value = false
            Toast.makeText(context, targetState.msg, Toast.LENGTH_SHORT).show()
        }

        is Status.Empty -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        modifier = Modifier
                            .width(50.dp)
                            .height(50.dp),
                        contentDescription = ""
                    )

                    Spacer(modifier = Modifier.width(5.dp))

                    Text(
                        text = "No Work available",
                        textAlign = TextAlign.Center
                    )
                }
            }

        }

        else -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "No data Found",
                    textAlign = TextAlign.Center
                )
            }
        }
    }


}

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun ShowListOffice(
    list: ArrayList<OfficeWorkModel>,
    navigatorController: NavHostController?,
    listState: LazyListState,
    model: OfficeWorkViewModel,
) {
    val modalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmStateChange = {
            it != ModalBottomSheetValue.HalfExpanded
        }
    )
    val clickID = remember {
        mutableStateOf("0")
    }
    val coroutineScope = rememberCoroutineScope()

    ModalBottomSheetLayout(
        sheetState = modalBottomSheetState,
        sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        sheetContent = {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(150.dp)

                    .background(
                        if (isSystemInDarkTheme()) {
                            StickyHeaderDark
                        } else (StickyHeaderLight),

                        )
            ) {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(10.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Menu", modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp),
                        color = FontColor2,
                        textAlign = TextAlign.Center
                    )

                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            coroutineScope.launch {
                                modalBottomSheetState.hide()
                                navigatorController?.navigate(Screens.AddDailyWork.route + "/${clickID.value}")

                            }
                        }
                        .padding(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "",
                            tint = FontColor2
                        )
                        Text(
                            text = "Edit", modifier = Modifier
                                .padding(start = 10.dp)
                                .align(CenterVertically), fontSize = 18.sp
                        )
                    }

                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            coroutineScope.launch {
                                model.deleteWork(list, clickID.value.toInt())
                                modalBottomSheetState.hide()
                            }
                        }
                        .padding(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "",
                            tint = FontColor2
                        )
                        Text(
                            text = "Delete", modifier = Modifier
                                .padding(start = 10.dp)
                                .align(CenterVertically), fontSize = 18.sp
                        )
                    }
                }
            }
        },
        sheetBackgroundColor = Color.Transparent,
        scrimColor = Color.Unspecified
    ) {

        LazyColumn(state = listState) {

            val group = list.groupBy {
                it.createddate.subSequence(
                    0,
                    list[0].createddate.lastIndexOf(" ")
                )
            }
            group.forEach { (section, sectionModel) ->
                stickyHeader {
                    StickHeader(section.toString())
                }
                items(sectionModel) { model ->
                    WorkDailyUI(model, clickID, modalBottomSheetState, coroutineScope)
                }
            }
        }

    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun WorkDailyUI(
    model: OfficeWorkModel,
    clickID: MutableState<String>,
    modalBottomSheetState: ModalBottomSheetState?,
    coroutineScope: CoroutineScope
) {
    val context = LocalContext.current

    val color = if (isSystemInDarkTheme()) {
        FontColor1Dark
    } else {
        FontColor1
    }

    Box(modifier = Modifier
        .fillMaxWidth()
        .clickable {
            coroutineScope.launch {
                modalBottomSheetState?.hide()
            }
        }) {
        Icon(
            Icons.Outlined.MoreVert,
            contentDescription = "",
            tint = color,
            modifier = Modifier
                .height(40.dp)
                .width(40.dp)
                .padding(10.dp)
                .fillMaxWidth(0.2f)
                .align(Alignment.TopEnd)
                .clickable {
                    clickID.value = model.id.toString()
                    coroutineScope.launch {
                        if (modalBottomSheetState != null) {
                            if (modalBottomSheetState.isVisible) {
                                modalBottomSheetState.hide()
                            } else {
                                modalBottomSheetState.show()
                            }
                        }

                    }
                }
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
        ) {
            Text(
                text = model.comment, modifier = Modifier
                    .padding(vertical = 10.dp)
                    .fillMaxWidth(0.95f)
            )


            val date = if (model.updated_at != null) {
                "Updated on - ${model.updated_at.subSequence(0, model.updated_at.lastIndexOf(":"))}"

            } else {
                "Created on - ${
                    model.createddate.subSequence(
                        0,
                        model.createddate.lastIndexOf(":")
                    )
                }"
            }
            Text(
                text = date,
                modifier = Modifier
                    .fillMaxWidth(),
                fontSize = 12.sp,
                color = FontColor2,
            )


            Box(modifier = Modifier.fillMaxWidth()) {
                if (!model.attachments.isNullOrEmpty()) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(vertical = 5.dp)
                            .clickable {
                                val url = "https://shirdiyuva.in/${model.attachments}"
                                Log.d("@@", "ShowCommentItem: $url")
                                val intent = Intent().apply {
                                    action = Intent.ACTION_VIEW
                                    data = Uri.parse(url)
                                }
                                context.startActivity(intent)
                            }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Attachment,
                            contentDescription = "attachment",
                            tint = FontColor2
                        )
                        Text(
                            text = "Attachment", modifier = Modifier
                                .padding(horizontal = 5.dp)
                                .align(CenterVertically),
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            color = FontColor2
                        )
                    }

                }
            }

            Divider(thickness = 0.5.dp, color = FontColor2.copy(0.5f))
        }

    }

}


@Composable
fun StickHeader(section: String) {
    Text(
        text = section,
        textAlign = TextAlign.Start,
        fontSize = 18.sp,
        fontWeight = FontWeight.Normal,
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isSystemInDarkTheme()) {
                    StickyHeaderDark
                } else {
                    StickyHeaderLight
                }
            )
            .padding(horizontal = 10.dp, vertical = 5.dp)

    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
@Preview
fun OfficeWorkPreview() {
    WorkDailyUI(
        model = OfficeWorkModel(
            attachments = "atachement",
            comment = "sdfsd sdfsdf sdfsdf sdf sdf hi this is the way u sing a song sdf",
            id = 1,
            createddate = "12/12/2022 12:20:10",
            updated_at = "12/12/2022 12:20:10",
            coordinator_id = 1,
            mobileno = "121223213123"
        ),
        clickID = mutableStateOf("0"),
        modalBottomSheetState = null,
        coroutineScope = rememberCoroutineScope()
    )
}

@Composable
@Preview
fun stickHeaderPreview() {
    StickHeader("2022-02-12")
}