package com.appointment.tutionservice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.appointment.tutionservice.databinding.ActivityLoginSignUpBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginSignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginSignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginSignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.customerCard.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE

            userTypeData(Constant.APP_USER_ID.toString(),"1",Constant.API_KEY)
            binding.tickHomeTutor.visibility = View.VISIBLE
            binding.tickGuardian.visibility = View.GONE

            val intent = Intent(this, ProfileCreateSuccessActivity::class.java)
            intent.putExtra("CARD_TYPE", Constant.CUSTOMER_CARD)
            Constant.CARD_TYPE = Constant.CUSTOMER_CARD
            startActivity(intent)
            finish()
        }

        binding.providerCard.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            userTypeData(Constant.APP_USER_ID.toString(),"2",Constant.API_KEY)
            binding.tickHomeTutor.visibility = View.GONE
            binding.tickGuardian.visibility = View.VISIBLE

            val intent = Intent(this, PackageActivity::class.java)
            intent.putExtra("CARD_TYPE", Constant.PROVIDER_CARD)
            Constant.CARD_TYPE = Constant.PROVIDER_CARD
            startActivity(intent)
            finish()
        }
    }

    private fun userTypeData(
        appUserId: String,
        appUserType: String,
        apiKey: String
    ) {
        val addCustomer = UserTypeData(
            appUserId, appUserType, apiKey
        )

        Log.i("TAG", "addCustomer: $addCustomer")

        val call = RetrofitClient.api.userTypeUpdate(addCustomer)
        call.enqueue(object : Callback<UserTypeResponse> {
            override fun onResponse(call: Call<UserTypeResponse>, response: Response<UserTypeResponse>) {
                if (response.isSuccessful) {
                    val updateProfileResponse = response.body()
                    Log.i("TAG", "onResponse: " + response.body())
                    if (response.body()!!.status == 1) {
                        binding.progressBar.visibility = View.GONE
                        Constant.APP_USER_KEY = response.body()!!.data
//                        Toast.makeText(
//                            applicationContext,
//                            "Profile created successfully",
//                            Toast.LENGTH_SHORT
//                        ).show()

                    } else {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(
                            applicationContext,
                            "Failed to create profile details. Please try again.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    binding.progressBar.visibility = View.GONE
                    Log.e("API", "API call failed with code ${response.code()}")
                }
            }

            override fun onFailure(call: Call<UserTypeResponse>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                Log.e("API", "API call failed with exception: ${t.message}")
                Toast.makeText(
                    applicationContext,
                    "Failed to create profile details. Please try again.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

}