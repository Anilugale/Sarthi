package com.vk.sarthi

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.vk.sarthi.utli.Constants


class MyFirebaseService : FirebaseMessagingService() {
    companion object {
       val TAG = MyFirebaseService::class.simpleName
    }
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "onNewToken: ")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        Log.d(TAG, "From: " + remoteMessage.from)
        if (remoteMessage.notification != null) {
            remoteMessage.notification!!.title?.let { remoteMessage.notification!!.body?.let { it1 ->
                sendNotification(it,
                    it1
                )
            } }

        }


    }

    private fun sendNotification(messageTitle: String, messageBody: String) {


        val manager = NotificationManagerCompat.from(applicationContext)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                Constants.CHANNEL_ID, Constants.CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
        }
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.putExtra("FromNotification",true)
        if(messageTitle.equals("message",true)){
            getSharedPreferences(applicationContext.packageName, Context.MODE_PRIVATE).edit().putBoolean("isMsg",true).commit()
        }
        val pi = PendingIntent.getActivity(applicationContext, Math.random().toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT or
                PendingIntent.FLAG_IMMUTABLE)


        val notification: Notification = NotificationCompat.Builder(this, Constants.CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(messageBody)
            .setContentIntent(pi)
            .setAutoCancel(true)
            .build()
        manager.notify(1, notification)
    }
}