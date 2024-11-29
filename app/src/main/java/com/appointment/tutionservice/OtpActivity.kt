package com.appointment.tutionservice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import com.appointment.tutionservice.Constant.API_KEY
import com.appointment.tutionservice.Constant.APP_USER_ID
import com.appointment.tutionservice.Constant.APP_VERSION_NAME
import com.appointment.tutionservice.Constant.DEVICE_TOKEN
import com.appointment.tutionservice.Constant.MOBILE_NO
import com.appointment.tutionservice.databinding.ActivityOtpBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OtpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOtpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val sharedPrefHelper = SharedPreferenceHelper(this)
       val fcmToken = sharedPrefHelper.getFcmToken()

        binding.refreshBtn.setOnClickListener {
            Utility.hideKeyboard(this)
            binding.progressBar.visibility = View.VISIBLE
            loginCheckMobileNumber(MOBILE_NO, Utility.getDeviceId(this), API_KEY, fcmToken)
        }
        val otpFields = arrayOf(
            binding.otpBox1,
            binding.otpBox2,
            binding.otpBox3,
            binding.otpBox4,
            binding.otpBox5,
            binding.otpBox6
        )

        otpFields.forEachIndexed { index, editText ->
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // Move focus to the next EditText when this EditText is filled
                    if (s?.length == 1 && index < otpFields.size - 1) {
                        otpFields[index + 1].requestFocus()
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            editText.setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                    if (index > 0) {
                        otpFields[index - 1].requestFocus()
                        otpFields[index].setText("")
                    }
                    else{
                        editText.setText("")
                    }
                    return@setOnKeyListener true
                }
                false
            }
        }

        binding.btnLogin.setOnClickListener {
            val otp = otpFields.joinToString("") { it.text.toString() }

            if (otp.length < otpFields.size) {
                Toast.makeText(this, "Please enter a valid OTP", Toast.LENGTH_SHORT).show()
            } else {
                Utility.hideKeyboard(this)
                binding.progressBar.visibility = View.VISIBLE
                checkOtpForLogin(
                    otp,
                    MOBILE_NO,
                    API_KEY,
                    Utility.getDeviceId(this),
                    Utility.deviceType,
                    DEVICE_TOKEN,
                    Utility.deviceModelNumber,
                    Utility.deviceSerialNumber,
                    Utility.deviceBrand,
                    APP_VERSION_NAME.toString(),
                    0.0,
                    0.0,
                    APP_USER_ID.toString()
                )
            }
        }

    }

    private fun checkOtpForLogin(
        otp: String,
        user_mobile: String,
        api_key: String,
        device_id: String,
        device_type: String,
        device_token: String,
        device_model_no: String,
        device_serial_no: String,
        device_brand: String,
        app_version: String,
        lat: Double,
        lng: Double,
        app_user_id: String
    ) {
        val addUserDetails = OtpModel(
            otp,
            user_mobile,
            api_key,
            device_id,
            device_type,
            device_token,
            device_model_no,
            device_serial_no,
            device_brand,
            app_version,
            lat,
            lng,
            app_user_id
        )
        Log.i("TAG", "addCustomer: $addUserDetails")
        val call = RetrofitClient.api.checkOtpForLogin(addUserDetails)
        call.enqueue(object : Callback<OtpResponse> {
            override fun onResponse(call: Call<OtpResponse>, response: Response<OtpResponse>) {
                if (response.isSuccessful) {
                    val updateProfileResponse = response.body()
                    Log.i("TAG", "onResponse: " + response.body())
                    updateProfileResponse?.let {
                        if (it.status == 1) {
                            binding.progressBar.visibility = View.GONE
                            Constant.APP_USER_KEY = it.data.app_user_key
                            Constant.app_registration_id = it.data.app_registration_id
                            Constant.app_user_action_type = it.data.app_user_action_type
                            Constant.current_app_user_type = it.data.current_app_user_type
                            val sharedPrefHelper = SharedPreferenceHelper(applicationContext)
                            sharedPrefHelper.setLoggedIn(true)
                            sharedPrefHelper.saveLoginData(it.data.app_user_key, it.data.app_registration_id, it.data.app_user_action_type, it.data.current_app_user_type, APP_USER_ID.toString(), MOBILE_NO)

                            Toast.makeText(
                                applicationContext,
                                "Login successfully...",
                                Toast.LENGTH_SHORT
                            ).show()

                            if (Constant.current_app_user_type == "1") {
                                val intent =
                                    Intent(applicationContext, CustomerMainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            else{
                                val intent =
                                    Intent(applicationContext, ProviderMainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }

                        } else {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(applicationContext, "OTP is wrong or does not match!", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(applicationContext, "OTP is wrong or does not match!", Toast.LENGTH_SHORT).show()
                    Log.e("API", "API call failed with code ${response.code()}")
                }
            }

            override fun onFailure(call: Call<OtpResponse>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                Log.e("API", "API call failed with exception: ${t.message}")
                Toast.makeText(applicationContext, "OTP is wrong or does not match!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loginCheckMobileNumber(userMobile: String,
                                       deviceId: String,
                                       apiKey: String, fcmToken: String) {
        val addUserDetails = LoginMobileNoModel(userMobile,deviceId,apiKey, fcmToken)
        Log.i("TAG", "addCustomer: $addUserDetails")
        val call = RetrofitClient.api.loginWithMobileNo(addUserDetails)
        call.enqueue(object : Callback<APiModel> {
            override fun onResponse(call: Call<APiModel>, response: Response<APiModel>) {
                if (response.isSuccessful) {
                    val updateProfileResponse = response.body()
                    Log.i("TAG", "onResponse: "+response.body())
                    updateProfileResponse?.let {
                        if (it.status == 1) {
                            binding.progressBar.visibility = View.GONE

                            Constant.APP_USER_ID = it.data.app_user_id
                            Toast.makeText(applicationContext, "OTP reSent successfully...", Toast.LENGTH_SHORT).show()
                            val intent = Intent(applicationContext, OtpActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(applicationContext, it.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    binding.progressBar.visibility = View.GONE
                    Log.e("API", "API call failed with code ${response.code()}")
                }
            }

            override fun onFailure(call: Call<APiModel>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                Log.e("API", "API call failed with exception: ${t.message}")
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

}