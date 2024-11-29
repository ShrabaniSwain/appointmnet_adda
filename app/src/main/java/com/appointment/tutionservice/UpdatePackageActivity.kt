package com.appointment.tutionservice

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.appointment.tutionservice.databinding.ActivityUpdatePackageBinding
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdatePackageActivity : AppCompatActivity(), PaymentResultListener {
    private lateinit var binding: ActivityUpdatePackageBinding
    private lateinit var packageData: List<MembershipPackage>
    private lateinit var updatePackageRequest: UpdatePackageRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdatePackageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        getAllMembershipPackages()
    }

    private fun getAllMembershipPackages() {
        showProgressBar()
        val call = RetrofitClient.api.getAllMembershipPackages(
            Constant.MOBILE_NO,
            Constant.API_KEY,
            Utility.getDeviceId(applicationContext),
            Utility.deviceToken,
            Constant.APP_USER_KEY,
            Constant.APP_USER_ID.toString(),
            Constant.APP_VERSION_NAME.toString()
        )

        call.enqueue(object : Callback<MembershipPackagesResponse> {
            override fun onResponse(call: Call<MembershipPackagesResponse>, response: Response<MembershipPackagesResponse>) {
                Log.i("TAG", "response: $response")

                if (response.isSuccessful) {
                    hideProgressBar()
                    val errorBody = response.errorBody()
                    packageData = response.body()?.data?.membershipPackages ?: emptyList()

                    val adapter = UpdatePackageAdapter(applicationContext, packageData){notificationText->
                        updatePackageRequest = UpdatePackageRequest(
                            notificationText.packageId.toInt(), notificationText.packageId, notificationText.validityDays, Constant.MOBILE_NO,
                            Constant.API_KEY,
                            Utility.getDeviceId(this@UpdatePackageActivity), Utility.deviceType,
                            Utility.deviceToken,
                            Constant.APP_USER_ID.toString(),
                            Constant.APP_VERSION_NAME.toString(), Constant.APP_USER_KEY, "",0.0, 0.0
                        )
                        val amount = Math.round(notificationText.packagePrice.toFloat() * 100).toInt()
                        Checkout.preload(applicationContext)
                        val co = Checkout()
                        co.setKeyID(Constant.RAZORPAY_KEY)
                        val obj = JSONObject()
                        try {
                            obj.put("name", Constant.Provider_Name)
                            obj.put("description", "Test payment")
                            obj.put("theme.color", "")
                            obj.put("currency", "INR")
                            obj.put("amount", amount)
                            obj.put("prefill.contact", Constant.MOBILE_NO)
                            obj.put("prefill.email", Constant.EMAIL)
                            co.open(this@UpdatePackageActivity, obj)
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                    binding.rvPackage.adapter = adapter
                    binding.rvPackage.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
                } else {
                    Toast.makeText(applicationContext, response.message(), Toast.LENGTH_SHORT).show()
                    Log.i("TAG", response.message())
                }
            }

            override fun onFailure(call: Call<MembershipPackagesResponse>, t: Throwable) {
                hideProgressBar()
                Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show()
                Log.i("TAG", "bannerImage: ${t.message}")

            }
        })
    }

    private fun updateMembershipPackage(updatePackageRequest: UpdatePackageRequest) {

        val call = RetrofitClient.api.updateMembershipPackage(updatePackageRequest)
        call.enqueue(object : Callback<UpdateProfileResponse> {
            override fun onResponse(call: Call<UpdateProfileResponse>, response: Response<UpdateProfileResponse>) {
                if (response.isSuccessful) {
                    val updateProfileResponse = response.body()
                    Log.i("TAG", "onResponse: " + response.body())
                    updateProfileResponse?.let {
                        if (it.status == 1) {
                            val intent = Intent(applicationContext, ProviderMainActivity::class.java)
                            startActivity(intent)
                            finish()
                            Toast.makeText(
                                applicationContext,
                                updateProfileResponse.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
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
                    Log.e("API", "API call failed with code ${response.code()}")
                }
            }

            override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
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

    override fun onPaymentSuccess(p0: String?) {
        if (p0 != null) {
            updatePackageRequest.paymentId = p0
        }
        Log.i("TAG", "onPaymentSuccess: " + updatePackageRequest)
        updateMembershipPackage(updatePackageRequest)
        Toast.makeText(this, "Payment is successful" , Toast.LENGTH_SHORT).show();
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        Toast.makeText(this, "Payment Failed due to error", Toast.LENGTH_SHORT).show();
    }
}