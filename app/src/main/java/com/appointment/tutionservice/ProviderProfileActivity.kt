package com.appointment.tutionservice

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.appointment.tutionservice.databinding.ActivityProviderProfileBinding
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ProviderProfileActivity : AppCompatActivity(), OnAddPhotoClickListener {

    private lateinit var binding: ActivityProviderProfileBinding
    private val READ_EXTERNAL_STORAGE_REQUEST_CODE = 123
    private val FILE_REQUEST_CODE = 100
    private var imageUri: Uri? = null
    private var scannerLogoUri: Uri? = null
    private var bannerUri: Uri? = null
    private var galleryUri: Uri? = null

        var scannerImagePath : String? = ""
        var bannerImagePath : String? = ""

    private var currentImageField: Int = 0
    private val IMAGE_VIEW = 1
    private val SCANNER_LOGO = 2
    private val BANNER = 3
    private val GALLERY = 4
    val daysOfWeek = listOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
    private lateinit var photosAdapter: PhotosAdapter
    private lateinit var notification: AppUserData

    var logo_id = "0"
    var discount_id = "0"
    private lateinit var selectedPhotosAdapter: SelectedPhotosAdapter
    private val photos = mutableListOf<Uri>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProviderProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, daysOfWeek)
        binding.etDate.setAdapter(adapter)
        binding.etDate.setOnClickListener {
            binding.etDate.showDropDown()
        }

        binding.etOpen.setOnClickListener {
            showTimePickerDialog { selectedTime ->
                binding.etOpen.text = selectedTime
            }
        }
        binding.etClose.setOnClickListener {
            showTimePickerDialog { selectedTime ->
                binding.etClose.text = selectedTime
            }
        }
        binding.etMondayOpen.setOnClickListener {
            showTimePickerDialog { selectedTime ->
                binding.etMondayOpen.text = selectedTime
            }
        }
        binding.etMondayClose.setOnClickListener {
            showTimePickerDialog { selectedTime ->
                binding.etMondayClose.text = selectedTime
            }
        }
        binding.etTuesDayOpen.setOnClickListener {
            showTimePickerDialog { selectedTime ->
                binding.etTuesDayOpen.text = selectedTime
            }
        }
        binding.etTuesDayClose.setOnClickListener {
            showTimePickerDialog { selectedTime ->
                binding.etTuesDayClose.text = selectedTime
            }
        }
        binding.etWedDayOpen.setOnClickListener {
            showTimePickerDialog { selectedTime ->
                binding.etWedDayOpen.text = selectedTime
            }
        }
        binding.etWedDayClose.setOnClickListener {
            showTimePickerDialog { selectedTime ->
                binding.etWedDayClose.text = selectedTime
            }
        }
        binding.etThuDayOpen.setOnClickListener {
            showTimePickerDialog { selectedTime ->
                binding.etThuDayOpen.text = selectedTime
            }
        }
        binding.etThuDayClose.setOnClickListener {
            showTimePickerDialog { selectedTime ->
                binding.etThuDayClose.text = selectedTime
            }
        }
        binding.etFriDayOpen.setOnClickListener {
            showTimePickerDialog { selectedTime ->
                binding.etFriDayOpen.text = selectedTime
            }
        }
        binding.etFriDayClose.setOnClickListener {
            showTimePickerDialog { selectedTime ->
                binding.etFriDayClose.text = selectedTime
            }
        }
        binding.etSatDayOpen.setOnClickListener {
            showTimePickerDialog { selectedTime ->
                binding.etSatDayOpen.text = selectedTime
            }
        }
        binding.etSatDayClose.setOnClickListener {
            showTimePickerDialog { selectedTime ->
                binding.etSatDayClose.text = selectedTime
            }
        }

        selectedPhotosAdapter = SelectedPhotosAdapter(this, photos, this)
        binding.rvPhotos.layoutManager = GridLayoutManager(this, 3)
        binding.rvPhotos.adapter = selectedPhotosAdapter

        binding.etDate.setOnItemClickListener { parent, view, position, id ->
            val selectedDay = parent.getItemAtPosition(position) as String
            binding.etDate.setText(selectedDay)
            showCardForDay(selectedDay)
        }
        binding.progressBar.visibility = View.VISIBLE
        getProviderProfileDetails()
        Glide.with(this)
            .load(Constant.customer_ProfileImage)
            .apply(RequestOptions.placeholderOf(R.drawable.baseline_person_24))
            .into(binding.imageView)

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.setting.setOnClickListener {
            val intent = Intent(applicationContext, SettingsActivity::class.java)
            startActivity(intent)
        }
        binding.scanned.setOnClickListener {
            val intent = Intent(applicationContext, ProfileScannerActivity::class.java)
            startActivity(intent)
        }
        binding.location.setOnClickListener {
            val intent = Intent(applicationContext, ProfileCurrentLocationActivity::class.java)
            startActivity(intent)
        }
        binding.imageView.setOnClickListener {
            currentImageField = IMAGE_VIEW
            openGallery()
        }

        binding.etScannerLogo.setOnClickListener {
            currentImageField = SCANNER_LOGO
            openGallery()
        }

        binding.etBanner.setOnClickListener {
            currentImageField = BANNER
            openGallery()
        }

        binding.btnNext.setOnClickListener {
            val etName = binding.etNameSelect.text.toString()
            val etMobileNumber = binding.etMobileNo.text.toString()
            val etEmail = binding.etEmailIDSelect.text.toString()
            val alterNativeMob = binding.etAccMobileNoSelect.text.toString()
            val aadhaarNo = binding.etAccNoSelect.text.toString()
            val accountNo = binding.etBankAccNoSelect.text.toString()
            val bankName = binding.etBankAccNameSelect.text.toString()
            val ifscNo = binding.etIfscNo.text.toString()
            val branchName = binding.etCardNoSelect.text.toString()
            val twitter = binding.etTwitterSelect.text.toString()
            val google = binding.etGoodlePlay.text.toString()
            val faceBook = binding.etFacebook.text.toString()
            val website = binding.etWebsite.text.toString()
            val description = binding.etDescription.text.toString()
            val experience = binding.etExperience.text.toString()
            val etBusinessName = binding.etBusinessName.text.toString()
            val daysMap = mapOf(
                "Sunday" to "${binding.etOpen.text}|${binding.etClose.text}",
                "Monday" to "${binding.etMondayOpen.text}|${binding.etMondayClose.text}",
                "Tuesday" to "${binding.etTuesDayOpen.text}|${binding.etTuesDayClose.text}",
                "Wednesday" to "${binding.etWedDayOpen.text}|${binding.etWedDayClose.text}",
                "Thursday" to "${binding.etThuDayOpen.text}|${binding.etThuDayClose.text}",
                "Friday" to "${binding.etFriDayOpen.text}|${binding.etFriDayClose.text}",
                "Saturday" to "${binding.etSatDayOpen.text}|${binding.etSatDayClose.text}"
            )

            scannerLogoUri.let {
                scannerImagePath = getPath(it)
            }
            bannerUri.let {
                bannerImagePath = getPath(it)
            }
            imageUri.let {
                val imagePath = getPath(it)
                updateProviderProfileDetails(
                    etName,
                    "",
                    "2",
                    etEmail,
                    etMobileNumber,
                    "",
                    "",
                    "",
                    etBusinessName,
                    "",
                    ifscNo,
                    accountNo,
                    branchName,
                    bankName,
                    aadhaarNo,
                    alterNativeMob,
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    faceBook,
                    twitter,
                    google,
                    "",
                    Constant.BANK_ID,
                    Constant.API_KEY,
                    Utility.getDeviceId(applicationContext),
                    Utility.deviceType,
                    Utility.deviceToken,
                    Constant.APP_USER_ID.toString(),
                    Constant.APP_VERSION_NAME.toString(),
                    Constant.APP_USER_KEY,
                    0.0,
                    0.0,
                    imagePath ?: "", website, description, experience, daysMap, scannerImagePath  ?: "", logo_id, bannerImagePath ?: "", discount_id
                )
                Log.d("ProviderProfileDetails", "Name: ${etName}")
                Log.d("ProviderProfileDetails", "Email: ${etEmail}")
                Log.d("ProviderProfileDetails", "Mobile: ${etMobileNumber}")
                Log.d("ProviderProfileDetails", "IFSC No: ${ifscNo}")
                Log.d("ProviderProfileDetails", "Account No: ${accountNo}")
                Log.d("ProviderProfileDetails", "Branch Name: ${branchName}")
                Log.d("ProviderProfileDetails", "Bank Name: ${bankName}")
                Log.d("ProviderProfileDetails", "Aadhaar No: ${aadhaarNo}")
                Log.d("ProviderProfileDetails", "Alternative Mobile: ${alterNativeMob}")
                Log.d("ProviderProfileDetails", "Facebook: ${faceBook}")
                Log.d("ProviderProfileDetails", "Twitter: ${twitter}")
                Log.d("ProviderProfileDetails", "Google: ${google}")
                Log.d("ProviderProfileDetails", "Bank ID: ${Constant.BANK_ID}")
                Log.d("ProviderProfileDetails", "API Key: ${Constant.API_KEY}")
                Log.d("ProviderProfileDetails", "Device ID: ${Utility.getDeviceId(applicationContext)}")
                Log.d("ProviderProfileDetails", "Device Type: ${Utility.deviceType}")
                Log.d("ProviderProfileDetails", "Device Token: ${Utility.deviceToken}")
                Log.d("ProviderProfileDetails", "App User ID: ${Constant.APP_USER_ID}")
                Log.d("ProviderProfileDetails", "App Version Name: ${Constant.APP_VERSION_NAME}")
                Log.d("ProviderProfileDetails", "App User Key: ${Constant.APP_USER_KEY}")
                Log.d("ProviderProfileDetails", "Latitude: 0.0")
                Log.d("ProviderProfileDetails", "Longitude: 0.0")
                Log.d("ProviderProfileDetails", "Image Path: $imagePath")
                Log.d("ProviderProfileDetails", "Website: $website")
                Log.d("ProviderProfileDetails", "Description: $description")
                Log.d("ProviderProfileDetails", "Experience: $experience")
                Log.d("ProviderProfileDetails", "Days Map: $daysMap")
                Log.d("ProviderProfileDetails", "Scanner Image Path: $scannerImagePath")
                Log.d("ProviderProfileDetails", "Scanner Logo: ${SCANNER_LOGO.toString()}")
                Log.d("ProviderProfileDetails", "Banner Image Path: $bannerImagePath")
                Log.d("ProviderProfileDetails", "Banner: ${BANNER.toString()}")
            }
        }
    }

    private fun showCardForDay(day: String) {
        when (day) {
            "Sunday" -> binding.sundayCard.visibility = View.VISIBLE
            "Monday" -> binding.mondayCard.visibility = View.VISIBLE
            "Tuesday" -> binding.tuesCard.visibility = View.VISIBLE
            "Wednesday" -> binding.WedCard.visibility = View.VISIBLE
            "Thursday" -> binding.thusCard.visibility = View.VISIBLE
            "Friday" -> binding.friCard.visibility = View.VISIBLE
            "Saturday" -> binding.satCard.visibility = View.VISIBLE
        }
    }
    private fun updateProviderProfileDetails(
        userProfileName: String,
        services: String,
        appUserType: String,
        userEmail: String,
        userMobile: String,
        packageId: String,
        validityDays: String,
        packagePrice: String,
        businessName: String,
        aboutBusiness: String,
        ifscCode: String,
        accountNumber: String,
        branchName: String,
        bankName: String,
        addharNo: String,
        optionalMobileNo: String,
        franchiseNo: String,
        gstNo: String,
        landlineNo: String,
        registrationNo: String,
        faxNo: String,
        workingHours: String,
        facebookProfile: String,
        twitterProfile: String,
        googlePlusProfile: String,
        linkedinProfile: String,
        bankInfoId: String,
        apiKey: String,
        deviceId: String,
        deviceType: String,
        deviceToken: String,
        appUserId: String,
        appVersion: String,
        appUserKey: String,
        lat: Double,
        lng: Double,
        imagePath: String,
        website: String,
        description: String,
        experience: String,
        days: Map<String, String>,
        imageLogoPath: String,
        logo_image_id: String,
        imageBannerPath: String,
        discount_banner_id: String
    ) {
        binding.progressBar1.visibility = View.VISIBLE
        val imageName = File(getPath(imageUri).toString()).name
        val imageFile = File(getPath(imageUri).toString())

        val logoImageName = File(getPath(scannerLogoUri).toString()).name
        val logoImageFile = File(getPath(scannerLogoUri).toString())

        val bannerImageName = File(getPath(bannerUri).toString()).name
        val bannerImageFile = File(getPath(bannerUri).toString())

        val gson = Gson()
        val json = gson.toJson(days)
        Log.i("TAG", "updateProviderProfileDetails: " + json)

        val userProfileNameMultiPart: RequestBody = userProfileName.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val servicesMultiPart: RequestBody = services.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val appUserTypeMultiPart: RequestBody = appUserType.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val userEmailMultiPart: RequestBody = userEmail.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val userMobileMultiPart: RequestBody = userMobile.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val packageIdMultiPart: RequestBody = packageId.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val validityDaysMultiPart: RequestBody = validityDays.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val packagePriceMultiPart: RequestBody = packagePrice.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val businessNameMultiPart: RequestBody = businessName.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val aboutBusinessMultiPart: RequestBody = aboutBusiness.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val ifscCodeMultiPart: RequestBody = ifscCode.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val accountNumberMultiPart: RequestBody = accountNumber.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val branchNameMultiPart: RequestBody = branchName.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val bankNameMultiPart: RequestBody = bankName.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val addharNoMultiPart: RequestBody = addharNo.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val optionalMobileNoMultiPart: RequestBody = optionalMobileNo.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val franchiseNoMultiPart: RequestBody = franchiseNo.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val gstNoMultiPart: RequestBody = gstNo.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val landlineNoMultiPart: RequestBody = landlineNo.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val registrationNoMultiPart: RequestBody = registrationNo.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val faxNoMultiPart: RequestBody = faxNo.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val workingHoursMultiPart: RequestBody = workingHours.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val facebookProfileMultiPart: RequestBody = facebookProfile.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val twitterProfileMultiPart: RequestBody = twitterProfile.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val googlePlusProfileMultiPart: RequestBody = googlePlusProfile.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val linkedinProfileMultiPart: RequestBody = linkedinProfile.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val bankInfoIdMultiPart: RequestBody = bankInfoId.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val apiKeyMultiPart: RequestBody = apiKey.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val deviceIdMultiPart: RequestBody = deviceId.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val deviceTypeMultiPart: RequestBody = deviceType.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val deviceTokenMultiPart: RequestBody = deviceToken.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val appUserIdMultiPart: RequestBody = appUserId.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val appVersionMultiPart: RequestBody = appVersion.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val appUserKeyMultiPart: RequestBody = appUserKey.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val latMultiPart: RequestBody = lat.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val lngMultiPart: RequestBody = lng.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val webMultiPart: RequestBody = website.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val desMultiPart: RequestBody = description.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val exMultiPart: RequestBody = experience.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val daysMultiPart: RequestBody = days.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val logoIdMultiPart: RequestBody = logo_image_id.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val discountIdMultiPart: RequestBody = discount_banner_id.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        Log.i("TAG", "updateProviderProfileDetails: " + daysMultiPart.toString())


        val requestFile: RequestBody? = if (imageFile.exists() && imageFile.length() > 0) {
            imageFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        } else {
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), "")
        }
        val multipartBody = MultipartBody.Part.createFormData("service_image", imageName, requestFile!!)

        val requestLogoFile: RequestBody? = if (logoImageFile.exists() && logoImageFile.length() > 0) {
            logoImageFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        } else {
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), "")
        }
        val multipartLogoBody = MultipartBody.Part.createFormData("logo_image_name", logoImageName, requestLogoFile!!)

        val requestBannerFile: RequestBody? = if (bannerImageFile.exists() && bannerImageFile.length() > 0) {
            bannerImageFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        } else {
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), "")
        }
        val multipartBannerBody = MultipartBody.Part.createFormData("discount_banner_name", bannerImageName, requestBannerFile!!)



        val call = RetrofitClient.api.providerProfileUpdate( userProfileNameMultiPart,
            servicesMultiPart,
            appUserTypeMultiPart,
            userEmailMultiPart,
            userMobileMultiPart,
            packageIdMultiPart,
            validityDaysMultiPart,
            packagePriceMultiPart,
            businessNameMultiPart,
            aboutBusinessMultiPart,
            ifscCodeMultiPart,
            accountNumberMultiPart,
            branchNameMultiPart,
            bankNameMultiPart,
            addharNoMultiPart,
            optionalMobileNoMultiPart,
            franchiseNoMultiPart,
            gstNoMultiPart,
            landlineNoMultiPart,
            registrationNoMultiPart,
            faxNoMultiPart,
            workingHoursMultiPart,
            facebookProfileMultiPart,
            twitterProfileMultiPart,
            googlePlusProfileMultiPart,
            linkedinProfileMultiPart,
            bankInfoIdMultiPart,
            apiKeyMultiPart,
            deviceIdMultiPart,
            deviceTypeMultiPart,
            deviceTokenMultiPart,
            appUserIdMultiPart,
            appVersionMultiPart,
            appUserKeyMultiPart,
            latMultiPart,
            lngMultiPart,
            multipartBody,
            webMultiPart, desMultiPart, exMultiPart, daysMultiPart,multipartLogoBody, logoIdMultiPart, multipartBannerBody, discountIdMultiPart)
        call.enqueue(object : Callback<ProviderProfileResponse> {
            override fun onResponse(
                call: Call<ProviderProfileResponse>,
                response: Response<ProviderProfileResponse>
            ) {
                if (response.isSuccessful) {
                    val updateProfileResponse = response.body()
                    Log.i("TAG", "onResponse: " + response.body())
                    updateProfileResponse?.let {
                        if (it.status == 1) {
                            binding.progressBar1.visibility = View.GONE
                            if (!it.data.profile_image.isNullOrBlank()) {
                                Constant.customer_ProfileImage = it.data.profile_image
                            }
                            Glide.with(applicationContext)
                                .load(it.data.profile_image)
                                .apply(RequestOptions.placeholderOf(R.drawable.baseline_person_24))
                                .into(binding.imageView )
                            val resultIntent = Intent()
                            setResult(Activity.RESULT_OK, resultIntent)
                            val intent =
                                Intent(applicationContext, SettingsActivity::class.java)
                            startActivity(intent)
                            finish()
                            Toast.makeText(
                                applicationContext,
                                "Profile details updated successfully!",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            binding.progressBar1.visibility = View.GONE
                            Toast.makeText(
                                applicationContext,
                                it.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    binding.progressBar1.visibility = View.GONE
                    Log.e("API", "API call failed with code ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ProviderProfileResponse>, t: Throwable) {
                binding.progressBar1.visibility = View.GONE
                Log.e("API", "API call failed with exception: ${t.message}")
                Toast.makeText(
                    applicationContext,
                    t.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun getProviderProfileDetails() {
        val call = RetrofitClient.api.getProviderProfileDetails(
            Constant.MOBILE_NO,
            Constant.API_KEY,
            Utility.getDeviceId(applicationContext),
            Utility.deviceToken,
            Constant.APP_USER_KEY,
            Constant.APP_USER_ID.toString(),
            Constant.APP_VERSION_NAME.toString()
        )
        call.enqueue(object : Callback<UserData> {
            override fun onResponse(call: Call<UserData>, response: Response<UserData>) {
                if (response.isSuccessful) {
                    binding.progressBar.visibility = View.GONE
                    Constant.customer_ProfileImage =
                        response.body()?.data?.app_user_data?.profile_image ?: ""
                    logo_id =
                        response.body()?.data?.app_user_data?.business_logo_id ?: "0"
                    discount_id =
                        response.body()?.data?.app_user_data?.discount_id ?: "0"

                    notification = response.body()?.data?.app_user_data!!
                    photosAdapter = PhotosAdapter(applicationContext, notification)
                    val gridLayoutManager = GridLayoutManager(applicationContext, 4)
                    binding.rvUploaded.layoutManager = gridLayoutManager
                    binding.rvUploaded.adapter = photosAdapter

                    Glide.with(applicationContext)
                        .load(response.body()?.data?.app_user_data?.profile_image)
                        .apply(RequestOptions.placeholderOf(R.drawable.baseline_person_24))
                        .into(binding.imageView)

                    if (!response.body()?.data?.app_user_data?.business_logo_image.isNullOrEmpty()) {
                        binding.ivLogo.visibility = View.VISIBLE
                        Glide.with(applicationContext)
                            .load(response.body()?.data?.app_user_data?.business_logo_image)
                            .apply(RequestOptions.placeholderOf(R.drawable.baseline_person_24))
                            .into(binding.ivLogo)
                    }

                    if (!response.body()?.data?.app_user_data?.discount_image.isNullOrEmpty()) {
                        binding.ivBanner.visibility = View.VISIBLE
                        Glide.with(applicationContext)
                            .load(response.body()?.data?.app_user_data?.discount_image)
                            .apply(RequestOptions.placeholderOf(R.drawable.baseline_person_24))
                            .into(binding.ivBanner)
                    }

                    binding.etMobileNo.text = Editable.Factory.getInstance().newEditable(
                        response.body()?.data?.app_user_data?.user_mobile
                            ?: ""
                    )
                    binding.etEmailIDSelect.text = Editable.Factory.getInstance().newEditable(
                        response.body()?.data?.app_user_data?.user_email
                            ?: ""
                    )
                    binding.etNameSelect.text = Editable.Factory.getInstance().newEditable(
                        response.body()?.data?.app_user_data?.user_profile_name
                            ?: ""
                    )
                    binding.etAccMobileNoSelect.text = Editable.Factory.getInstance().newEditable(
                        response.body()?.data?.app_user_data?.optional_mobile_no
                            ?: ""
                    )
                    binding.etAccNoSelect.text = Editable.Factory.getInstance().newEditable(
                        response.body()?.data?.app_user_data?.addhar_no
                            ?: ""
                    )
                    Constant.BANK_ID = response.body()?.data?.app_user_data?.bank_info_id
                        ?: ""
                    binding.etBankAccNoSelect.text = Editable.Factory.getInstance().newEditable(
                        response.body()?.data?.app_user_data?.account_number
                            ?: ""
                    )
                    binding.etBankAccNameSelect.text = Editable.Factory.getInstance().newEditable(
                        response.body()?.data?.app_user_data?.bank_name
                            ?: ""
                    )
                    binding.etIfscNo.text = Editable.Factory.getInstance().newEditable(
                        response.body()?.data?.app_user_data?.ifsc_code
                            ?: ""
                    )
                    binding.etCardNoSelect.text = Editable.Factory.getInstance().newEditable(
                        response.body()?.data?.app_user_data?.branch_name
                            ?: ""
                    )
                    binding.etTwitterSelect.text = Editable.Factory.getInstance().newEditable(
                        response.body()?.data?.app_user_data?.tweeter_profile
                            ?: ""
                    )
                    binding.etGoodlePlay.text = Editable.Factory.getInstance().newEditable(
                        response.body()?.data?.app_user_data?.gplus_profile
                            ?: ""
                    )
                    binding.etFacebook.text = Editable.Factory.getInstance().newEditable(
                        response.body()?.data?.app_user_data?.facebook_profile
                            ?: ""
                    )
                    binding.etWebsite.text = Editable.Factory.getInstance().newEditable(
                        response.body()?.data?.app_user_data?.website
                            ?: ""
                    )
                    binding.etDescription.text = Editable.Factory.getInstance().newEditable(
                        response.body()?.data?.app_user_data?.description
                            ?: ""
                    )
                    binding.etBusinessName.text = Editable.Factory.getInstance().newEditable(
                        response.body()?.data?.app_user_data?.business_name
                            ?: ""
                    )
                    binding.etExperience.text = Editable.Factory.getInstance().newEditable(
                        response.body()?.data?.app_user_data?.experience
                            ?: ""
                    )

                    if (!response.body()?.data?.app_user_data?.shop_time.isNullOrEmpty()) {
                        if (response.body()?.data?.app_user_data?.shop_time?.get(0)?.shop_open_days?.trim() == "Sunday") {
                            if (!response.body()?.data?.app_user_data?.shop_time?.get(0)?.shop_open_time.isNullOrEmpty() || !response.body()?.data?.app_user_data?.shop_time?.get(
                                    0
                                )?.shop_close_time.isNullOrEmpty()
                            ) {
                                binding.sundayCard.visibility = View.VISIBLE
                                binding.etOpen.text = Editable.Factory.getInstance().newEditable(
                                    response.body()?.data?.app_user_data?.shop_time!![0].shop_open_time
                                )
                                binding.etClose.text = Editable.Factory.getInstance().newEditable(
                                    response.body()?.data?.app_user_data?.shop_time!![0].shop_close_time
                                )
                            }
                        }
                        if (response.body()?.data?.app_user_data?.shop_time?.get(1)?.shop_open_days?.trim() == "Monday") {
                            if (!response.body()?.data?.app_user_data?.shop_time?.get(1)?.shop_open_time.isNullOrEmpty() || !response.body()?.data?.app_user_data?.shop_time?.get(
                                    1
                                )?.shop_close_time.isNullOrEmpty()
                            ) {
                                binding.mondayCard.visibility = View.VISIBLE
                                binding.etMondayOpen.text =
                                    Editable.Factory.getInstance().newEditable(
                                        response.body()?.data?.app_user_data?.shop_time!![1].shop_open_time
                                    )
                                binding.etMondayClose.text =
                                    Editable.Factory.getInstance().newEditable(
                                        response.body()?.data?.app_user_data?.shop_time!![1].shop_close_time
                                    )
                            }
                        }
                        if (response.body()?.data?.app_user_data?.shop_time?.get(2)?.shop_open_days?.trim() == "Tuesday") {
                            if (!response.body()?.data?.app_user_data?.shop_time?.get(2)?.shop_open_time.isNullOrEmpty() || !response.body()?.data?.app_user_data?.shop_time?.get(
                                    2
                                )?.shop_close_time.isNullOrEmpty()
                            ) {
                                binding.tuesCard.visibility = View.VISIBLE
                                binding.etTuesDayOpen.text =
                                    Editable.Factory.getInstance().newEditable(
                                        response.body()?.data?.app_user_data?.shop_time!![2].shop_open_time
                                    )
                                binding.etTuesDayClose.text =
                                    Editable.Factory.getInstance().newEditable(
                                        response.body()?.data?.app_user_data?.shop_time!![2].shop_close_time
                                    )
                            }
                        }
                        if (response.body()?.data?.app_user_data?.shop_time?.get(3)?.shop_open_days?.trim() == "Wednesday") {
                            if (!response.body()?.data?.app_user_data?.shop_time?.get(3)?.shop_open_time.isNullOrEmpty() || !response.body()?.data?.app_user_data?.shop_time?.get(
                                    3
                                )?.shop_close_time.isNullOrEmpty()
                            ) {
                                binding.WedCard.visibility = View.VISIBLE
                                binding.etWedDayOpen.text =
                                    Editable.Factory.getInstance().newEditable(
                                        response.body()?.data?.app_user_data?.shop_time!![3].shop_open_time
                                    )
                                binding.etWedDayClose.text =
                                    Editable.Factory.getInstance().newEditable(
                                        response.body()?.data?.app_user_data?.shop_time!![3].shop_close_time
                                    )
                            }
                        }
                        if (response.body()?.data?.app_user_data?.shop_time?.get(4)?.shop_open_days?.trim() == "Thursday") {
                            if (!response.body()?.data?.app_user_data?.shop_time?.get(4)?.shop_open_time.isNullOrEmpty() || !response.body()?.data?.app_user_data?.shop_time?.get(
                                    4
                                )?.shop_close_time.isNullOrEmpty()
                            ) {
                                binding.thusCard.visibility = View.VISIBLE
                                binding.etThuDayOpen.text =
                                    Editable.Factory.getInstance().newEditable(
                                        response.body()?.data?.app_user_data?.shop_time!![4].shop_open_time
                                    )
                                binding.etThuDayClose.text =
                                    Editable.Factory.getInstance().newEditable(
                                        response.body()?.data?.app_user_data?.shop_time!![4].shop_close_time
                                    )
                            }
                        }

                        if (response.body()?.data?.app_user_data?.shop_time?.get(5)?.shop_open_days?.trim() == "Friday") {
                            if (!response.body()?.data?.app_user_data?.shop_time?.get(5)?.shop_open_time.isNullOrEmpty() || !response.body()?.data?.app_user_data?.shop_time?.get(
                                    5
                                )?.shop_close_time.isNullOrEmpty()
                            ) {
                                binding.friCard.visibility = View.VISIBLE
                                binding.etFriDayOpen.text =
                                    Editable.Factory.getInstance().newEditable(
                                        response.body()?.data?.app_user_data?.shop_time!![5].shop_open_time
                                    )
                                binding.etFriDayClose.text =
                                    Editable.Factory.getInstance().newEditable(
                                        response.body()?.data?.app_user_data?.shop_time!![5].shop_close_time
                                    )
                            }
                        }

                        if (response.body()?.data?.app_user_data?.shop_time?.get(6)?.shop_open_days?.trim() == "Saturday") {
                            if (!response.body()?.data?.app_user_data?.shop_time?.get(6)?.shop_open_time.isNullOrEmpty() || !response.body()?.data?.app_user_data?.shop_time?.get(
                                    6
                                )?.shop_close_time.isNullOrEmpty()
                            ) {
                                binding.satCard.visibility = View.VISIBLE
                                binding.etSatDayOpen.text =
                                    Editable.Factory.getInstance().newEditable(
                                        response.body()?.data?.app_user_data?.shop_time!![6].shop_open_time
                                    )
                                binding.etSatDayClose.text =
                                    Editable.Factory.getInstance().newEditable(
                                        response.body()?.data?.app_user_data?.shop_time!![6].shop_close_time
                                    )
                            }
                        }
                    }
                    Log.i("TAG", "responseBody: ${response.body()}")
                } else {
                    binding.progressBar.visibility = View.GONE
                    val errorBody = response.errorBody()
                    Toast.makeText(applicationContext, response.message(), Toast.LENGTH_SHORT)
                        .show()
                    Log.i("TAG", "responseBody: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<UserData>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
                Log.i("TAG", "responseBody: Something went wrong.")

            }
        })
    }

    private fun openGallery() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Request the permission
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    READ_EXTERNAL_STORAGE_REQUEST_CODE
                )
            } else {
                val galleryIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galleryIntent, FILE_REQUEST_CODE)
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_MEDIA_IMAGES
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                    READ_EXTERNAL_STORAGE_REQUEST_CODE
                )
            } else {
                val galleryIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galleryIntent, FILE_REQUEST_CODE)
            }

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            READ_EXTERNAL_STORAGE_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, proceed to open the gallery
                    openGallery()
                } else {
                    // Permission denied, show a message or take appropriate action
                    Toast.makeText(
                        this,
                        "Permission denied. To use this feature, please grant the permission in App Settings.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val selectedImage: Uri? = data?.data
            selectedImage?.let { it ->
//                imageUri = it
                when (currentImageField) {
                    IMAGE_VIEW -> {
                        imageUri = it
                        Glide.with(this)
                            .load(imageUri)
                            .apply(RequestOptions.placeholderOf(R.drawable.baseline_person_24))
                            .into(binding.imageView)
                    }
                    SCANNER_LOGO -> {
                        scannerLogoUri = it
                        Glide.with(this)
                            .load(scannerLogoUri)
                            .apply(RequestOptions.placeholderOf(R.drawable.baseline_person_24))
                            .into(binding.ivLogo)
                        binding.ivLogo.visibility = View.VISIBLE
                    }
                    BANNER -> {
                        bannerUri = it
                        Glide.with(this)
                            .load(bannerUri)
                            .apply(RequestOptions.placeholderOf(R.drawable.baseline_person_24))
                            .into(binding.ivBanner)
                        binding.ivBanner.visibility = View.VISIBLE
                    }
                    GALLERY -> {
                        galleryUri = it
                        selectedPhotosAdapter.addPhoto(it)
                        galleryUri.let {
                            val imagePath = getPath(it)
                            uploadServiceProviderBanner(
                                Constant.MOBILE_NO,
                                Constant.API_KEY,
                                Utility.getDeviceId(applicationContext),
                                Utility.deviceType,
                                Utility.deviceToken,
                                Constant.APP_USER_ID.toString(),
                                Constant.APP_VERSION_NAME.toString(),
                                Constant.APP_USER_KEY,
                                "0.0",
                                "0.0",
                                imagePath ?: ""
                            )
                        }
                    }

                    else -> {}
                }
            }
        }
    }

    private fun showTimePickerDialog(onTimeSelected: (String) -> Unit) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Time")

        val timesArray = arrayOf(
            "6 AM", "7 AM", "8 AM", "9 AM", "10 AM", "11 AM", "12 PM", "01 PM", "02 PM", "03 PM", "04 PM", "05 PM",
            "06 PM", "07 PM", "08 PM", "09 PM", "10 PM", "11 PM", "12 AM", "01 AM", "02 AM", "03 AM", "04 AM", "05 AM", "None"
        )

        builder.setItems(timesArray) { dialog, which ->
            val selectedTime = timesArray[which]
            onTimeSelected(selectedTime)
        }

        builder.create().show()
    }


    private fun getPath(uri: Uri?): String? {
        val projection = arrayOf(MediaStore.MediaColumns.DATA)
        val cursor = uri?.let { contentResolver.query(it, projection, null, null, null) }
        cursor?.let {
            val column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
            cursor.moveToFirst()
            val imagePath = cursor.getString(column_index)
            cursor.close()
            return imagePath
        }
        return null
    }

    override fun openPhotoGallery() {
        currentImageField = GALLERY
        openGallery()
    }

    private fun uploadServiceProviderBanner(
        userMobile: String,
        apiKey: String,
        deviceId: String,
        deviceType: String,
        deviceToken: String,
        appUserId: String,
        appVersion: String,
        appUserKey: String,
        lat: String,
        lng: String,
        imagePath: String
    ) {
        val userMobileMultiPart: RequestBody = userMobile.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val apiKeyMultiPart: RequestBody = apiKey.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val deviceIdMultiPart: RequestBody = deviceId.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val deviceTypeMultiPart: RequestBody = deviceType.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val deviceTokenMultiPart: RequestBody = deviceToken.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val appUserIdMultiPart: RequestBody = appUserId.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val appVersionMultiPart: RequestBody = appVersion.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val appUserKeyMultiPart: RequestBody = appUserKey.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val latMultiPart: RequestBody = lat.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val lngMultiPart: RequestBody = lng.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        val imageName = File(getPath(galleryUri).toString()).name
        val imageFile = File(getPath(galleryUri).toString())

        val requestFile: RequestBody = imageFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val multipartBody = MultipartBody.Part.createFormData("provider_banner", imageName, requestFile)

        val apiService = RetrofitClient.retrofit.create(TutionServiceApi::class.java)
        binding.progressBar.visibility = View.VISIBLE

        val call = apiService.uploadServiceProviderBanner(userMobileMultiPart, apiKeyMultiPart, deviceIdMultiPart, deviceTypeMultiPart, deviceTokenMultiPart, appUserIdMultiPart, appVersionMultiPart,
            appUserKeyMultiPart, latMultiPart, lngMultiPart, multipartBody)

        call.enqueue(object : Callback<ProviderGalleryResponse> {
            override fun onResponse(
                call: Call<ProviderGalleryResponse>,
                response: Response<ProviderGalleryResponse>
            ) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    Log.i("TAG", "onResponse: "+ response.body())
                    apiResponse?.let {
                        if (it.status == 1) {
                            binding.progressBar.visibility = View.GONE
//                            Constant.customer_ProfileImage = it.banner
//                            Glide.with(applicationContext)
//                                .load(it.data.profile_image)
//                                .apply(RequestOptions.placeholderOf(com.flyngener.tutionservice.R.drawable.baseline_person_24))
//                                .into(binding.imageView )
                        } else {
                            Toast.makeText(
                                this@ProviderProfileActivity,
                                "Profile pic upload failed: ${it.msg}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    Toast.makeText(
                        this@ProviderProfileActivity,
                        "Profile pic upload failed: ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ProviderGalleryResponse>, t: Throwable) {
                Toast.makeText(
                    this@ProviderProfileActivity,
                    "Profile pic upload failed: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}