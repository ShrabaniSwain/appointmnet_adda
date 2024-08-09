package com.appointment.tutionservice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.appointment.tutionservice.databinding.ActivityProviderMainBinding
import com.razorpay.PaymentResultListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProviderMainActivity : AppCompatActivity() , PaymentResultListener{

    private lateinit var binding: ActivityProviderMainBinding
    private val homeFragment: Fragment = ProviderHomeFragment()
    private val requestFragment: Fragment = ProviderRequestFragment()
    private val crmFragment: Fragment = CrmFragment()
    private val messagesFragment: Fragment = ProviderMessageFragment()
    private val bidsFragment: Fragment = ProviderBidsFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProviderMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigation.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.actionHome -> replaceFragment(homeFragment)
                R.id.actionCrm -> replaceFragment(crmFragment)
                R.id.actionRequests -> replaceFragment(requestFragment)
                R.id.actionMessage -> replaceFragment(messagesFragment)
                R.id.actionBids -> replaceFragment(bidsFragment)
            }
            true
        }

        replaceFragment(homeFragment)

    }

    private fun replaceFragment(homeFragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, homeFragment)
            .commit()
    }


    private fun updateProfileVerification(profileVerifyPaymentRequest: ProfileVerifyPaymentRequest) {
        val call = RetrofitClient.api.updateProfileVerification(profileVerifyPaymentRequest)
        call.enqueue(object : Callback<UpdateProfileResponse> {
            override fun onResponse(call: Call<UpdateProfileResponse>, response: Response<UpdateProfileResponse>) {
                if (response.isSuccessful) {
                    val updateProfileResponse = response.body()
                    Log.i("TAG", "onResponse: $updateProfileResponse")
                    Toast.makeText(applicationContext, "Confirmed", Toast.LENGTH_SHORT).show()
                    val intent = Intent(applicationContext, ProviderMainActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(applicationContext, response.message(), Toast.LENGTH_SHORT).show()
                    Log.i("TAG", "appUserServicesUpdate onResponse error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
                Log.i("TAG", "appUserServicesUpdate onFailure: ${t.message}")
            }
        })
    }

    override fun onPaymentSuccess(p0: String?) {
        try {
            val paymentDetails = mutableListOf<String>()
            if (p0 != null) {
                paymentDetails.add(p0)
            }
            val payAfterAssign = ProfileVerifyPaymentRequest(
                Constant.BUDGET,
                Constant.VALIDATE_DAYS, paymentDetails, Constant.MOBILE_NO, Constant.API_KEY,
                Utility.getDeviceId(applicationContext), Utility.deviceType,
                Utility.deviceToken, Constant.APP_USER_ID.toString(),
                Constant.APP_VERSION_NAME.toString(), Constant.APP_USER_KEY, 0.0, 0.0
            )
            updateProfileVerification(payAfterAssign)
            Log.i("TAG", "onPaymentSuccess: $payAfterAssign")
            Toast.makeText(applicationContext, "Payment is successful", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("TAG", "Error in onPaymentSuccess: " + e.message)
        }
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        Toast.makeText(applicationContext, "Payment Failed due to error", Toast.LENGTH_SHORT)
            .show()
    }

}