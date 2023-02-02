package com.vk.sarthi.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.vk.sarthi.R
import com.vk.sarthi.WifiService
import com.vk.sarthi.cache.Cache
import com.vk.sarthi.model.DailyVisitModel
import com.vk.sarthi.model.Village
import com.vk.sarthi.ui.nav.BottomNavigationBar
import com.vk.sarthi.ui.nav.Screens
import com.vk.sarthi.ui.theme.FontColor2
import com.vk.sarthi.ui.theme.WindowColor
import com.vk.sarthi.viewmodel.DailyVisitStateList
import com.vk.sarthi.viewmodel.DailyVisitVM
import com.vk.sarthi.viewmodel.MainViewModel
import kotlinx.coroutines.launch


@Composable
fun DailyVisit(navigatorController: NavHostController?) {

    val listState = rememberLazyListState()
    val fabVisibility by derivedStateOf {
        listState.firstVisibleItemIndex == 0
    }
    val activityViewModel: MainViewModel = hiltViewModel()
    activityViewModel.getVillageList()
    val context = LocalContext.current
    val showDialog = remember { mutableStateOf(false) }
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    Scaffold(
        scaffoldState = scaffoldState, topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = LocalContext.current.getString(R.string.daily_visit),
                        fontSize = 18.sp
                    )
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
                },
           /*     actions = {
                    Icon(
                        imageVector = Icons.Outlined.Logout,
                        contentDescription = "menu",
                        modifier = Modifier
                            .padding(10.dp)
                            .clickable { showDialog.value = true }
                    )
                }*/
            )
        }, drawerContent = { DrawerView(navigatorController, Screens.DailyVisit.route) },
        floatingActionButton = {
            val density = LocalDensity.current
            AnimatedVisibility(
                visible = fabVisibility,
                enter = slideInVertically {
                    with(density) { 40.dp.roundToPx() }
                } + fadeIn(),
                exit = fadeOut(
                    animationSpec = keyframes {
                        this.durationMillis = 120
                    }
                )
            ) {
                FloatingActionButton(onClick = {
                    if (Cache.villageData==null) {
                        Cache.restoreVillageData(context)
                        context.toast("Offline")
                    }
                    if (Cache.villageData!=null) {
                        navigatorController?.navigate(Screens.AddDailyVisit.route + "/0")
                    }
                }) {
                    Icon(Icons.Filled.Add, "")
                }
            }
        },
        bottomBar = {
            BottomNavigationBar(navController = navigatorController!!)
        },
        backgroundColor = if (isSystemInDarkTheme()) Color.Black else WindowColor
    ) {

        val model: DailyVisitVM = hiltViewModel()
        val viewModel = remember { model }


        val targetState = viewModel.stateExpose.collectAsState().value

        LaunchedEffect(key1 = Cache.dailyVisitList) {
            model.getDailyVisitList()
        }
        val swipeRefreshState = rememberSwipeRefreshState(false)
        Box(modifier = Modifier.padding(paddingValues = it)) {
            when (targetState) {

                is DailyVisitStateList.Process -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        ShowListProgress()
                    }
                }

                is DailyVisitStateList.Success -> {
                    swipeRefreshState.isRefreshing = false
                    ShowDailyList(
                        targetState.list,
                        navigatorController,
                        model,
                        swipeRefreshState
                    )

                }

                is DailyVisitStateList.Empty -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
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
                                text = "No data available",
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                }
                is DailyVisitStateList.Failed->{
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = targetState.msg,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                else -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No data Found",
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
    if (showDialog.value) {
        showLogout(context, navigatorController, showDialog)
    }

}

@Composable
fun ShowDailyList(
    list: List<DailyVisitModel>,
    navigatorController: NavHostController?,
    model: DailyVisitVM,
    swipeRefreshState: SwipeRefreshState,
) {

    val lazyState = rememberLazyListState()

    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = {
            swipeRefreshState.isRefreshing = true
            model.getDailyVisitList(true)
        },
    ) {
        LazyColumn(contentPadding = PaddingValues(5.dp), state = lazyState) {
            items(count = list.size, key = { it }) {
                ShowDailyItem(list[it], navigatorController)
            }
        }
    }

}

@Composable
fun ShowDailyItem(
    dailyVisitModel: DailyVisitModel,
    navigatorController: NavHostController?,
) {
    if (dailyVisitModel.villagename.isNullOrEmpty()) {
        dailyVisitModel.villagename  = if(Cache.villageMap[dailyVisitModel.villageid]!=null){
            Cache.villageMap[dailyVisitModel.villageid]!!.village
        }else{
            ""
        }
    }

    Card(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .clickable {
                navigatorController?.navigate(Screens.DailyVisitDetails.route + "/${dailyVisitModel.id}")
            }

    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {

            if (dailyVisitModel.persons_visited.isNotEmpty()) {
                Text(
                    text = "No of Persons visited :- " + dailyVisitModel.persons_visited.size,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(5.dp))
            }

            if (dailyVisitModel.villagename!=null && dailyVisitModel.villagename.isNotEmpty()) {
                Text(
                    text = "Village Name/गावाचे नाव : ${dailyVisitModel.villagename}",
                    fontSize = 14.sp
                )
            }


            Spacer(modifier = Modifier.height(5.dp))
            val date = if (dailyVisitModel.updated_at.isNullOrBlank()) {
                "Created on ${dailyVisitModel.createddate}"
            } else {
                "Updated on ${dailyVisitModel.updated_at}"
            }
            Text(
                text = date, fontSize = 12.sp,
                color = FontColor2
            )
        }
    }


}



