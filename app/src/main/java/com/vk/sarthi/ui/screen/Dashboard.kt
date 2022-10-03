package com.vk.sarthi.ui.screen

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.vk.sarthi.cache.Cache
import com.vk.sarthi.model.Village
import com.vk.sarthi.ui.nav.Screens
import com.vk.sarthi.ui.theme.FontColor2
import com.vk.sarthi.ui.theme.Teal200
import com.vk.sarthi.ui.theme.WindowColor
import com.vk.sarthi.utli.SettingPreferences
import com.vk.sarthi.viewmodel.MainViewModel
import com.vk.sarthi.viewmodel.VillageState
import kotlinx.coroutines.launch


@Composable
fun Dashboard(
    navigator: NavHostController?
) {

    val context = LocalContext.current
    val showDialog = remember { mutableStateOf(false) }
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val activityViewModel: MainViewModel = hiltViewModel()
    val modeVM = remember {
        activityViewModel
    }

    val value = modeVM.stateExpose.collectAsState().value
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(title = { Text(text = "Dashboard") }, actions = {
                Icon(
                    imageVector = Icons.Outlined.Logout,
                    contentDescription = "menu",
                    modifier = Modifier
                        .padding(10.dp)
                        .clickable { showDialog.value = true }
                )
            }, navigationIcon = {
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


        drawerContent = { DrawerView(navigator, Screens.Dashboard.route) },

        backgroundColor = if (isSystemInDarkTheme()) Color.Black else WindowColor
    ) {

        when (value) {
            VillageState.Empty -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
            is VillageState.Failed -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(text = "No data Found",Modifier.align(Alignment.Center))
                }
            }
            VillageState.Success -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    if (Cache.villageData != null) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(), horizontalAlignment = Alignment.Start
                        ) {
                            items(Cache.villageData!!.villages.size, key = { it }) {
                                ShowVillageItem(Cache.villageData!!.villages[it])
                            }
                        }
                    }

                    val uriTag = "URI"
                    val url =
                        "https://twitter.com/RVikhePatil?ref_src=twsrc%5Egoogle%7Ctwcamp%5Eserp%7Ctwgr%5Eauthor"
                    val uriHandler = LocalUriHandler.current

                    val annotatedString = buildAnnotatedString {
                        append("Follow - us")

                        addStringAnnotation(
                            tag = uriTag,
                            annotation = url,
                            start = 0,
                            end = url.length
                        )
                    }

                    ClickableText(
                        text = annotatedString,
                        onClick = { position ->
                            val annotations = annotatedString.getStringAnnotations(
                                uriTag,
                                start = position,
                                end = position
                            )
                            annotations.firstOrNull()?.let {
                                uriHandler.openUri(it.item)
                            }
                        },
                        style = TextStyle(
                            fontSize = 12.sp,
                            color = FontColor2,
                            textDecoration = TextDecoration.Underline
                        ),
                        modifier = Modifier
                            .padding(8.dp)
                            .align(Alignment.BottomCenter),
                    )
                }
            }
        }


    }
    if (showDialog.value) {
        showLogout(context, navigator, showDialog)
    }

}

@Composable
fun ShowVillageItem(it: Village) {
    val current = LocalContext.current
    Card(modifier = Modifier
        .padding(10.dp)
        .fillMaxWidth()) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = "गावाचे नाव : ${it.village}",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(text = "जिल्ह्याचे नाव : ${it.taluka}", fontSize = 12.sp, color = FontColor2)
            Spacer(modifier = Modifier.height(5.dp))
            Text(text = "माहिती",
                fontSize = 14.sp,
                color = Color.White,
                modifier = Modifier
                    .background(
                        color = Teal200.copy(0.6f),
                        RoundedCornerShape(20.dp)
                    )
                    .padding(vertical = 5.dp, horizontal = 10.dp)
                    .clickable {
                        var intent = Intent(Intent.ACTION_VIEW)
                        intent.setDataAndType(
                            Uri.parse("https://shirdiyuva.in/" + it.infourl),
                            "application/pdf"
                        )
                        intent = Intent.createChooser(intent, "Open File")
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        current.startActivity(intent)
                    }
            )


        }
    }
}


@Composable
fun showLogout(context: Context, navigator: NavHostController?, showDialog: MutableState<Boolean>) {
    AlertDialog(
        onDismissRequest = {
            showDialog.value = false
        },
        confirmButton = {
            TextButton(onClick = {
                showDialog.value = false
                SettingPreferences.clearCache(context)
                Cache.clear()
                navigator?.navigate(Screens.Login.route) {
                    popUpTo(Screens.DailyVisit.route) {
                        inclusive = true
                    }
                }
            })
            { Text(text = stringResource(id = android.R.string.ok)) }
        },
        text = { Text(text = "Do you want logout?") },
        dismissButton = {
            TextButton(onClick = {
                showDialog.value = false
            })
            { Text(text = stringResource(id = android.R.string.cancel)) }
        }
    )
}

@Composable
@Preview
fun DashboardPreview() {
    Dashboard(navigator = null)

}