package com.vk.sarthi.ui.screen

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.R
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import com.valentinilk.shimmer.shimmer
import com.vk.sarthi.cache.Cache
import com.vk.sarthi.model.Village
import com.vk.sarthi.ui.nav.Screens
import com.vk.sarthi.ui.theme.FontColor2
import com.vk.sarthi.ui.theme.Teal200


@Composable
fun ShowListProgress() {

    LazyColumn(modifier= Modifier.fillMaxWidth()) {
     items(8, key = {it}){
         ShimmerItem()
     }
    }
}

@Composable
fun ShimmerItem() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shimmer(),
        contentAlignment = Alignment.Center
    ) {
        Column(modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .background(Color.Gray.copy(alpha = 0.5f))
            .padding(10.dp)) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(15.dp)
                    .background(Color.Gray)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .height(15.dp)
                    .background(Color.Gray)

            )

            Box(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .padding(vertical = 5.dp)
                        .height(15.dp)
                        .align(Alignment.CenterStart)
                        .background(Color.Gray)

                )

                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .padding(vertical = 5.dp)
                        .height(15.dp)
                        .align(Alignment.CenterEnd)
                        .background(Color.Gray)

                )
            }
        }

    }
}


@Composable
fun HeaderBarProgress(modifier: Modifier) {
    CircularProgressIndicator(
        modifier = modifier,
        strokeWidth = 2.dp
    )
}

@Composable
@Preview
fun PreviewComplaintList1() {
    ShowListProgress()
}


@Composable
fun DrawerView(navigator: NavHostController?,route :String) {
    val current = LocalContext.current
    Box(modifier = Modifier
        .fillMaxWidth()){
        Column {

            Card(modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp), elevation = 4.dp) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(text = "Name - ${Cache.loginUser!!.name}")
                    Text(text = "Mobile No - ${Cache.loginUser!!.mobileno}", color = FontColor2, fontSize = 12.sp)
                }
            }
            DrawerItems(name = "गावाची माहिती", callback = {
                navigator?.navigate(Screens.Dashboard.route) {
                    popUpTo(0)
                }
            }, isSelected = route == Screens.Dashboard.route)

            DrawerItems(name = "रोजचे काम", callback = {
                navigator?.navigate(Screens.DailyVisit.route) {
                    popUpTo(0)
                }
            }, isSelected = route == Screens.DailyVisit.route)

            DrawerItems(name = "तक्रारी", callback = {
                navigator?.navigate(Screens.ComplaintList.route) {
                    popUpTo(0)
                }
            }, isSelected = route == Screens.ComplaintList.route)

            DrawerItems(name = "योजनांची यादी", callback = {
                navigator?.navigate(Screens.YojnaList.route) {
                    popUpTo(0)
                }
            }, isSelected = route == Screens.YojnaList.route)

            DrawerItems(name = "वेळापत्रक", callback = {  current.toast("Coming  soon..") }, isSelected = route == Screens.AddComment.route)

            DrawerItems(name = "संदेश", callback = {
                navigator?.navigate(Screens.MessageList.route) {
                    popUpTo(0)
                }
            },
                isSelected = route == Screens.MessageList.route
            )

        }
    }

}



@Composable
fun DrawerItems(name :String,callback:()->Unit,isSelected :Boolean){
    Spacer(modifier = Modifier.height(5.dp))
    Text(text = name, modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 10.dp)
        .clickable {
            callback()
        }
        .background(
            color = if (isSelected) Teal200.copy(alpha = 0.5f) else Color.Transparent,
            shape = RoundedCornerShape(15.dp)
        )
        .padding(10.dp)
    )
}

 fun Context.toast(text:String){
     Toast.makeText(this,text,Toast.LENGTH_SHORT).show()
 }

private val ELEMENT_HEIGHT = 48.dp


@Composable
fun DropDownSpinner(
    modifier: Modifier = Modifier,
    defaultText: String = "Select...",
    selectedItem: Village?,
    onItemSelected: (Int, Village) -> Unit,
    itemList: List<Village>?,
) {
    var isOpen by remember { mutableStateOf(false) }

    Box(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colors.surface)
            .height(ELEMENT_HEIGHT),
        contentAlignment = Alignment.CenterStart
    ) {
        if (selectedItem == null || selectedItem.toString().isEmpty()) {
            Text(
                text = defaultText,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 3.dp),
                color = MaterialTheme.colors.onSurface.copy(.45f)
            )
        }

        Text(
            text = selectedItem?.village?:"",
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 32.dp, bottom = 3.dp),
            color = MaterialTheme.colors.onSurface
        )


        DropdownMenu(
            modifier = Modifier.fillMaxWidth(.85f),
            expanded = isOpen,
            onDismissRequest = {
                isOpen = false
            },
        ) {
            itemList?.forEachIndexed { index, item ->
                DropdownMenuItem(
                    onClick = {
                        isOpen = false
                        onItemSelected(index, item)
                    }
                ) {
                    Text(item.village)
                }
            }
        }

        Icon(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 8.dp)
                .size(24.dp),

            imageVector = Icons.Outlined.ArrowDropDown,
            contentDescription = "Dropdown"
        )

        Spacer(
            modifier = Modifier
                .matchParentSize()
                .background(Color.Transparent)
                .clickable(
                    onClick = { isOpen = true }
                )
        )
    }
}


@Composable
fun DropDownSpinner(
    modifier: Modifier = Modifier,
    defaultText: String = "Select...",
    selectedItem: String,
    onItemSelected: (Int, String) -> Unit,
    itemList: List<String>?,
) {
    var isOpen by remember { mutableStateOf(false) }

    Box(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colors.surface)
            .height(ELEMENT_HEIGHT),
        contentAlignment = Alignment.CenterStart
    ) {
        if (selectedItem == null || selectedItem.isEmpty()) {
            Text(
                text = defaultText,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 3.dp),
                color = MaterialTheme.colors.onSurface.copy(.45f)
            )
        }

        Text(
            text = selectedItem,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 32.dp, bottom = 3.dp),
            color = MaterialTheme.colors.onSurface
        )


        DropdownMenu(
            modifier = Modifier.fillMaxWidth(.85f),
            expanded = isOpen,
            onDismissRequest = {
                isOpen = false
            },
        ) {
            itemList?.forEachIndexed { index, item ->
                DropdownMenuItem(
                    onClick = {
                        isOpen = false
                        onItemSelected(index, item)
                    }
                ) {
                    Text(item)
                }
            }
        }

        Icon(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 8.dp)
                .size(24.dp),

            imageVector = Icons.Outlined.ArrowDropDown,
            contentDescription = "Dropdown"
        )

        Spacer(
            modifier = Modifier
                .matchParentSize()
                .background(Color.Transparent)
                .clickable(
                    onClick = { isOpen = true }
                )
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ShowProgressDialog() {

    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = true
        )
    ) {
        Card(backgroundColor = Color.White) {
            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(25.dp), contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colors.secondary)
            }
        }
    }

}

@Composable
@Preview
fun headerProgressV() {
    HeaderBarProgress(
        Modifier
            .size(30.dp)
            .padding(start = 10.dp))
}

