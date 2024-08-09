package com.appointment.tutionservice

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.appointment.tutionservice.databinding.ActivityImportSuccessfullyBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class ImportSuccessfullyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityImportSuccessfullyBinding
    private var selectedFilePath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImportSuccessfullyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        selectedFilePath = intent.getStringExtra("filePath")
        val fileName = intent.getStringExtra("fileName")
        binding.fileName.text = fileName

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.cancel.setOnClickListener {
            binding.fileName.text = ""
        }

        binding.tvDownload.setOnClickListener {
            val fileUrl = "https://appointmentadda.com/uploads/sample_mobile.csv"
            val fileName = "sample_mobile.csv"
            downloadFile(this, fileUrl, fileName)
        }

        binding.btnSendNotice.setOnClickListener {
            selectedFilePath?.let { path ->
                crmFileImport(File(path), Constant.API_KEY, Constant.MOBILE_NO, Utility.getDeviceId(applicationContext), Utility.deviceToken, Constant.APP_USER_ID.toString(), "2", Utility.deviceType, Constant.APP_USER_KEY,
                    Constant.APP_VERSION_NAME.toString(),"0.0","0.0")
            }
        }
    }

    private fun crmFileImport(
        file: File?,
        apiKey: String,
        userMobile: String,
        deviceId: String,
        deviceToken: String,
        appUserId: String,
        appUserType: String,
        deviceType: String,
        appUserKey: String,
        appVersion: String,
        lat: String,
        lng: String,
    ) {
        val requestFile = file?.asRequestBody("text/csv".toMediaTypeOrNull())

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
        val appUserTypeMultiPart: RequestBody =
            appUserType.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val latMultiPart: RequestBody = lat.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val lngMultiPart: RequestBody = lng.toRequestBody("multipart/form-data".toMediaTypeOrNull())


        val excelFile = requestFile?.let {
            MultipartBody.Part.createFormData("file", file?.name,
                it
            )
        }

        val apiService = RetrofitClient.retrofit.create(TutionServiceApi::class.java)
        showProgressBar()

        val call = apiService.crmFileImport(
            excelFile,
            apiKeyMultiPart,
            userMobileMultiPart,
            deviceIdMultiPart,
            deviceTokenMultiPart,
            appUserIdMultiPart,
            appUserTypeMultiPart,
            deviceTypeMultiPart,
            appUserKeyMultiPart,
            appVersionMultiPart,
            latMultiPart,
            lngMultiPart,
        )

        call.enqueue(object : Callback<UpdateProfileResponse> {
            override fun onResponse(
                call: Call<UpdateProfileResponse>,
                response: Response<UpdateProfileResponse>
            ) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    Log.i("TAG", "onResponse: " + response.body())
                    apiResponse?.let {
                        if (it.status == 1) {
                            showProgressBar()
                            Toast.makeText(
                                this@ImportSuccessfullyActivity,
                                "Imported successfully",
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
                                this@ImportSuccessfullyActivity,
                                "Documents upload failed: ${it.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.i("TAG", "onResponse: " + response.message())

                        }
                    }
                } else {
                    Toast.makeText(
                        this@ImportSuccessfullyActivity,
                        "Document upload failed: ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.i("TAG", "onResponse: " + response.message())
                    hideProgressBar()

                }
            }

            override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
                Toast.makeText(
                    this@ImportSuccessfullyActivity,
                    "Document upload failed: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
                Log.i("TAG", "onResponse: " + t.message)

                hideProgressBar()

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

    private fun downloadFile(context: Context, url: String, fileName: String) {
        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle(fileName)
            .setDescription("Downloading $fileName")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request) // Start the download

        Toast.makeText(context, "Download started", Toast.LENGTH_SHORT).show()
    }
}
