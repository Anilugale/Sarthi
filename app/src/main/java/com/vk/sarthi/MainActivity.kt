package com.vk.sarthi

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.messaging.FirebaseMessaging
import com.markodevcic.peko.Peko
import com.markodevcic.peko.PermissionResult
import com.vk.sarthi.ui.nav.Screens
import com.vk.sarthi.ui.nav.ShowNavGraph
import com.vk.sarthi.ui.theme.SarthiTheme
import com.vk.sarthi.utli.Constants
import com.vk.sarthi.utli.SettingPreferences
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    companion object{
        const val TAG = "MainActivity"
    }
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var isShowComplaint = false
    private var isMsg = false

    private val mPref:SharedPreferences by lazy {
        SettingPreferences.get(this@MainActivity)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "intent: $intent")

        if (intent.extras != null) {
            Log.d(TAG, "onCreate: ${intent.extras!!.getBoolean("FromNotification",false)}")
            isShowComplaint = true
            isMsg =  getSharedPreferences(applicationContext.packageName, Context.MODE_PRIVATE).getBoolean("isMsg",true)
            if (isMsg) {
                getSharedPreferences(applicationContext.packageName, Context.MODE_PRIVATE).edit().putBoolean("isMsg",false).commit()
            }
        }
        installSplashScreen()

        setContent {
            SarthiTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val context = LocalContext.current
                    val route = if(SettingPreferences.isUserSave(context)){
                        if (isShowComplaint) {
                            if(isMsg){
                                Screens.MessageList.route
                            }else {
                                Screens.ComplaintList.route
                            }
                        }else {
                            Screens.Dashboard.route
                        }
                    }else{
                        Screens.Login.route
                    }
                    ShowNavGraph(route)
                }
            }


        }


    }

    override fun onStart() {
        super.onStart()
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            val token = task.result
            val msg = getString(R.string.msg_token_fmt, token)
            mPref.edit().putString(SettingPreferences.TOKEN,token).commit()
            SettingPreferences.token = token
            Log.d(TAG, msg)
        })

        lifecycleScope.launchWhenStarted {
            val result = Peko.requestPermissionsAsync(this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION)
            if (result is PermissionResult.Granted) {
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(this@MainActivity)
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location : Location? ->
                        location?.apply {
                            Log.d(TAG, "onCreate: $location")
                            mPref.edit().putString(Constants.LOCATION,"${location.latitude}|${location.longitude}").apply()
                        }
                    }
            } else {
                showPermissionDialog()

            }
        }


    }

    private fun showPermissionDialog() {
        MaterialAlertDialogBuilder(this, R.style.DialogWindowTheme)
            .setTitle(R.string.app_name)
            .setMessage(R.string.permission_error)
            .setPositiveButton("Okay"
            ) { p0, _ ->
                p0.dismiss()

                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri: Uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            .create().show()
    }


}



