package com.vk.sarthi.utli.com.vk.sarthi.ui.screen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.vk.sarthi.R
import com.vk.sarthi.cache.Cache
import com.vk.sarthi.model.Village
import com.vk.sarthi.model.YojnaModel
import com.vk.sarthi.model.YojsnaPostReq
import com.vk.sarthi.ui.nav.Screens
import com.vk.sarthi.ui.screen.DrawerView
import com.vk.sarthi.ui.screen.DropDownSpinner
import com.vk.sarthi.ui.screen.toast
import com.vk.sarthi.ui.theme.FontColor1
import com.vk.sarthi.ui.theme.FontColor1Dark
import com.vk.sarthi.ui.theme.FontColor2
import com.vk.sarthi.ui.theme.WindowColor
import com.vk.sarthi.utli.com.vk.sarthi.viewmodel.YojnaState
import com.vk.sarthi.utli.com.vk.sarthi.viewmodel.YojnaViewModel
import kotlinx.coroutines.launch

@Composable
fun YojnaList(navigator: NavHostController) {

    val model: YojnaViewModel = hiltViewModel()
    val viewModel = remember { model }
    val targetState = viewModel.stateExpose.collectAsState().value

    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()


    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(title = { Text(text = LocalContext.current.getString(R.string.yojna_list)) },
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
        drawerContent = { DrawerView(navigator, Screens.ComplaintList.route) },
    ) {


        when (targetState) {
            YojnaState.Empty ->
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "No Data Found")
                }
            is YojnaState.Failed -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = targetState.msg)
                }
            }
            YojnaState.Progress -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is YojnaState.Success -> {
                showYojnaList(model = model, targetState.list)
            }
            is YojnaState.SuccessToast -> {
                Toast.makeText(LocalContext.current,targetState.msg,Toast.LENGTH_SHORT).show()
                LaunchedEffect(key1 = targetState ){
                    navigator.navigate(Screens.Dashboard.route) {
                        popUpTo(0)
                    }
                }

            }

            else -> {

            }
        }
    }
}

@Composable
fun showYojnaList(model: YojnaViewModel, yojnaList: ArrayList<YojnaModel>) {
    val selectedVillage: Village? = null
    var ganName by remember { mutableStateOf(selectedVillage?.gan ?: "") }
    var villageName by remember { mutableStateOf(selectedVillage) }
    val list = Cache.villageData!!.villages.groupBy { it.gan }
    val gatList = list.keys
    val current = LocalContext.current
    val swipeRefreshState = rememberSwipeRefreshState(false)
    val selectedList = mutableStateListOf<Int>()
    Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn {
                item(key = "header") {
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
                }
                items(count = yojnaList.size, key = { it }) {
                    val yojna = yojnaList[it]

                    if (yojna.isCheck == null) {
                        yojna.isCheck = mutableStateOf(false)
                    }
                    Card(modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth()
                        .clickable {
                            if (villageName != null) {
                                yojna.isCheck.value = !yojna.isCheck.value
                                if (yojna.isCheck.value) {
                                    selectedList.add(yojna.id)
                                } else {
                                    selectedList.remove(yojna.id)
                                }
                            } else {
                                Toast
                                    .makeText(
                                        current,
                                        "कृपया प्रथम गाव निवडा!",
                                        Toast.LENGTH_SHORT
                                    )
                                    .show()
                            }

                        }) {

                        Column(Modifier.padding(10.dp)) {
                            Text(
                                text = yojna.schemename,
                                fontSize = 16.sp,
                                color = if (isSystemInDarkTheme()) {
                                    FontColor1Dark
                                } else {
                                    FontColor1
                                }
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                            Text(text = yojna.type, fontSize = 14.sp, color = FontColor2)
                            Spacer(modifier = Modifier.height(5.dp))
                            Text(text = yojna.form, fontSize = 14.sp, color = FontColor2)

                            Checkbox(checked = yojna.isCheck.value, onCheckedChange = null)
                        }

                    }
                }

                item {
                    Button(
                        onClick = {
                            if (selectedList.isNotEmpty()) {
                                Log.d("@@", "showYojnaList: ${selectedList.toList()}")
                                val req = YojsnaPostReq(coordinatorid = Cache.loginUser!!.id,
                                    village_id = villageName!!.id.toString(),
                                    yojana = selectedList.toList()
                                )
                                model.sendYojna(req)
                            }else{
                                current.toast("किमान एक योजना निवडा!")
                            }

                        },
                        modifier = Modifier
                            .padding(vertical = 5.dp, horizontal = 10.dp)
                            .fillMaxWidth()
                    ) {
                        Text(text = "Submit")
                    }
                }
            }

    }
}
