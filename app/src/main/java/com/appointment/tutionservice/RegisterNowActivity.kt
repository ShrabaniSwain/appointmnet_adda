package com.appointment.tutionservice

import android.R
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.appointment.tutionservice.Constant.API_KEY
import com.appointment.tutionservice.Constant.APP_USER_ID
import com.appointment.tutionservice.Constant.APP_VERSION_NAME
import com.appointment.tutionservice.Constant.REGISTER_VIA
import com.appointment.tutionservice.databinding.ActivityRegisterNowBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class RegisterNowActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterNowBinding
    private val cityNames = mutableListOf<String>()
    private val stateNames = mutableListOf<String>()
    private var selectedCityId: String? = null
    private var gender: String = ""

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterNowBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.genderMen.setOnClickListener {
            gender = "Male"
            binding.tickMen.visibility = View.VISIBLE
            binding.tickWomen.visibility = View.GONE
        }

        binding.genderWomen.setOnClickListener {
            gender = "Female"
            binding.tickMen.visibility = View.GONE
            binding.tickWomen.visibility = View.VISIBLE
        }

        binding.referralCodeCheck.setOnCheckedChangeListener { _, isChecked ->
            binding.etReferralCode.isEnabled = isChecked
        }

        binding.btnNext.setOnClickListener {
            val etName = binding.etName.text.toString()
            val etMobileNumber = binding.etPhone.text.toString()
            val etEmail = binding.etEmail.text.toString()
            val etCity = binding.etCity.text.toString()
            val etState = binding.etState.text.toString()
            val etPinCode = binding.etPinCode.text.toString()
            Constant.MOBILE_NO = etMobileNumber
            if (etName.isEmpty() || etMobileNumber.isEmpty() || etCity.isEmpty() || etEmail.isEmpty() || etPinCode.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (gender.isEmpty()) {
                Toast.makeText(this, "Please select a gender", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                binding.progressBar.visibility = View.VISIBLE
                selectedCityId?.let { it1 ->
                    registrationStepOne(
                        REGISTER_VIA,
                        etName,
                        etEmail,
                        gender,
                        it1,
                        etState,
                        etPinCode,
                        etMobileNumber,
                        API_KEY,
                        Utility.getDeviceId(this),
                        Utility.deviceType,
                        Utility.deviceToken,
                        Utility.deviceModelNumber,
                        Utility.deviceSerialNumber,
                        Utility.deviceBrand,
                        APP_VERSION_NAME.toString(),
                        0.0,
                        0.0,
                        "0"
                    )
                }
            }
        }

        binding.tvRegisterNow.setOnClickListener {
            val intent = Intent(applicationContext, LoginMobileNoHome::class.java)
            startActivity(intent)
        }

        GlobalScope.launch(Dispatchers.Main) {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.api.allTheData()
                }
                if (response.isSuccessful) {
                    val customerDetailsResponse = response.body()
                    customerDetailsResponse?.let { handleCityListResponse(it.data.all_citys) }
                } else {
                    Log.i("TAG", "API Call failed with error code: ${response.code()}")
                }
            } catch (e: IOException) {
                Log.e("TAG", "No internet connection", e)
                Toast.makeText(applicationContext, "No internet connection. Please check your connection and try again.", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Log.e("TAG", "An unexpected error occurred", e)
                Toast.makeText(applicationContext, "An unexpected error occurred. Please try again.", Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun handleCityListResponse(result: List<City>) {

        val adapter = ArrayAdapter(this, R.layout.simple_list_item_1, cityNames)
        binding.etCity.setAdapter(adapter)

        binding.etCity.setOnItemClickListener { parent, _, position, _ ->
            val selectedProductName = parent.getItemAtPosition(position) as String
            val selectedProduct = result.find { it.city_name == selectedProductName }

            selectedProduct?.let {
                selectedCityId = it.city_id
                binding.etState.text = Editable.Factory.getInstance().newEditable(it.state_name)
            }
        }
        cityNames.clear()
        cityNames.addAll(result.map { it.city_name })

        stateNames.clear()
        stateNames.addAll(result.map { it.state_name })
    }

    private fun registrationStepOne(
        registerVia: String,
        fullName: String, emailId: String,
        gender: String,
        city: String,
        state: String, pinCode: String,
        mobileNumber: String,
        apikey: String, deviceId: String,
        deviceType: String,
        deviceToken: String,
        deviceModelNo: String,
        deviceSerialNo: String,
        deviceBrand: String,
        appVersion: String,
        lat: Double,
        lng: Double,
        appUserId: String
    ) {
        val addCustomer = RegistrationData(
            registerVia,
            fullName,
            emailId,
            gender,
            city,
            state,
            pinCode,
            mobileNumber,
            apikey,
            deviceId,
            deviceType,
            deviceToken,
            deviceModelNo,
            deviceSerialNo,
            deviceBrand,
            appVersion,
            lat,
            lng,
            appUserId
        )

        Log.i("TAG", "addCustomer: $addCustomer")

        val call = RetrofitClient.api.registrationStepOne(addCustomer)
        call.enqueue(object : Callback<APiModel> {
            override fun onResponse(call: Call<APiModel>, response: Response<APiModel>) {
                if (response.isSuccessful) {
                    val updateProfileResponse = response.body()
                    Log.i("TAG", "onResponse: " + response.body())
                    updateProfileResponse?.let {
                        if (it.status == 1) {
                            binding.progressBar.visibility = View.GONE
                            APP_USER_ID = it.data.app_user_id
                            Toast.makeText(
                                applicationContext,
                                "Registration details added successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent(applicationContext, LoginSignUpActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(
                                applicationContext,
                                it.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    binding.progressBar.visibility = View.GONE
                    Log.e("API", "API call failed with code ${response.message()}")
                    Toast.makeText(
                        applicationContext,
                        response.message(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<APiModel>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                Log.e("API", "Something went wrong")
                Toast.makeText(
                    applicationContext,
                    "Sorry, This phone is already exist.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }


}