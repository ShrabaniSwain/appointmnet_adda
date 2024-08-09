package com.appointment.tutionservice

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
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
        showNotification(p0.data["title"], p0.data["message"])
        Log.i(TAG, "onMessageReceived: " )

        if (p0.data.isNotEmpty()) {
            Log.i(TAG, "Notification => ${p0.notification?.body}")
//            showNotification(p0.)
//            val btType = p0.data.getValue(BT_TYPE).toInt()
//
//            DevLogs.d(TAG, "Message data payload: ${p0.data}")
//
//            LATITUDE = p0.data["latitude"]?.toDouble() ?: 0.0
//            LONGITUDE = p0.data["longitude"]?.toDouble() ?: 0.0
//            DATETIME = p0.data["dateTime"]?.toLong() ?: 0L
//            SERVERDATETIME = p0.data["serverDateTime"]?.toLong() ?: 0L
//            ISLOCATIONPERMISSIONENABLED = p0.data["isLocationPermissionEnabled"]?.toBoolean() ?: false
//
//            if (btType != BT_TYPE_LOCATION) {
//                if (btType == BT_TYPE_LOGOUT) {
//                    Utility.logoutAndNavigateToSignIn(this)
//                }
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    val channel = NotificationChannel(
//                        "MyNotification", "AlertNotification",
//                        NotificationManager.IMPORTANCE_HIGH
//                    )
//                    val manager =
//                        getSystemService(NotificationManager::class.java) as NotificationManager
//                    manager.createNotificationChannel(channel)
//                }
//                //updateAlertsData()
//                saveAlertIntoLocalDB(p0.data)
//
//            }
        }
    }


    private fun showNotification(title: String?, desc: String?) {

        val intent = Intent(this, SplashActivity::class.java)

        val pendingIntent: PendingIntent
//
//        if (application.getSharedPreferences("notification", MODE_PRIVATE)
//                .getBoolean(Constants, false)
//        ) {
//            pendingIntent = NavDeepLinkBuilder(this)
////                .setComponentName(DashBoardActivity::class.java)
//                .setGraph(R.navigation.nav_graph)
//                .setDestination(R.id.alerts)
//                .setArguments(bundle)
//                .createPendingIntent()
//            application.getSharedPreferences(Constants.SHARED_PREFERENCE_FILE_NAME, MODE_PRIVATE).edit().putBoolean(AppConstant.IS_NOTIFICATIONS, true).apply()
//        } else {
//            pendingIntent = PendingIntent.getActivity(
//                applicationContext,
//                System.currentTimeMillis().toInt(),
//                intent,
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
//                } else {
//                    PendingIntent.FLAG_UPDATE_CURRENT
//                }
//            )
//        }


        //create notification builder
        val builder = NotificationCompat.Builder(this, "MyNotification")
            .setContentTitle("$title")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentText(desc)
//            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        val manager = NotificationManagerCompat.from(this)
        manager.apply {
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(123, builder.build())
        }
    }

}