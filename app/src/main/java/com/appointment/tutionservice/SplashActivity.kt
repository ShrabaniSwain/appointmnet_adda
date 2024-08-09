package com.appointment.tutionservice

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.appointment.tutionservice.databinding.ActivitySplashBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.i("TAG", "onCreate: " + Utility.getDeviceId(applicationContext))
        val sharedPrefHelper = SharedPreferenceHelper(this)
        if (sharedPrefHelper.isLoggedIn()) {

            Constant.APP_USER_KEY = sharedPrefHelper.getUserKey(this)
            Constant.app_registration_id = sharedPrefHelper.getRegistrationId(this)
            Constant.app_user_action_type = sharedPrefHelper.getUserActionType(this)
            Constant.current_app_user_type = sharedPrefHelper.getUserType(this)
            Constant.APP_USER_ID = sharedPrefHelper.getUserId(this).toInt()
            Constant.MOBILE_NO = sharedPrefHelper.getMobileNo(this)

            loadMainActivityWithDelay()
        }
        else {
            loadSignUpActivityWithDelay()
        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.i("FirebaseMessaging", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            Log.i("FirebaseMessaging", "Fetching FCM registration token failed  $token", )

        })
    }

    private fun loadSignUpActivityWithDelay() {
        Handler().postDelayed({
            val intent = Intent(this, LoginMobileNoHome::class.java)
            startActivity(intent)
            finish()
        }, 2000)
    }

    private fun loadMainActivityWithDelay() {
        Handler().postDelayed({
            if (Constant.current_app_user_type == "1") {
                val intent = Intent(this, CustomerMainActivity::class.java)
                startActivity(intent)
                finish()
            }
            else{
                val intent = Intent(this, ProviderMainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, 2000)
    }
}