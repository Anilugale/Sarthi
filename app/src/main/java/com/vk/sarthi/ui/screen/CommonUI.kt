package com.vk.sarthi.ui.screen

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.valentinilk.shimmer.shimmer
import com.vk.sarthi.cache.Cache
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

            DrawerItems(name = "वेळापत्रक", callback = {  current.toast("Coming  soon..") }, isSelected = route == Screens.AddComment.route)
            DrawerItems(name = "संदेश", callback = {  current.toast("Coming  soon..") }, isSelected = route == Screens.AddComment.route)
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

@Composable
@Preview
fun headerProgressV() {
    HeaderBarProgress(
        Modifier
            .size(30.dp)
            .padding(start = 10.dp))
}
