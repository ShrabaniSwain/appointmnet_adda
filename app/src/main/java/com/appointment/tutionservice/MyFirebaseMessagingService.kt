package com.appointment.tutionservice

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {


    companion object {
        private const val TAG = "MyFirebaseMessagingService"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.i(TAG, "onNewToken: " + token)
        val firebasePref: SharedPreferences =
            getSharedPreferences("notification", Context.MODE_PRIVATE)
        val editorFirebasePref: SharedPreferences.Editor = firebasePref.edit()
        editorFirebasePref.putString("firebaseFcmToken", token)
        editorFirebasePref.apply()

    }



    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        showNotification(p0.notification?.title, p0.notification?.body)
        Log.i(TAG, "onMessageReceived: "+ (p0.notification?.body ?: "empty "))

        if (p0.data.isNotEmpty()) {
            Log.i(TAG, "Notification => ${p0.notification?.body}")
        }
    }


    private fun showNotification(title: String?, desc: String?) {
        Log.i(TAG, "showNotification: " + "showNotification")
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "YOUR_CHANNEL_ID"
        val channelName = "YOUR_CHANNEL_NAME"
        val soundUri = Uri.parse("android.resource://${applicationContext.packageName}/${R.raw.custom_notification_mp3}")

        val builder: NotificationCompat.Builder

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // For Oreo and above, create a notification channel with custom sound
            val notificationChannel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Channel description"
                enableLights(true)
                enableVibration(true)
                setSound(soundUri, AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build())
            }

            notificationManager.createNotificationChannel(notificationChannel)

            builder = NotificationCompat.Builder(applicationContext, channelId)
        } else {
            builder = NotificationCompat.Builder(applicationContext)
                .setSound(soundUri)
        }

        // Set up intent for notification click action
        val intent = if (Constant.current_app_user_type == "1") {
            Intent(applicationContext, CustomerMainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        } else {
            Intent(applicationContext, ProviderMainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build the notification
        builder.setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title ?: applicationContext.getString(R.string.app_name))
            .setContentText(desc)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true).priority = NotificationCompat.PRIORITY_HIGH // High priority for visibility

        // Show the notification
        notificationManager.notify(123, builder.build()) // Use a unique notification ID
    }


}