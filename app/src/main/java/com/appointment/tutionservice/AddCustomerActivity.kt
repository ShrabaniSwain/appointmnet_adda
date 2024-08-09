package com.appointment.tutionservice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.appointment.tutionservice.databinding.ActivityAddCustomerBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddCustomerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddCustomerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCustomerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnNext.setOnClickListener {
            val name = binding.etNameSelect.text.toString()
            val mobile = binding.etMobileNo.text.toString()
            val email = binding.etEmailIDSelect.text.toString()
            val dob = binding.etDobSelect.text.toString()
            val city = binding.etCitySelect.text.toString()
            val address = binding.etAddressSelect.text.toString()

            val crmCreateData = CrmCreateData(
                "0",
                name,
                mobile,
                email,
                "",
                dob,
                city,
                address,
                Constant.MOBILE_NO,
                Constant.API_KEY,
                Utility.getDeviceId(applicationContext),
                Utility.deviceType,
                Utility.deviceToken,
                Constant.APP_USER_ID.toString(),
                Constant.APP_VERSION_NAME.toString(),
                Constant.APP_USER_KEY,
                0.0,
                0.0
            )

            if (name.isBlank() || mobile.isBlank() || email.isBlank() || dob.isBlank() || city.isBlank() || address.isBlank()) {
                Toast.makeText(
                    this,
                    "Please fill in all fields",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            } else {
                appCrmDocument(crmCreateData)
            }
        }
    }

        private fun openCrmProviderFragment(){
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.crmContainer, CrmFragment())
            transaction.disallowAddToBackStack()
            transaction.commit()
    }

    private fun appCrmDocument(crmCreateData: CrmCreateData) {
        showProgressBar()
        val call = RetrofitClient.api.appCrmDocument(crmCreateData)
        call.enqueue(object : Callback<UpdateProfileResponse> {
            override fun onResponse(call: Call<UpdateProfileResponse>, response: Response<UpdateProfileResponse>) {
                if (response.isSuccessful) {
                    val updateProfileResponse = response.body()
                    Log.i("TAG", "onResponse: " + response.body())
                    updateProfileResponse?.let {
                        if (it.status == 1) {
                            hideProgressBar()
                            openCrmProviderFragment()
                        } else {
                            hideProgressBar()
                            Toast.makeText(
                                applicationContext,
                                "Something went wrong",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    Toast.makeText(
                        applicationContext,
                        response.body()?.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    hideProgressBar()
                    Log.e("API", "API call failed with code ${response.code()}")
                }
            }

            override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
                hideProgressBar()
                Log.e("API", "API call failed with exception: ${t.message}")
                Toast.makeText(
                    applicationContext,
                    "Something went wrong",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }
}