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
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.appointment.tutionservice.Constant.API_KEY
import com.appointment.tutionservice.Constant.APP_USER_ID
import com.appointment.tutionservice.Constant.APP_USER_KEY
import com.appointment.tutionservice.Constant.APP_VERSION_NAME
import com.appointment.tutionservice.Constant.MOBILE_NO
import com.appointment.tutionservice.databinding.ActivityProfileScannerBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ProfileScannerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileScannerBinding
    private val READ_EXTERNAL_STORAGE_REQUEST_CODE = 123
    private var imageUri: File? = null
    private var bankImageUri: File? = null
    private var businessImageUri: File? = null
    private var residenceImageUri: File? = null
    private var personalIdImagePath = 0
    private var bankProofIdImagePath = 0
    private var businessIdImagePath = 0
    private var residenceIdImagePath = 0

    private var personalIdType = 1
    private var bankProofIdType= 2
    private var businessIdType = 3
    private var residenceIdType = 4

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showProgressBar()
        getAppUserUploadedDocuments()

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.etScannerPersonalId.setOnClickListener {
            openGallery(personalIdType)
        }
        binding.etScannerBankPrrof.setOnClickListener {
            openGallery(bankProofIdType)
        }
        binding.etScannerBusinessPrrof.setOnClickListener {
            openGallery(businessIdType)
        }
        binding.etScannerResidencePrrof.setOnClickListener {
            openGallery(residenceIdType)
        }

        binding.btnFinish.setOnClickListener {
            providerDocumentUpload(
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
                imageUri ,
                personalIdImagePath.toString(),
                bankImageUri,
                bankProofIdImagePath.toString(),
                businessImageUri,
                businessIdImagePath.toString(),
                residenceImageUri,
                residenceIdImagePath.toString()
            )

            val logString = "${Constant.MOBILE_NO}," +
                    "${Constant.API_KEY}," +
                    "${Utility.getDeviceId(applicationContext)}," +
                    "${Utility.deviceType}," +
                    "${Utility.deviceToken}," +
                    "${Constant.APP_USER_ID}," +
                    "${Constant.APP_VERSION_NAME}," +
                    "${Constant.APP_USER_KEY}," +
                    "0.0," +
                    "0.0," +
                    "$imageUri," +
                    "${personalIdImagePath.toString()}," +
                    "$bankImageUri," +
                    "${bankProofIdImagePath.toString()}," +
                    "$businessImageUri," +
                    "${businessIdImagePath.toString()}," +
                    "$residenceImageUri," +
                    "${residenceIdImagePath.toString()}"

            Log.d("MyApp", logString)

        }

        binding.setting.setOnClickListener {
            val intent = Intent(applicationContext, SettingsActivity::class.java)
            startActivity(intent)
        }
        binding.profile.setOnClickListener {
            val intent = Intent(applicationContext, ProviderProfileActivity::class.java)
            startActivity(intent)
        }
        binding.location.setOnClickListener {
            val intent = Intent(applicationContext, ProfileCurrentLocationActivity::class.java)
            startActivity(intent)
        }
    }

    private fun openGallery(requestCode: Int) {
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
                startActivityForResult(galleryIntent, requestCode)
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
                startActivityForResult(galleryIntent, requestCode)
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
                    openGallery(requestCode)
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
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                personalIdType -> {
                    handleImageResult(data, binding.ivIdProof, personalIdType)
                }

                bankProofIdType -> {
                    handleImageResult(data, binding.ivBankIdProof, bankProofIdType)
                }

                businessIdType -> {
                    handleImageResult(data, binding.ivBusinessIdProof, businessIdType)
                }

                residenceIdType -> {
                    handleImageResult(data, binding.ivResidenceIdProof, residenceIdType)
                }

            }
        }
    }

    private fun handleImageResult(data: Intent?, imageView: ImageView, requestCode: Int) {
        val selectedImage: Uri? = data?.data
        val file = File(getPath(selectedImage).toString())
        selectedImage?.let {
            when (requestCode) {
                personalIdType -> {
                    imageUri = file
                }
                bankProofIdType -> {
                    bankImageUri = file
                }
                businessIdType -> {
                    businessImageUri = file
                }
                residenceIdType -> {
                    residenceImageUri = file
                }
            }
            imageView.visibility = View.VISIBLE
            Glide.with(this)
                .load(file)
                .into(imageView)
        }
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

    private fun providerDocumentUpload(
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
        personalIdImagePath: File?,
        personalIdImageId: String,
        bankProofIdImagePath: File?,
        bankProofImageId: String,
        businessIdImagePath: File?,
        businessIdImageId: String,
        residenceIdImagePath: File?,
        residenceImageId: String
    ) {
        val userMobileMultiPart: RequestBody =
            userMobile.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val apiKeyMultiPart: RequestBody =
            apiKey.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val deviceIdMultiPart: RequestBody =
            deviceId.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val deviceTypeMultiPart: RequestBody =
            deviceType.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val deviceTokenMultiPart: RequestBody =
            deviceToken.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val appUserIdMultiPart: RequestBody =
            appUserId.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val appVersionMultiPart: RequestBody =
            appVersion.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val appUserKeyMultiPart: RequestBody =
            appUserKey.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val latMultiPart: RequestBody = lat.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val lngMultiPart: RequestBody = lng.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val personalIdImageIdMultiPart: RequestBody =
            personalIdImageId.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val bankProofImageIdMultiPart: RequestBody =
            bankProofImageId.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val businessIdImageIdMultiPart: RequestBody =
            businessIdImageId.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val residenceImageIdMultiPart: RequestBody =
            residenceImageId.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        val requestFile = imageUri?.asRequestBody("image/*".toMediaTypeOrNull())
        val requestFileBank = bankImageUri?.asRequestBody("image/*".toMediaTypeOrNull())
        val requestFileBusiness = businessImageUri?.asRequestBody("image/*".toMediaTypeOrNull())
        val requestFileResidense = residenceImageUri?.asRequestBody("image/*".toMediaTypeOrNull())
        val personalId =
            requestFile?.let {
                MultipartBody.Part.createFormData("personal_id_image_name", imageUri?.name,
                    it
                )
            }
        val bankProof =
            requestFileBank?.let {
                MultipartBody.Part.createFormData("bank_proof_image_name", bankImageUri?.name,
                    it
                )
            }
        val business =
            requestFileBusiness?.let {
                MultipartBody.Part.createFormData("business_id_image_name", businessImageUri?.name,
                    it
                )
            }
        val residence =
            requestFileResidense?.let {
                MultipartBody.Part.createFormData("residence_image_name", residenceImageUri?.name,
                    it
                )
            }

        val apiService = RetrofitClient.retrofit.create(TutionServiceApi::class.java)
        showProgressBar()

        val call = apiService.providerDocumentUpload(
            userMobileMultiPart,
            apiKeyMultiPart,
            deviceIdMultiPart,
            deviceTypeMultiPart,
            deviceTokenMultiPart,
            appUserIdMultiPart,
            appVersionMultiPart,
            appUserKeyMultiPart,
            latMultiPart,
            lngMultiPart,
            personalId,
            personalIdImageIdMultiPart,
            bankProof,
            bankProofImageIdMultiPart,
            business,
            businessIdImageIdMultiPart,
            residence,
            residenceImageIdMultiPart
        )

        call.enqueue(object : Callback<UploadDocResponse> {
            override fun onResponse(
                call: Call<UploadDocResponse>,
                response: Response<UploadDocResponse>
            ) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    Log.i("TAG", "onResponse: " + response.body())
                    apiResponse?.let {
                        if (it.status == 1) {
                            showProgressBar()
                            Toast.makeText(
                                this@ProfileScannerActivity,
                                "Documents uploaded successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent(
                                applicationContext,
                                ProviderMainActivity::class.java
                            )
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(
                                this@ProfileScannerActivity,
                                "Documents upload failed: ${it.msg}",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.i("TAG", "onResponse: " + response.message())

                        }
                    }
                } else {
                    Toast.makeText(
                        this@ProfileScannerActivity,
                        "Document upload failed: ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.i("TAG", "onResponse: " + response.message())
                    hideProgressBar()

                }
            }

            override fun onFailure(call: Call<UploadDocResponse>, t: Throwable) {
                Toast.makeText(
                    this@ProfileScannerActivity,
                    "Document upload failed: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
                Log.i("TAG", "onResponse: " + t.message)

                hideProgressBar()

            }
        })
    }

    private fun getAppUserUploadedDocuments() {
        showProgressBar()
        val call = RetrofitClient.api.getAppUserUploadedDocuments(
            userMobile = MOBILE_NO,
            apiKey = API_KEY,
            deviceId = Utility.getDeviceId(applicationContext),
            deviceToken = Utility.deviceToken,
            appUserKey = APP_USER_KEY,
            appUserId = APP_USER_ID.toString(),
            appVersion = APP_VERSION_NAME.toString()
        )

        call.enqueue(object : Callback<DocumentResponse> {
            override fun onResponse(
                call: Call<DocumentResponse>,
                response: Response<DocumentResponse>
            ) {
                if (response.isSuccessful) {
                    if (response.body()?.status == 1) {
                        hideProgressBar()
                        val apiResponse = response.body()
                        Log.i("TAG", "onResponse: $apiResponse")
                            personalIdImagePath =
                                response.body()?.personal_id?.appUserDocumentId?.toInt()
                                    ?: 0
                            bankProofIdImagePath =
                                response.body()?.bank_proof?.appUserDocumentId?.toInt()
                                    ?: 0
                            businessIdImagePath =
                                response.body()?.business_id?.appUserDocumentId?.toInt()
                                    ?: 0
                            residenceIdImagePath =
                                response.body()?.residence?.appUserDocumentId?.toInt()
                                    ?: 0

                        personalIdType =
                            response.body()?.personal_id?.documentType?.toInt()
                                ?: 1
                        bankProofIdType =
                            response.body()?.bank_proof?.documentType?.toInt()
                                ?: 2
                        businessIdType =
                            response.body()?.business_id?.documentType?.toInt()
                                ?: 3
                        residenceIdType =
                            response.body()?.residence?.documentType?.toInt()
                                ?: 4

                        if (response.body()?.personal_id?.documentImgPath?.isNotEmpty() == true) {
                            binding.ivIdProof.visibility = View.VISIBLE
                            Glide.with(applicationContext)
                                .load(response.body()?.personal_id?.documentImgPath)
                                .into(binding.ivIdProof)
                        }
                        if (response.body()?.bank_proof?.documentImgPath?.isNotEmpty() == true) {

                            binding.ivBankIdProof.visibility = View.GONE
                            Glide.with(applicationContext)
                                .load(response.body()?.bank_proof?.documentImgPath)
                                .into(binding.ivBankIdProof)
                        }
                        if (response.body()?.business_id?.documentImgPath?.isNotEmpty() == true) {
                            binding.ivBusinessIdProof.visibility = View.VISIBLE
                            Glide.with(applicationContext)
                                .load(response.body()?.business_id?.documentImgPath)
                                .into(binding.ivBusinessIdProof)
                        }
                        if (response.body()?.residence?.documentImgPath?.isNotEmpty() == true) {
                            binding.ivResidenceIdProof.visibility = View.VISIBLE
                            Glide.with(applicationContext)
                                .load(response.body()?.residence?.documentImgPath)
                                .into(binding.ivResidenceIdProof)
                        }
                    } else {
                        hideProgressBar()
                        Log.i("TAG", "onResponse: response")
                    }
                }
            }

            override fun onFailure(call: Call<DocumentResponse>, t: Throwable) {
                hideProgressBar()
                Log.i("TAG", "onResponse: failed")

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