package com.appointment.tutionservice

import android.Manifest
import android.R
import android.app.Activity
import android.app.AlertDialog
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.appointment.tutionservice.Constant.API_KEY
import com.appointment.tutionservice.Constant.APP_USER_ID
import com.appointment.tutionservice.Constant.APP_USER_KEY
import com.appointment.tutionservice.Constant.APP_VERSION_NAME
import com.appointment.tutionservice.Constant.CITY
import com.appointment.tutionservice.Constant.EMAIL
import com.appointment.tutionservice.Constant.GENDER
import com.appointment.tutionservice.Constant.MOBILE_NO
import com.appointment.tutionservice.Constant.NAME
import com.appointment.tutionservice.Constant.PINCODE
import com.appointment.tutionservice.databinding.ActivityCustomerProfileBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class CustomerProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCustomerProfileBinding
    private val cityNames = mutableListOf<String>()
    private val stateNames = mutableListOf<String>()
    private var selectedCityId: String? = null
    private val READ_EXTERNAL_STORAGE_REQUEST_CODE = 123
    private val FILE_REQUEST_CODE = 100
    private lateinit var imageUri: Uri

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        getCustomerProfileDetails()
        binding.progressBar.visibility = View.VISIBLE

        binding.etGenderSelect.setOnClickListener {
            showGenderOptionsDialog()
        }
        binding.imageView.setOnClickListener {
            openGallery()
        }
        Glide.with(this)
            .load(Constant.customer_ProfileImage)
            .apply(RequestOptions.placeholderOf(com.appointment.tutionservice.R.drawable.baseline_person_24))
            .into(binding.imageView)

        binding.btnNext.setOnClickListener {
            val etName = binding.etNameSelect.text.toString()
            val etMobileNumber = binding.etMobileNo.text.toString()
            val etEmail = binding.etEmailIDSelect.text.toString()
            val etCity = binding.etCitySelect.text.toString()
            val etState = binding.etStateSelect.text.toString()
            val gender = binding.etGenderSelect.text.toString()
            val pinCode = binding.etAddressSelect.text.toString()

            if (etName.isEmpty() || etMobileNumber.isEmpty() || etCity.isEmpty() || etEmail.isEmpty() || pinCode.isEmpty() || etState.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (gender.isEmpty()) {
                Toast.makeText(this, "Please select a gender", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val isProfilePhotoUpdated = ::imageUri.isInitialized
            if (isProfilePhotoUpdated) {
                imageUri.let {
                    val imagePath = getPath(it)
                    uploadProfileImage(
                        MOBILE_NO,
                        API_KEY,
                        Utility.getDeviceId(applicationContext),
                        Utility.deviceType,
                        Utility.deviceToken,
                        APP_USER_ID.toString(),
                        APP_VERSION_NAME.toString(),
                        APP_USER_KEY,
                        "0.0",
                        "0.0",
                        imagePath ?: ""
                    )
                }
            }

            selectedCityId?.let { it1 ->
                updateCustomerProfileDetails(
                    etName,
                    etEmail,
                    gender,
                    it1,
                    pinCode,
                    etMobileNumber,
                    API_KEY,
                    Utility.getDeviceId(applicationContext),
                    Utility.deviceType,
                    Utility.deviceToken,
                    APP_USER_ID.toString(),
                    APP_VERSION_NAME.toString(),
                    APP_USER_KEY,
                    0.0,
                    0.0,
                    it1
                )
            }

            val resultIntent = Intent()
            setResult(Activity.RESULT_OK, resultIntent)
            binding.progressBar.visibility = View.VISIBLE
            val intent =
                Intent(applicationContext, CustomerMainActivity::class.java)
            startActivity(intent)
            finish()
            Toast.makeText(
                applicationContext,
                "Profile details updated successfully!",
                Toast.LENGTH_SHORT
            ).show()
//            val isProfilePhotoUpdated = ::imageUri.isInitialized
//            val isProfileDetailsUpdated = isProfileDetailsUpdated(etName,etMobileNumber,etEmail,etCity,gender,pinCode)
//
//            if (isProfilePhotoUpdated || isProfileDetailsUpdated) {
//                if (isProfilePhotoUpdated) {
//                    imageUri.let {
//                        val imagePath = getPath(it)
//                        uploadProfileImage(
//                            MOBILE_NO,
//                            API_KEY,
//                            Utility.getDeviceId(applicationContext),
//                            Utility.deviceType,
//                            Utility.deviceToken,
//                            APP_USER_ID.toString(),
//                            APP_VERSION_NAME.toString(),
//                            APP_USER_KEY,
//                            "0.0",
//                            "0.0",
//                            imagePath ?: ""
//                        )
//                    }
//                }
//                binding.progressBar.visibility = View.VISIBLE
//                selectedCityId?.let { it1 ->
//                    updateCustomerProfileDetails(
//                        etName,
//                        etEmail,
//                        gender,
//                        it1,
//                        pinCode,
//                        etMobileNumber,
//                        API_KEY,
//                        Utility.getDeviceId(applicationContext),
//                        Utility.deviceType,
//                        Utility.deviceToken,
//                        APP_USER_ID.toString(),
//                        APP_VERSION_NAME.toString(),
//                        APP_USER_KEY,
//                        0.0,
//                        0.0,
//                        it1
//                    )
//                }
//                val resultIntent = Intent()
//                setResult(Activity.RESULT_OK, resultIntent)
//
//                val intent =
//                    Intent(applicationContext, CustomerMainActivity::class.java)
//                startActivity(intent)
//                finish()
//                Toast.makeText(
//                    applicationContext,
//                    "Profile details updated successfully!",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//            else {
//                Toast.makeText(this, "No changes made", Toast.LENGTH_SHORT).show()
//            }
        }


        GlobalScope.launch(Dispatchers.Main) {
            val response = withContext(Dispatchers.IO) {
                RetrofitClient.api.allTheData()
            }
            if (response.isSuccessful) {
                val customerDetailsResponse = response.body()
                customerDetailsResponse?.let { handleCityListResponse(it.data.all_citys) }
            } else {
                Log.i("TAG", "API Call failed with error code: ${response.code()}")
            }
        }
    }

    private fun isProfileDetailsUpdated(userName: String, mobile:String, etEmail: String, city: String, gender: String, pinCode:String): Boolean {
        return userName != NAME || mobile != MOBILE_NO || etEmail != EMAIL || city != CITY || gender != GENDER || pinCode != PINCODE
    }

    private fun showGenderOptionsDialog() {
        val genderOptions = arrayOf("Male", "Female", "Others")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Gender")
        builder.setItems(genderOptions) { _, which ->
            val selectedGender = genderOptions[which]
            binding.etGenderSelect.text = selectedGender
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun getCustomerProfileDetails() {
        val call = RetrofitClient.api.getCustomerProfileDetails(
            MOBILE_NO,
            API_KEY,
            Utility.getDeviceId(applicationContext),
            Utility.deviceToken,
            APP_USER_KEY,
            APP_USER_ID.toString(),
            APP_VERSION_NAME.toString()
        )
        call.enqueue(object : Callback<CustomerProfileResponse> {
            override fun onResponse(call: Call<CustomerProfileResponse>, response: Response<CustomerProfileResponse>) {
                if (response.isSuccessful) {
                    binding.progressBar.visibility = View.GONE
                    selectedCityId = response.body()?.data?.appUserData?.cityId
                    Constant.customer_ProfileImage =
                        response.body()?.data?.appUserData?.profileImage ?: ""

                    Glide.with(applicationContext)
                        .load(response.body()?.data?.appUserData?.profileImage)
                        .apply(RequestOptions.placeholderOf(com.appointment.tutionservice.R.drawable.baseline_person_24))
                        .into(binding.imageView )
                    binding.etMobileNo.text = Editable.Factory.getInstance().newEditable(response.body()?.data?.appUserData?.userMobile
                        ?: ""
                    )
                    binding.etEmailIDSelect.text = Editable.Factory.getInstance().newEditable(response.body()?.data?.appUserData?.userEmail
                        ?: ""
                    )
                    binding.etNameSelect.text = Editable.Factory.getInstance().newEditable(response.body()?.data?.appUserData?.userProfileName
                        ?: ""
                    )
                    binding.etGenderSelect.text = Editable.Factory.getInstance().newEditable(response.body()?.data?.appUserData?.gender
                        ?: ""
                    )
                    binding.etCitySelect.text = Editable.Factory.getInstance().newEditable(response.body()?.data?.appUserData?.cityName
                        ?: ""
                    )
                    binding.etStateSelect.text = Editable.Factory.getInstance().newEditable(response.body()?.data?.appUserData?.stateName
                        ?: ""
                    )
                    binding.etAddressSelect.text = Editable.Factory.getInstance().newEditable(response.body()?.data?.appUserData?.pinCode
                        ?: ""
                    )
                    Log.i("TAG", "responseBody: ${response.body()}")
                } else {
                    binding.progressBar.visibility = View.GONE
                    val errorBody = response.errorBody()
                    Toast.makeText(applicationContext, response.message(), Toast.LENGTH_SHORT).show()
                    Log.i("TAG", "responseBody: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<CustomerProfileResponse>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
                Log.i("TAG", "responseBody: ${t.message}")

            }
        })
    }


    private fun updateCustomerProfileDetails(
        userProfileName: String, userEmail: String, gender: String, city: String, pinCode: String, userMobile: String, apiKey: String,
        deviceId: String, deviceType: String, deviceToken: String, appUserId: String, appVersion: String, appUserKey: String, latitude: Double, longitude: Double, state: String
    ) {
        val addCustomer = CustomerProfileUpdateData( userProfileName, userEmail, gender, city, pinCode, userMobile,
            apiKey, deviceId, deviceType, deviceToken, appUserId, appVersion, appUserKey, latitude, longitude, state)

        Log.i("TAG", "addCustomer: $addCustomer")

        val call = RetrofitClient.api.customerProfileUpdate(addCustomer)
        call.enqueue(object : Callback<UpdateProfileResponse> {
            override fun onResponse(call: Call<UpdateProfileResponse>, response: Response<UpdateProfileResponse>) {
                if (response.isSuccessful) {
                    val updateProfileResponse = response.body()
                    Log.i("TAG", "onResponse: " + response.body())
                    updateProfileResponse?.let {
                        if (it.status == 1) {
                            binding.progressBar.visibility = View.GONE
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
                    Log.e("API", "API call failed with code ${response.code()}")
                }
            }

            override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                Log.e("API", "API call failed with exception: ${t.message}")
                Toast.makeText(
                    applicationContext,
                    t.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun handleCityListResponse(result: List<City>) {

        val adapter = ArrayAdapter(this, R.layout.simple_list_item_1, cityNames)
        binding.etCitySelect.setAdapter(adapter)

        binding.etCitySelect.setOnItemClickListener { parent, _, position, _ ->
            val selectedProductName = parent.getItemAtPosition(position) as String
            val selectedProduct = result.find { it.city_name == selectedProductName }

            selectedProduct?.let {
                selectedCityId = it.city_id
                binding.etStateSelect.text = Editable.Factory.getInstance().newEditable(it.state_name)
            }
        }
        cityNames.clear()
        cityNames.addAll(result.map { it.city_name })

        stateNames.clear()
        stateNames.addAll(result.map { it.state_name })
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
            selectedImage?.let {
                imageUri = it
                Glide.with(this)
                    .load(imageUri)
                    .apply(RequestOptions.placeholderOf(com.appointment.tutionservice.R.drawable.baseline_person_24))
                    .into(binding.imageView)

            }
        }
    }

    private fun getPath(uri: Uri): String? {
        val projection = arrayOf(MediaStore.MediaColumns.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        cursor?.let {
            val column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
            cursor.moveToFirst()
            val imagePath = cursor.getString(column_index)
            cursor.close()
            return imagePath
        }
        return null
    }

    private fun uploadProfileImage(
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

        val imageName = File(getPath(imageUri).toString()).name
        val imageFile = File(getPath(imageUri).toString())

        val requestFile: RequestBody = imageFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val multipartBody = MultipartBody.Part.createFormData("profile_image", imageName, requestFile)

        val apiService = RetrofitClient.retrofit.create(TutionServiceApi::class.java)
        binding.progressBar.visibility = View.VISIBLE

        val call = apiService.appUserProfileImageUpdate(userMobileMultiPart, apiKeyMultiPart, deviceIdMultiPart, deviceTypeMultiPart, deviceTokenMultiPart, appUserIdMultiPart, appVersionMultiPart,
            appUserKeyMultiPart, latMultiPart, lngMultiPart, multipartBody)

        call.enqueue(object : Callback<ProfileImageResponse> {
            override fun onResponse(
                call: Call<ProfileImageResponse>,
                response: Response<ProfileImageResponse>
            ) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    Log.i("TAG", "onResponse: "+ response.body())
                    apiResponse?.let {
                        if (it.status == 1) {
                            binding.progressBar.visibility = View.GONE
                            Constant.customer_ProfileImage = it.data.profile_image
                            Glide.with(applicationContext)
                                .load(it.data.profile_image)
            .apply(RequestOptions.placeholderOf(com.appointment.tutionservice.R.drawable.baseline_person_24))
                                .into(binding.imageView )
                        } else {
                            Toast.makeText(
                                this@CustomerProfileActivity,
                                "Profile pic upload failed: ${it.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    Toast.makeText(
                        this@CustomerProfileActivity,
                        "Profile pic upload failed: ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ProfileImageResponse>, t: Throwable) {
                Toast.makeText(
                    this@CustomerProfileActivity,
                    "Profile pic upload failed: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

}
