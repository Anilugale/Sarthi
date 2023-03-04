package com.vk.sarthi.ui.screen

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vk.sarthi.R
import com.vk.sarthi.cache.Cache
import com.vk.sarthi.model.DailyVisitModel
import com.vk.sarthi.model.PersonsVisited
import com.vk.sarthi.ui.nav.BottomNavigationBar
import com.vk.sarthi.ui.nav.Screens
import com.vk.sarthi.ui.theme.FontColor2
import com.vk.sarthi.ui.theme.WindowColor
import com.vk.sarthi.viewmodel.*
import kotlinx.coroutines.launch


@Composable
fun DailyVisit(navigatorController: NavHostController?) {

    val listState = rememberLazyListState()
    val fabVisibility by derivedStateOf {
        listState.firstVisibleItemIndex == 0
    }
    val lazyState = rememberLazyListState()
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
                    if (Cache.villageData == null) {
                        Cache.restoreVillageData(context)
                    }
                    if (Cache.villageData != null) {
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
    ) { it ->

        val model: DailyVisitVM = hiltViewModel()
        val viewModel = remember { model }


        val targetState = viewModel.stateExpose.collectAsState().value

        LaunchedEffect(key1 = Cache.dailyVisitList) {
            Cache.dailyVisitList.clear()
            model.getDailyVisitList(isFromPagination = false)
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
                        swipeRefreshState,
                        lazyState
                    )

                }

                is DailyVisitStateList.Empty -> {
                    ShowEmptyListDailyVisit()
                }

                is DailyVisitStateList.OffLine -> {
                    if (targetState.list.isEmpty()) {
                        ShowEmptyListDailyVisit()
                    } else {
                        val reversed = targetState.list.reversed()
                        LazyColumn(contentPadding = PaddingValues(5.dp), state = lazyState) {
                            items(count = reversed.size,key = { it }) {
                                ShowDailyItemOffline(
                                    reversed[it],
                                    navigatorController
                                )
                            }
                        }
                    }

                }

                is DailyVisitStateList.Failed -> {
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
fun ShowEmptyListDailyVisit() {
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

@Composable
fun ShowDailyList(
    list: List<DailyVisitModel>,
    navigatorController: NavHostController?,
    model: DailyVisitVM,
    swipeRefreshState: SwipeRefreshState,
    lazyState: LazyListState,
) {


    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = {
            swipeRefreshState.isRefreshing = true
            model.getDailyVisitList(true, true)
        },
    ) {
        LazyColumn(contentPadding = PaddingValues(5.dp), state = lazyState) {
            items(count = list.size, key = { it }) {
                ShowDailyItem(list[it], navigatorController)
            }
            if (model.isFooter.value) {
                item {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                        model.getDailyVisitList(isFromPagination = true)
                    }
                }
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
        dailyVisitModel.villagename = if (Cache.villageMap[dailyVisitModel.villageid] != null) {
            Cache.villageMap[dailyVisitModel.villageid]!!.village
        } else {
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

            if (dailyVisitModel.villagename != null && dailyVisitModel.villagename.isNotEmpty()) {
                Text(
                    text = "गावाचे नाव : ${dailyVisitModel.villagename}",
                    fontSize = 16.sp
                )
            }

            if (dailyVisitModel.persons_visited.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.person_visited) + " :- " + dailyVisitModel.persons_visited.size,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(5.dp))
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


@Composable
fun ShowDailyItemOffline(
    dailyVisitModel: VisitOffLineModel,
    navigatorController: NavHostController?,
) {
    val isShowDetails = remember {
        mutableStateOf(false)
    }

    Card(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .clickable {
               // isShowDetails.value = true
                navigatorController?.navigate(Screens.EditDailyVisit.route + "/${dailyVisitModel.id}")
            }
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {

            if (dailyVisitModel.villageName.isNotEmpty()) {
                Text(
                    text = "गावाचे नाव : ${dailyVisitModel.villageName}",
                    fontSize = 16.sp
                )
            }
        }


    }
    if (isShowDetails.value) {
        ShowOfflineDetails(dailyVisitModel) {
            isShowDetails.value = !isShowDetails.value
        }
    }
}
@Composable
fun ShowOfflineDetails(model: VisitOffLineModel, function: () -> Unit) {

    Dialog(
        onDismissRequest = {
            function()
        },
    ) {
        Card(backgroundColor = MaterialTheme.colors.surface) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp)
                    .verticalScroll(state = rememberScrollState())
            ) {
                model.hashMap.forEach { it ->
                    if (it.value.isNotEmpty()) {
                        if(it.key == "persons_visited"){
                            if(it.value == "[]"){
                                return@forEach
                            }
                            val list = Gson().fromJson<ArrayList<PersonsVisited>>(it.value,object :TypeToken<ArrayList<PersonsVisited>>(){}.type )
                            Text(text = stringResource(R.string.person_visited), fontSize = 13.sp)
                            list.forEach {
                                Text(text = "नाव - "+it.name)
                                Text(text = "विषय - "+it.subject)
                                Text(text = "माहिती - "+it.information)
                                Text(text = "सर्वेक्षण - "+it.servey)
                                Spacer(modifier = Modifier.height(3.dp))
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }else{
                            if(it.key != "coordinator_id" &&
                                it.key != "villageid" &&
                                it.key != "visitid" &&
                                it.key != "latitude" &&
                                it.key != "longitude"
                            ) {
                                Text(text = getLabel(it.key, LocalContext.current), fontSize = 13.sp)
                                Text(text = it.value)
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                        }
                    }
                }

                OutlinedButton(onClick = { function() }, modifier = Modifier.align(Alignment.End)) {
                    Text(text = "Okay")
                }

            }
        }
    }

}

fun getLabel(key: String, current: Context): String {
    return when (key) {
        "birthdayinfo" -> {
            current.getString(R.string.birthday_info)
        }
        "rashanshopinfo" -> {
            current.getString(R.string.ration_info)
        }
        "electricityinfo" -> {
            current.getString(R.string.electric_info)
        }
        "drinkingwaterinfo" -> {
            current.getString(R.string.drinking_water_info)
        }
        "watercanelinfo" -> {
            current.getString(R.string.water_canal_info)
        }
        "schoolinfo" -> {
            current.getString(R.string.school_info)
        }
        "primarycarecenterinfo" -> {
            current.getString(R.string.prathamik_info)
        }
        "veterinarymedicineinfo" -> {
            current.getString(R.string.pashu_info)
        }
        "govservantinfo" -> {
            current.getString(R.string.gov_emp_info)
        }
        "politicalinfo" -> {
            current.getString(R.string.politics_info)
        }
        "deathpersoninfo" -> {
            current.getString(R.string.death_person_info)
        }
        "newschemes" -> {
            current.getString(R.string.yojna_list)
        }
        "devinfo" -> {
            current.getString(R.string.development_info)
        }
        "otherinfo" -> {
            current.getString(R.string.other_info)
        }
        else -> {
            key
        }
    }
}






