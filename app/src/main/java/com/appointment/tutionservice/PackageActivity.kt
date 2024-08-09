package com.appointment.tutionservice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.Toast
import com.appointment.tutionservice.databinding.ActivityPackageBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PackageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPackageBinding
    private var packageIdNormal = ""
    private var packageIdPremium = ""
    private var packageId = ""
    private var validateDaysNormal = ""
    private var validateDaysPremium = ""
    private var validateDays = ""
    private var cardViewSelected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPackageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getAllMembershipPackages()
        binding.starterCatdView.setOnClickListener {
            binding.tickStarter.visibility = View.VISIBLE
            binding.tickPremium.visibility = View.GONE
            packageId = packageIdNormal
            validateDays = validateDaysNormal
            cardViewSelected = true
        }

        binding.premiumCatdView.setOnClickListener {
            binding.tickStarter.visibility = View.GONE
            binding.tickPremium.visibility = View.VISIBLE
            packageId = packageIdPremium
            validateDays = validateDaysPremium
            cardViewSelected = true
        }

        binding.tvBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.btnSubmit.setOnClickListener {
            if (!cardViewSelected) {
                Toast.makeText(this, "Please select a package", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else {
                val updatePackageRequest = UpdatePackageRequest(
                    packageId.toInt(), packageId, validateDays, Constant.MOBILE_NO,
                    Constant.API_KEY,
                    Utility.getDeviceId(applicationContext), Utility.deviceType,
                    Utility.deviceToken,
                    Constant.APP_USER_ID.toString(),
                    Constant.APP_VERSION_NAME.toString(), Constant.APP_USER_KEY, "",0.0, 0.0
                )
                updateMembershipPackage(updatePackageRequest)
            }
        }

    }

    private fun getAllMembershipPackages() {
        binding.progressBar.visibility = View.VISIBLE
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
                if (response.isSuccessful) {
                    binding.progressBar.visibility = View.GONE

                    packageIdNormal = response.body()?.data?.membershipPackages?.get(0)?.packageId ?: ""
                    packageIdPremium = response.body()?.data?.membershipPackages?.get(1)?.packageId ?: ""
                    validateDaysNormal = response.body()?.data?.membershipPackages?.get(0)?.validityDays ?: ""
                    validateDaysPremium = response.body()?.data?.membershipPackages?.get(1)?.validityDays ?: ""
                    binding.tvStarter.text = response.body()?.data?.membershipPackages?.get(0)?.packageName ?: ""
                    val price = response.body()?.data?.membershipPackages?.get(0)?.packagePrice ?: ""
                    binding.tvStarterAmount.text = "₹ $price"
                    val validityDays = response.body()?.data?.membershipPackages?.get(0)?.validityDays ?: ""
                    binding.tvStarterMonthly.text = "$validityDays Days"

                    binding.tvPremium.text = response.body()?.data?.membershipPackages?.get(1)?.packageName ?: ""
                    val pricePrimium = response.body()?.data?.membershipPackages?.get(1)?.packagePrice ?: ""
                    binding.tvPremiumAmount.text = "₹ $pricePrimium"
                    val validityDaysPremium = response.body()?.data?.membershipPackages?.get(1)?.validityDays ?: ""
                    binding.tvPremiumYearly.text = "$validityDaysPremium Days"

                    val htmlTextStarter = response.body()?.data?.membershipPackages?.get(0)?.packageDescription ?: ""
                    val cleanText = Html.fromHtml(htmlTextStarter).toString()
                    binding.tvUnlimited.text = cleanText

                    val htmlTextPremium= response.body()?.data?.membershipPackages?.get(1)?.packageDescription ?: ""
                    val cleanText1 = Html.fromHtml(htmlTextPremium).toString()
                    binding.tvUnlimitedRequest.text = cleanText1

                    Log.i("TAG", "response: ${response.body()}")
                } else {
                    binding.progressBar.visibility = View.GONE
                    val errorBody = response.errorBody()
                    Toast.makeText(applicationContext, response.message(), Toast.LENGTH_SHORT).show()
                    Log.i("TAG", response.message())
                }
            }

            override fun onFailure(call: Call<MembershipPackagesResponse>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
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
                            if (Constant.CARD_TYPE == "Home") {
                                binding.progressBar.visibility = View.GONE
                                val intent = Intent(applicationContext, ProviderMainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            else {
                                binding.progressBar.visibility = View.GONE
                                val intent = Intent(
                                    applicationContext,
                                    ProfileCreateSuccessActivity::class.java
                                )
                                startActivity(intent)
                                finish()
                            }
                        } else {
                            binding.progressBar.visibility = View.GONE
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
                    binding.progressBar.visibility = View.GONE
                    Log.e("API", "API call failed with code ${response.code()}")
                }
            }

            override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                Log.e("API", "API call failed with exception: ${t.message}")
                Toast.makeText(
                    applicationContext,
                    "Something went wrong",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

}