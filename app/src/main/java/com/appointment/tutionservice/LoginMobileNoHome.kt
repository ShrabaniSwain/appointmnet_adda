package com.appointment.tutionservice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.appointment.tutionservice.databinding.ActivityLoginMobileNoHomeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginMobileNoHome : AppCompatActivity() {

    private lateinit var binding: ActivityLoginMobileNoHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginMobileNoHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val sharedPrefHelper = SharedPreferenceHelper(this)
        val fcmToken = sharedPrefHelper.getFcmToken()
        Log.i("TAG", "onCreate: " + fcmToken)

        binding.btnGetOtp.setOnClickListener{
            val etMobileNumber = binding.etMobileNumber.text.toString()
            if (etMobileNumber.isBlank() || etMobileNumber.length < 10) {
                Toast.makeText(
                    this,
                    "Please enter a valid mobile number",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            } else {
                Utility.hideKeyboard(this)
                binding.progressBar.visibility = View.VISIBLE
                if (Constant.FCM_TOKEN.isNotEmpty()) {
                    loginCheckMobileNumber(
                        etMobileNumber, Utility.getDeviceId(this), Constant.API_KEY,
                        Constant.FCM_TOKEN
                    )
                }
                Constant.MOBILE_NO = etMobileNumber
            }
        }

        binding.tvUserIdPass.setOnClickListener{
            val intent = Intent(this, LoginUserIdActivity::class.java)
            startActivity(intent)
        }

        binding.tvRegisterNow.setOnClickListener {
            val intent = Intent(this, RegisterNowActivity::class.java)
            startActivity(intent)
        }


    }

    private fun loginCheckMobileNumber(userMobile: String,
                                      deviceId: String,
                                       apiKey: String, fcmToken: String) {
        val addUserDetails = LoginMobileNoModel(userMobile,deviceId,apiKey, Constant.FCM_TOKEN)
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
//                            Toast.makeText(applicationContext, "OTP sent successfully...", Toast.LENGTH_SHORT).show()
                            Toast.makeText(applicationContext, it.data.otp, Toast.LENGTH_SHORT).show()
                            val intent = Intent(applicationContext, OtpActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(applicationContext, it.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(applicationContext, "Invalid Mobile Number", Toast.LENGTH_SHORT).show()
                    Log.e("API", "API call failed with code ${response.code()}")
                }
            }

            override fun onFailure(call: Call<APiModel>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                Log.e("API", "API call failed with exception: ${t.message}")
                Toast.makeText(applicationContext, "Invalid Mobile Number", Toast.LENGTH_SHORT).show()
            }
        })
    }

}