package com.appointment.tutionservice

import android.app.Activity
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat

class Utility {

    companion object{

        fun isInternetAvailable(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
        }

        fun hideKeyboard(activity: Activity) {
            val inputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            val currentFocusView = activity.currentFocus
            if (currentFocusView != null) {
                inputMethodManager.hideSoftInputFromWindow(currentFocusView.windowToken, 0)
            }
        }

        fun getDeviceId(context: Context): String {
            return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        }
        val deviceType = Constant.DEVICE_TYPE
        val deviceModelNumber: String = Build.MODEL
        val deviceSerialNumber: String = Build.SERIAL
        val deviceBrand: String = Build.BRAND
        val deviceToken = Constant.DEVICE_TOKEN

        fun itemBackGround(view: View){
            val cornerRadius = view.context.resources.getDimensionPixelSize(R.dimen.dp_10)
            val shapeDrawable = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                setColor(ContextCompat.getColor(view.context, R.color.white))
            }
            shapeDrawable.cornerRadius = cornerRadius.toFloat()
            view.background = shapeDrawable

            view.setOnClickListener {
                view.setBackgroundColor(ContextCompat.getColor(view.context, R.color.red))

                Handler(Looper.getMainLooper()).postDelayed({
                    view.setBackgroundColor(
                        ContextCompat.getColor(
                            view.context,
                            android.R.color.white
                        )
                    )
                    shapeDrawable.cornerRadius = cornerRadius.toFloat()
                    val borderWidth = view.context.resources.getDimensionPixelSize(R.dimen.dp_2)
                    shapeDrawable.setStroke(
                        borderWidth,
                        ContextCompat.getColor(view.context, R.color.red)
                    )
                    view.background = shapeDrawable
                }, 50)
            }

            var isItemSelected = false
            val borderWidth = view.context.resources.getDimensionPixelSize(R.dimen.dp_2)
            view.setOnClickListener {

                if (!isItemSelected) {
                    // First click: change background to red
                    view.setBackgroundColor(
                        ContextCompat.getColor(
                            view.context,
                            R.color.red
                        )
                    )

                    Handler(Looper.getMainLooper()).postDelayed({
                        view.setBackgroundColor(
                            ContextCompat.getColor(
                                view.context,
                                android.R.color.white
                            )
                        )
                        shapeDrawable.cornerRadius = cornerRadius.toFloat()
                        shapeDrawable.setStroke(
                            borderWidth,
                            ContextCompat.getColor(view.context, R.color.red)
                        )
                        view.background = shapeDrawable
                    }, 50)
                    isItemSelected = true
                } else {
                    // Second click: change background to white, add stroke
                    shapeDrawable.setColor(ContextCompat.getColor(view.context, R.color.white))
                    shapeDrawable.setStroke(
                        borderWidth,
                        ContextCompat.getColor(view.context, R.color.white)
                    )
                    view.background = shapeDrawable
                    isItemSelected = false
                }

            }
        }
    }
}