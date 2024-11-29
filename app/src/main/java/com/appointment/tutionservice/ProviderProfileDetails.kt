package com.appointment.tutionservice

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.appointment.tutionservice.databinding.ActivityProviderProfileDetailsBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class ProviderProfileDetails : AppCompatActivity() {
    private lateinit var binding: ActivityProviderProfileDetailsBinding
    private lateinit var photosAdapter: PhotosAdapter
    private lateinit var notification: AppUserData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProviderProfileDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getServiceProviderExtraInfo()

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.bookAppointmentButton.setOnClickListener {
            if (Constant.Question) {
                val intent = Intent(applicationContext, SelectQuestionOne::class.java)
                startActivity(intent)

            }
            else {
                val intent = Intent(applicationContext, SelectquestionThree::class.java)
                startActivity(intent)
            }
        }

    }

    private fun getServiceProviderExtraInfo() {
        val call = RetrofitClient.api.getServiceProviderExtraInfo(
            Constant.PROVIDER_MOBILE,
            Constant.API_KEY,
            Utility.deviceToken,
            Constant.PROVIDER_KEY,
            Constant.PROVIDER_ID,
            Constant.APP_VERSION_NAME.toString()
        )

        call.enqueue(object : Callback<ProviderDetailsResponse> {
            override fun onResponse(call: Call<ProviderDetailsResponse>, response: Response<ProviderDetailsResponse>) {
                if (response.isSuccessful) {
                    Log.i("TAG", "onResponse: " + response)

                    binding.progressBar.visibility = View.GONE
                    notification = response.body()?.data?.app_user_data!!

                    photosAdapter = PhotosAdapter(applicationContext, notification)
                    val gridLayoutManager = GridLayoutManager(applicationContext, 4)
                    binding.rvPhotos.layoutManager = gridLayoutManager
                    binding.rvPhotos.adapter = photosAdapter

                    if (response.body()?.data?.app_user_data?.is_profile_verified == "1"){
                        binding.verifiedBadge.visibility = View.VISIBLE
                    }
                    else{
                        binding.notVerifiedBadge.visibility = View.VISIBLE
                    }
                    Glide.with(applicationContext)
                        .load(response.body()?.data?.app_user_data?.discount_image)
                        .apply(RequestOptions.placeholderOf(R.drawable.noimageavailbale))
                        .into(binding.bannerImage)

                    Glide.with(applicationContext)
                        .load(response.body()?.data?.app_user_data?.business_logo_image)
                        .apply(RequestOptions.placeholderOf(R.drawable.noimageavailbale))
                        .into(binding.logoImage)

                    Glide.with(applicationContext)
                        .load(response.body()?.data?.app_user_data?.profile_image)
                        .apply(RequestOptions.placeholderOf(R.drawable.baseline_person_24))
                        .into(binding.providerImage)

                    setWorkingHours(response.body()?.data?.app_user_data?.shop_time ?: emptyList())
                    binding.companyName.text = Editable.Factory.getInstance().newEditable(
                        response.body()?.data?.app_user_data?.business_name
                            ?: ""
                    )
                    binding.aboutDescription.text = Editable.Factory.getInstance().newEditable(
                        response.body()?.data?.app_user_data?.description
                            ?: ""
                    )
                    binding.doctorName.text = Editable.Factory.getInstance().newEditable(
                        response.body()?.data?.app_user_data?.user_profile_name
                            ?: ""
                    )
                    binding.reviewCount.text = Editable.Factory.getInstance().newEditable(
                        "( ${response.body()?.data?.customer_ratings?.total_customers ?: "0"} )"
                    )

                    binding.ratingTotal.text = Editable.Factory.getInstance().newEditable(
                        "( ${response.body()?.data?.customer_ratings?.total_customers ?: "0"} )"
                    )

                    binding.rateCount.text = Editable.Factory.getInstance().newEditable(
                        response.body()?.data?.customer_ratings?.average_rating
                            ?: "0"
                    )
                    binding.ratingBarCount.text = Editable.Factory.getInstance().newEditable(
                        response.body()?.data?.customer_ratings?.average_rating
                            ?: "0"
                    )
                    val rating = response.body()?.data?.customer_ratings?.average_rating
                    if (rating != null) {
                        binding.ratingBar.rating = rating.toFloat()
                    }

                    // Assuming response is of type Response<ApiResponse>
                    val experience = response.body()?.data?.app_user_data?.experience ?: "0"
                    binding.experience.text = Editable.Factory.getInstance().newEditable("$experience Years Exp.")

                    val groupByRating = response.body()?.data?.customer_ratings?.groupByratingValue ?: emptyList()
                    binding.progressBarLine.progress = groupByRating.getOrNull(5)?.total_rating?.toInt() ?: 0
                    binding.progressBarLine4.progress = groupByRating.getOrNull(4)?.total_rating?.toInt() ?: 0
                    binding.progressBarLine3.progress = groupByRating.getOrNull(3)?.total_rating?.toInt() ?: 0
                    binding.progressBarLine2.progress = groupByRating.getOrNull(2)?.total_rating?.toInt() ?: 0
                    binding.progressBarLine1.progress = groupByRating.getOrNull(1)?.total_rating?.toInt() ?: 0


                    binding.btnWhatsapp.setOnClickListener {
                        val phoneNumber = response.body()?.data?.app_user_data?.user_mobile ?: ""
                        val url = "https://api.whatsapp.com/send?phone=$phoneNumber"
                        try {
                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.data = Uri.parse(url)
                            startActivity(intent)
                        } catch (e: ActivityNotFoundException) {
                            Toast.makeText(applicationContext, "WhatsApp is not installed", Toast.LENGTH_SHORT).show()
                        }
                    }

                    val website = response.body()?.data?.app_user_data?.website ?: ""

                        binding.websiteButton.setOnClickListener {
                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                    data = Uri.parse(website)
                                }
                                if (intent.resolveActivity(applicationContext.packageManager) != null) {
                                    startActivity(intent)
                                } else {
                                    Toast.makeText(
                                        applicationContext,
                                        "Website URL is not available",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }

                    binding.shareButton.setOnClickListener {
                        val shareText = "Check out this amazing app: https://play.google.com/store/apps/details?id=com.flyngener.tutionservice&pli=1"
                        val shareIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, shareText)
                            type = "text/plain"
                        }
                        startActivity(Intent.createChooser(shareIntent, "Share App Link"))
                    }

                    val location = response.body()?.data?.app_user_data?.location?.firstOrNull()

                    binding.directionsButton.setOnClickListener {
                        location?.let {
                            val cityName = it.city_name
                            val stateName = it.state_name
                            val pinCode = it.pin_code
                            val address = "$cityName, $stateName, $pinCode"

                            val gmmIntentUri = Uri.parse("geo:0,0?q=$address")
                            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                            mapIntent.setPackage("com.google.android.apps.maps")

                            if (mapIntent.resolveActivity(applicationContext.packageManager) != null) {
                                startActivity(mapIntent)
                            } else {
                                Toast.makeText(applicationContext, "Google Maps is not installed", Toast.LENGTH_SHORT).show()
                            }
                        } ?: run {
                            Toast.makeText(applicationContext, "Location data is not available", Toast.LENGTH_SHORT).show()
                        }
                    }

                    binding.callButton.setOnClickListener {
                        val phoneNumber = response.body()?.data?.app_user_data?.user_mobile ?: ""
                        val dialIntent = Intent(Intent.ACTION_DIAL)
                        dialIntent.data = Uri.parse("tel:$phoneNumber")
                        startActivity(dialIntent)
                    }

                    binding.location.text = Editable.Factory.getInstance().newEditable(
                        response.body()?.data?.app_user_data?.location?.get(0)?.city_name + "," + response.body()?.data?.app_user_data?.location?.get(0)?.state_name + "," + response.body()?.data?.app_user_data?.location?.get(0)?.pin_code
                    )

                    val instagram = response.body()?.data?.app_user_data?.tweeter_profile ?: ""
                    val facebook = response.body()?.data?.app_user_data?.facebook_profile ?: ""
                    val youtube = response.body()?.data?.app_user_data?.gplus_profile ?: ""

                    binding.socialImage.setOnClickListener {
                        openUrl(instagram)
                    }

                    binding.socialImage1.setOnClickListener {
                        openUrl(facebook)
                    }

                    binding.socialImage2.setOnClickListener {
                        openUrl(youtube)
                    }
                    Log.i("TAG", "responseBody: ${response.body()}")
                } else {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(applicationContext, response.message(), Toast.LENGTH_SHORT)
                        .show()
                    Log.i("TAG", "responseBody: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<ProviderDetailsResponse>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
                Log.i("TAG", "responseBody: Something went wrong.")

            }
        })
    }

    private fun openUrl(url: String) {
        val trimmedUrl = url.trim()
        if (trimmedUrl.isNotEmpty()) {
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(trimmedUrl))
                if (intent.resolveActivity(applicationContext.packageManager) != null) {
                    startActivity(intent)
                } else {
                    Toast.makeText(applicationContext, "No application can handle this request. Please install a web browser.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(applicationContext, "No application found to handle this URL", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(applicationContext, "URL is not available", Toast.LENGTH_SHORT).show()
        }
    }

    fun setWorkingHours(shopTimes: List<ShopTime>) {
        val dayNames = arrayOf(
            "Invalid",
            "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"
        )
        val calendar = Calendar.getInstance()
        val dayIndex = calendar.get(Calendar.DAY_OF_WEEK)
        val currentDay = dayNames[dayIndex]
        var workingHours = ""
        var closeHours = ""

        for (shopTime in shopTimes) {
            if (shopTime.shop_open_days.trim().equals(currentDay, ignoreCase = true)) {
                workingHours = shopTime.shop_open_time
                closeHours = shopTime.shop_close_time
                break
            }
        }

        // Set the values to your views or variables
        // Example:
        binding.workingHours.text = workingHours
        binding.closeHours.text = closeHours
    }

}