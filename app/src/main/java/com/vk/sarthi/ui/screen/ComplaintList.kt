package com.vk.sarthi.ui.screen

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Attachment
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.vk.sarthi.R
import com.vk.sarthi.WifiService
import com.vk.sarthi.cache.Cache
import com.vk.sarthi.model.*
import com.vk.sarthi.ui.nav.BottomNavigationBar
import com.vk.sarthi.ui.nav.Screens
import com.vk.sarthi.service.Service
import com.vk.sarthi.ui.theme.*
import com.vk.sarthi.utli.Constants
import com.vk.sarthi.viewmodel.MainViewModel
import com.vk.sarthi.viewmodel.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@Composable
fun ComplaintListUI(navigatorController: NavHostController?) {

    val model: ComplaintViewModel = hiltViewModel()
    val viewModel = remember { model }
    val targetState = viewModel.stateExpose.collectAsState().value
    val swipeRefreshState = rememberSwipeRefreshState(false)
    val activityViewModel: MainViewModel = hiltViewModel()
    activityViewModel.getVillageList()
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(title = { Text(text = LocalContext.current.getString(R.string.complaint_list)) },
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
                })
        },
        backgroundColor = if (isSystemInDarkTheme()) Color.Black else WindowColor,
        drawerContent = { DrawerView(navigatorController, Screens.ComplaintList.route) },
    ) {

        Box(modifier = Modifier.padding(paddingValues = it)) {
            when (targetState) {

                is Status.Process -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        ShowListProgress()
                    }
                }

                is Status.Success -> {
                    swipeRefreshState.isRefreshing = false
                    ShowList(targetState, navigatorController, model, swipeRefreshState)

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
                                text = "No comments available",
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                }
                is Status.Error->{
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = targetState.error,
                            textAlign = TextAlign.Center
                        )
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
    }

}


@Composable
fun ShowList(
    value1: Status.Success,
    navigatorController: NavHostController?,
    model: ComplaintViewModel,
    swipeRefreshState: SwipeRefreshState
) {
    val lazyState = rememberLazyListState()

    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = {
            model.getData(true)
            swipeRefreshState.isRefreshing = true
        },
    ) {
        LazyColumn(contentPadding = PaddingValues(5.dp), state = lazyState) {
            items(count = value1.list.size, key = { it }) {
                ShowCommentItem(value1.list[it], navigatorController)
            }
        }
    }
}

@Composable
fun ShowCommentItem(model: ComplaintModel, navigatorController: NavHostController?) {
    val context = LocalContext.current
    Card(
        elevation = 2.dp, modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp, horizontal = 10.dp)
            .clickable {
                navigatorController?.navigate("${Screens.ComplaintDetails.route}/${model.ticket_id}")
            }
    ) {

        val txtColor = if (isSystemInDarkTheme()) {
            FontColor1Dark
        } else {
            FontColor1
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {

            model.isurgent?.let {
                if (it == 1) {
                    Text(
                        text = "URGENT",
                        color = Color.White,
                        modifier = Modifier
                            .background(color = Color.Red, RoundedCornerShape(20.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                            .shadow(AppBarDefaults.TopAppBarElevation),
                        fontSize = 13.sp,

                        )
                }
            }
            Spacer(modifier = Modifier.height(5.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "BS-" + String.format("%06d", model.ticket_id),
                    modifier = Modifier
                        .align(CenterStart),
                    fontSize = 12.sp,
                    color = FontColor2
                )

                Text(
                    text = model.ticket_date,
                    modifier = Modifier
                        .align(CenterEnd),
                    fontSize = 12.sp,
                    color = FontColor2
                )
            }

            Text(
                text = model.categorie,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                fontSize = 14.sp,
                color = txtColor
            )

            Text(
                text = model.ticket_exp,
                fontSize = 14.sp,
                color = txtColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 5.dp),
                textAlign = TextAlign.Start
            )


            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                if (!model.attachments.isNullOrEmpty()) {
                    Row(modifier = Modifier
                        .align(CenterStart)
                        .wrapContentWidth()
                        .clickable {
                            val url =
                                "https://shirdiyuva.in/uploads/complaints/${model.attachments}"
                            Log.d("@@", "ShowCommentItem: $url")
                            val intent = Intent().apply {
                                action = Intent.ACTION_VIEW
                                data = Uri.parse(url)
                            }
                            context.startActivity(intent)
                        }
                    ) {
                        Icon(
                            Icons.Outlined.Attachment,
                            contentDescription = "",
                            tint = FontColor2,
                            modifier = Modifier
                                .height(20.dp)
                                .align(CenterVertically),
                        )
                        Text(
                            text = "Attachment",
                            modifier = Modifier
                                .wrapContentWidth()
                                .padding(start = 5.dp, top = 5.dp, bottom = 5.dp)
                                .align(CenterVertically),
                            fontSize = 12.sp,
                            color = FontColor2,
                            textAlign = TextAlign.Center
                        )


                    }
                }

                Text(
                    text = model.ticket_status,
                    modifier = Modifier
                        .background(getChipColor(model.ticket_status), RoundedCornerShape(5.dp))
                        .border(
                            width = 1.dp,
                            shape = RoundedCornerShape(5.dp),
                            color = FontColor2
                        )
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                        .wrapContentWidth()
                        .align(CenterEnd),
                    fontSize = 13.sp,
                    color = Color.White
                )
            }
        }

    }
}

fun getChipColor(ticketStatus: String): Color {
    return when (ticketStatus.lowercase()) {
        "inprocess" -> {
            chipProgress
        }
        "rejected" -> {
            chipReject
        }
        else -> {
            chipClose
        }
    }
}

@HiltViewModel
class ComplaintViewModel @Inject constructor(private val service: Service) : ViewModel() {
    private var state: MutableStateFlow<Status> = MutableStateFlow(Status.Empty)
    val stateExpose = state.asStateFlow()
    private var isFooterShow = true
    private var postList = arrayListOf<ComplaintModel>()


    init {
        getData()
    }

    fun getData(force: Boolean = false) = viewModelScope.launch {
        if (WifiService.instance.isOnline()) {
        if (Cache.commentList.isEmpty() || force) {

                state.value = Status.Process
                viewModelScope.launch(Dispatchers.IO) {
                    val commentList = service.getComplaintList(CoordinatoridMode(Cache.loginUser!!.id))

                    viewModelScope.launch(Dispatchers.Main) {
                        try {
                            if (commentList.body() != null) {
                                if (force) {
                                    Cache.commentList.clear()
                                    postList.clear()
                                }

                                Cache.commentList.addAll(commentList.body()!!.data)
                                postList.addAll(Cache.commentList)
                                if (postList.isEmpty()) {
                                    state.value = Status.Empty
                                } else {
                                    state.value = Status.Success(postList, isFooterShow)
                                }
                            } else {
                                state.value = Status.Error(commentList.message())

                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                            state.value = e.message?.let { Status.Error(it) }!!
                        }
                    }
                }
            }else{
            postList.addAll(Cache.commentList)
            state.value = Status.Success(postList, isFooterShow)
            }

        } else {
            Status.Error(Constants.NO_INTERNET)
        }
    }


}


@Composable
@Preview
fun PreviewComplaintList() {

    ShowCommentItem(
        ComplaintModel(
            "sdfsdf",
            "pani ani gas",
            "d12",
            arrayListOf(
                Comment(
                    comment = "comment text for ui showing here",
                    comment_id = 1,
                    comment_attachment = "comment Attachment",
                    comment_date = "12/12/2012",
                    updated_at = "12/12/2012",
                )
            ),
            3,
            "weweewee",
            "weweeewe",
            12,
            "Open",
            "9876546545",
            1
        ), null
    )
}


