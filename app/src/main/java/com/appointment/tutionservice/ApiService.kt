package com.appointment.tutionservice

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.appointment.tutionservice.Constant.NOTIFICATION_ID
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ApiService : Service() {

    private lateinit var notificationHelper: NotificationHelper
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable

    override fun onCreate() {
        super.onCreate()
        notificationHelper = NotificationHelper(this)
        notificationHelper.createNotificationChannel() // Create notification channel
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = notificationHelper.createForegroundNotification()
//        startForeground(1, notification)

        // Schedule periodic API calls every 5 seconds
//        startFetchingNotifications()

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopFetchingNotifications() // Stop the handler when the service is destroyed
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun startFetchingNotifications() {
        runnable = object : Runnable {
            override fun run() {
                allPushNotificationList() // Call API to fetch notifications

                // Schedule the runnable to run again after 5 seconds
                handler.postDelayed(this, 5000) // 5 seconds = 5000 milliseconds
            }
        }
        // Start the first execution
        handler.post(runnable)
    }

    private fun stopFetchingNotifications() {
        handler.removeCallbacks(runnable) // Remove callbacks to stop execution
    }

    private fun allPushNotificationList() {
        val call = RetrofitClient.api.allPushNotificationList(
            Constant.MOBILE_NO,
            Constant.API_KEY,
            Utility.getDeviceId(applicationContext),
            Utility.deviceToken,
            Constant.APP_USER_KEY,
            Constant.APP_USER_ID.toString(),
            Constant.APP_VERSION_NAME.toString()
        )

        call.enqueue(object : Callback<AlertNotifyResponse> {
            override fun onResponse(call: Call<AlertNotifyResponse>, response: Response<AlertNotifyResponse>) {
                if (response.isSuccessful) {
                    Log.i("TAG", "NotificationWorker: " + response.body())

                    response.body()?.let { alertNotifyResponse ->
                        if (alertNotifyResponse.data.isNotEmpty()) {
                            for (notification in alertNotifyResponse.data) {
                                notificationHelper.showNotification(
                                    notification.notification_title,
                                    notification.notification_message
                                )
                                NOTIFICATION_ID = alertNotifyResponse.data[0].notification_id
                                updatePushNotification()
                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<AlertNotifyResponse>, t: Throwable) {
                Log.e("NotificationWorker", "Error: ${t.message}")
            }
        })
    }

    private fun updatePushNotification() {
        val call = RetrofitClient.api.updatePushNotification(
            Constant.MOBILE_NO,
            Constant.API_KEY,
            Utility.getDeviceId(applicationContext),
            Utility.deviceToken,
            Constant.APP_USER_KEY,
            Constant.APP_USER_ID.toString(),
            NOTIFICATION_ID,
            Constant.APP_VERSION_NAME.toString()
        )

        call.enqueue(object : Callback<UpdateNotificationResponse> {
            override fun onResponse(call: Call<UpdateNotificationResponse>, response: Response<UpdateNotificationResponse>) {
                if (response.isSuccessful) {
                    Log.i("TAG", "onResponse: " + response.body())
                }
            }

            override fun onFailure(call: Call<UpdateNotificationResponse>, t: Throwable) {
                Log.e("NotificationWorker", "Error: ${t.message}")
            }
        })
    }
}



