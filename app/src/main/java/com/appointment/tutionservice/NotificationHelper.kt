package com.appointment.tutionservice

import android.Manifest
import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class NotificationHelper(private val context: Context) {

    private val CHANNEL_ID = "my_channel_id"
    private val CHANNEL_NAME = "My Notification Channel"
    private val CHANNEL_DESCRIPTION = "This is my notification channel"
    private val POST_NOTIFICATION_REQUEST_CODE = 101

    // Use system notification sound (default notification sound)
    private val systemSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

    fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH // Set importance to high for sound and vibration
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION

                // Set system notification sound
                setSound(systemSoundUri, createAudioAttributes())

                // Enable vibration and set custom vibration pattern
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 1000, 500) // Vibration pattern: wait 0ms, vibrate for 500ms, wait 1000ms, vibrate for 500ms

                // Optionally enable lights (this works on some devices)
                enableLights(true)
                lightColor = Color.GREEN
            }

            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createAudioAttributes(): AudioAttributes {
        return AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION) // Set usage for notifications
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION) // Set content type
            .build()
    }

    fun createForegroundNotification(): Notification {

        val intent = if (Constant.current_app_user_type == "1") {
            Intent(context, CustomerMainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        } else {
            Intent(context, ProviderMainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_SOUND)
//            .setSound(systemSoundUri) // Use system sound for notification
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
    }

    fun showNotification(title: String, message: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    context, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (context is Activity) {
                    ActivityCompat.requestPermissions(
                        context as Activity,
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        POST_NOTIFICATION_REQUEST_CODE
                    )
                    return
                } else {
                    Log.e("NotificationHelper", "Context is not an Activity. Cannot request permission.")
                    return
                }
            }
        }

        val intent = if (Constant.current_app_user_type == "1") {
            Intent(context, CustomerMainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        } else {
            Intent(context, ProviderMainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(longArrayOf(0, 500, 1000, 500)) // Set vibration pattern
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())

        // Play the system default notification sound manually
        try {
            val notification: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val ringtone = RingtoneManager.getRingtone(context.applicationContext, notification)
            ringtone.play()
            Log.e("NotificationHelper", "sound")
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("NotificationHelper", "Error playing notification sound: ${e.message}")
        }
    }


}



