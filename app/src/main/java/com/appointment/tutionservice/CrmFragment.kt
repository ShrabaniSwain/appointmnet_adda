package com.appointment.tutionservice

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.appointment.tutionservice.databinding.FragmentCrmBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class CrmFragment : Fragment(), CrmProviderAdapter.SelectAllListener {

    private lateinit var binding: FragmentCrmBinding
    private lateinit var crmList: List<CrmList>
    private var isSelectAllChecked = false
    private lateinit var crmAdapter: CrmProviderAdapter
    private var type = ""

    private lateinit var pushImage : ImageView
    private lateinit var imageview : CardView
    private val READ_EXTERNAL_STORAGE_REQUEST_CODE = 123
    private val FILE_REQUEST_CODE = 100
    private var imageUri: Uri? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCrmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (Utility.isInternetAvailable(requireContext())) {
            getCrmDocumentListing()
        }
        else {
            binding.tvCustomerList.visibility = View.GONE
            binding.selectAll.visibility = View.GONE

            Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show()
        }
        binding.selectAll.setOnCheckedChangeListener { _, isChecked ->
            isSelectAllChecked = isChecked
            crmAdapter.setSelectAll(isChecked)
            binding.btnNotice.visibility = if (isChecked) View.VISIBLE else View.GONE
        }
        binding.btnNotice.setOnClickListener {
            val size = crmAdapter.getSelectedUserId().size
            val pushAmount = Constant.PUSH_NOTIFICATION_AMOUNT.toFloat() * size
            val messageAmount = Constant.MESSAGE_AMOUNT.toFloat() * size

            val dialogBuilder = AlertDialog.Builder(requireContext())
            val inflater = LayoutInflater.from(requireContext()).inflate(R.layout.send_notice_dialog_box, null)
            dialogBuilder.setView(inflater)
            val close = inflater.findViewById<ImageView>(R.id.ivClose)
            val pushCard = inflater.findViewById<ConstraintLayout>(R.id.pushCard)
            val smsCard = inflater.findViewById<ConstraintLayout>(R.id.smsCard)
            val dialog = dialogBuilder.create()

            pushCard.setOnClickListener {
                if (Constant.WALLET_AMOUNT.isEmpty() || Constant.WALLET_AMOUNT == "0" || Constant.WALLET_AMOUNT.toInt() < pushAmount){
                    Toast.makeText(context, "Your wallet does not have enough money. Please recharge your wallet to send the message.", Toast.LENGTH_SHORT).show()
                }
                else {
                    type = "1"
                    showMessageDialog()
                    dialog.dismiss()
                }
            }

            smsCard.setOnClickListener {
                if (Constant.WALLET_AMOUNT.isEmpty() || Constant.WALLET_AMOUNT == "0" || Constant.WALLET_AMOUNT.toInt() < messageAmount){
                    Toast.makeText(context, "Your wallet does not have enough money. Please recharge your wallet to send the message.", Toast.LENGTH_SHORT).show()
                }
                else {
                    type = "2"
                    showMessageDialog()
                    dialog.dismiss()
                }
            }

            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            close.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }

        binding.btnAddNow.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(requireContext())
            val inflater = LayoutInflater.from(requireContext()).inflate(R.layout.add_new_dialog_box, null)
            dialogBuilder.setView(inflater)
            val close = inflater.findViewById<ImageView>(R.id.ivClose)
            val whatsappCard = inflater.findViewById<ConstraintLayout>(R.id.whatsappCard)
            val smsCard = inflater.findViewById<ConstraintLayout>(R.id.smsCard)
            val dialog = dialogBuilder.create()
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            close.setOnClickListener {
                dialog.dismiss()
            }

            whatsappCard.setOnClickListener {
                val intent = Intent(requireContext(), AddCustomerActivity::class.java)
                startActivity(intent)
            }

            smsCard.setOnClickListener {
                val intent = Intent(requireContext(), ImportRecordActivity::class.java)
                startActivity(intent)
            }

            dialog.show()
        }
    }

    private fun getCrmDocumentListing() {
        showProgressBar()
        val call = RetrofitClient.api.getCrmDocumentListing(
            Constant.MOBILE_NO,
            Constant.API_KEY,
            Utility.getDeviceId(requireContext()),
            Utility.deviceToken,
            Constant.APP_USER_KEY,
            Constant.APP_USER_ID.toString(),
            Constant.APP_VERSION_NAME.toString()
        )

        call.enqueue(object : Callback<CrmListResponse> {
            override fun onResponse(call: Call<CrmListResponse>, response: Response<CrmListResponse>) {
                if (response.isSuccessful) {
                    if (response.body()?.status == 1) {
                        if (isAdded) {
                            hideProgressBar()
                            crmList = response.body()?.data ?: emptyList()

                            if (crmList.isEmpty()) {
                                binding.tvNoData.visibility = View.VISIBLE
                                binding.tvCustomerList.visibility = View.GONE
                                binding.selectAll.visibility = View.GONE

                            } else {
                                binding.tvCustomerList.visibility = View.VISIBLE
                                binding.selectAll.visibility = View.VISIBLE
                                binding.tvNoData.visibility = View.GONE
                                crmAdapter =
                                    CrmProviderAdapter(crmList, this@CrmFragment, requireContext())
                                binding.rvCrm.layoutManager = LinearLayoutManager(
                                    requireContext(),
                                    LinearLayoutManager.VERTICAL,
                                    false
                                )
                                binding.rvCrm.adapter = crmAdapter

                            }
                        }
                        Log.i("TAG", "bannerImage: ${response.body()}")
                    }
                } else {
                    if (isAdded) {
                        Toast.makeText(requireContext(), response.message(), Toast.LENGTH_SHORT)
                            .show()
                    }
                    Log.i("TAG", "bannerImage: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<CrmListResponse>, t: Throwable) {
                Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
                Log.i("TAG", "bannerImage: ${t.message}")

            }
        })
    }

    private fun showProgressBar() {
        if (isAdded) {
            binding.progressBar.visibility = View.VISIBLE
            requireActivity().window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
        }
    }

    private fun hideProgressBar() {
        if (isAdded) {
            binding.progressBar.visibility = View.GONE
            requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }

    override fun onSelectAllChanged(isChecked: Boolean) {
        binding.btnNotice.visibility = if (isChecked) View.VISIBLE else View.GONE
    }

    private fun showMessageDialog() {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        val inflater = LayoutInflater.from(requireContext()).inflate(R.layout.message_dialog_box, null)
        dialogBuilder.setView(inflater)
        val messageTextView = inflater.findViewById<EditText>(R.id.etRequirements)
        val okButton = inflater.findViewById<TextView>(R.id.btnSend)
        val ivClose = inflater.findViewById<ImageView>(R.id.ivClose)
        val tvCharges = inflater.findViewById<TextView>(R.id.tvCharges)
        pushImage = inflater.findViewById(R.id.ivPhotos)
        imageview = inflater.findViewById(R.id.imageview)
        val ivCameraClick = inflater.findViewById<ImageView>(R.id.ivCameraClick)

        ivCameraClick.setOnClickListener {
            openGallery()
        }

        if (type == "2"){
            tvCharges.text = "Whatsapp broadcast ₹${Constant.MESSAGE_AMOUNT} paisa per message"
        }
        else if (type == "1"){
            tvCharges.text = "Push message ₹${Constant.PUSH_NOTIFICATION_AMOUNT} paisa per message"
        }
        val dialog = dialogBuilder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        ivClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

        okButton.setOnClickListener {
            val selectedPhoneNumbers = crmAdapter.getSelectedPhoneNumbers()
            val commaSeparatedNumbers = selectedPhoneNumbers.joinToString(separator = ",")

            val selectedUserId = crmAdapter.getSelectedUserId()
            val commaSeparatedIds = selectedUserId.joinToString(separator = ",")
            if (selectedPhoneNumbers.isNotEmpty()) {
                val sendMessage = SmsRequest(messageTextView.text.toString(),Constant.Provider_Name,Constant.MOBILE_NO,commaSeparatedNumbers,commaSeparatedIds,type,Constant.API_KEY,Utility.getDeviceId(requireContext()), Utility.deviceType, Utility.deviceToken,
                    Constant.APP_USER_ID.toString(), Constant.APP_VERSION_NAME.toString(), Constant.APP_USER_KEY, 0.0, 0.0)

//                val isProfilePhotoUpdated = ::imageUri.isInitialized
//
//                if (isProfilePhotoUpdated) {
                    val message = messageTextView.text.toString().trim()

                    if (message.isEmpty()) {
                        Toast.makeText(
                            requireContext(),
                            "Message cannot be empty",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (imageUri == null) {
                        Toast.makeText(
                            requireContext(),
                            "Please select an image",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    else{
                    imageUri.let {
                        val imagePath = getPath(it)

                        sendSmsToUser(
                            message,
                            Constant.Provider_Name,
                            Constant.MOBILE_NO,
                            commaSeparatedNumbers,
                            commaSeparatedIds,
                            type,
                            Constant.API_KEY,
                            Utility.getDeviceId(requireContext()),
                            Utility.deviceType,
                            Utility.deviceToken,
                            Constant.APP_USER_ID.toString(),
                            Constant.APP_VERSION_NAME.toString(),
                            Constant.APP_USER_KEY,
                            "0.0",
                            "0.0", imagePath ?: ""
                        )
                    }

                        Log.i("TAG", "showMessageDialog: " + sendMessage)
                        dialog.dismiss()
                }
//                }

            } else {
                Toast.makeText(requireContext(), "No items selected", Toast.LENGTH_SHORT).show()
            }


        }

    }

    private fun openGallery() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Request the permission
                ActivityCompat.requestPermissions(
                    requireContext() as Activity,
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
                    requireContext(),
                    Manifest.permission.READ_MEDIA_IMAGES
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireContext() as Activity,
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

    @Deprecated("Deprecated in Java")
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
                        requireContext(),
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
                imageview.visibility = View.VISIBLE
                Glide.with(this)
                    .load(imageUri)
                    .apply(RequestOptions.placeholderOf(R.drawable.baseline_person_24))
                    .into(pushImage)

            }
        }
    }

    private fun getPath(uri: Uri?): String? {
        val projection = arrayOf(MediaStore.MediaColumns.DATA)
        val cursor = uri?.let { requireContext().contentResolver.query(it, projection, null, null, null) }
        cursor?.let {
            val column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
            cursor.moveToFirst()
            val imagePath = cursor.getString(column_index)
            cursor.close()
            return imagePath
        }
        return null
    }

    private fun sendSmsToUser(message: String, userName: String,
                              userMobile: String,
                              sendSmsMobile: String,
                              userId: String,
                              type: String,
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
        val messageMultiPart: RequestBody = message.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val userNameMultiPart: RequestBody = userName.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val userMobileMultiPart: RequestBody = userMobile.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val sendSmsMobileMultiPart: RequestBody = sendSmsMobile.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val userIdMultiPart: RequestBody = userId.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val typeMultiPart: RequestBody = type.toRequestBody("multipart/form-data".toMediaTypeOrNull())
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
        val multipartBody = MultipartBody.Part.createFormData("push_image", imageName, requestFile)

        val call = RetrofitClient.api.sendSmsToUser(messageMultiPart, userNameMultiPart, userMobileMultiPart, sendSmsMobileMultiPart, userIdMultiPart, typeMultiPart, apiKeyMultiPart, deviceIdMultiPart,
            deviceTypeMultiPart, deviceTokenMultiPart, appUserIdMultiPart, appVersionMultiPart, appUserKeyMultiPart, latMultiPart, lngMultiPart, multipartBody)

        call.enqueue(object : Callback<MessageAPiResponse> {
            override fun onResponse(call: Call<MessageAPiResponse>, response: Response<MessageAPiResponse>) {
                if (response.isSuccessful) {
                    val updateProfileResponse = response.body()
                    Log.i("TAG", "onResponse: $updateProfileResponse")
                    Toast.makeText(context, "Message send successfully.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                    Log.i("TAG", "appUserServicesUpdate onResponse error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<MessageAPiResponse>, t: Throwable) {
                Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
                Log.i("TAG", "appUserServicesUpdate onFailure: ${t.message}")
            }
        })
    }

    private fun sendSmsToSelectedItems() {
        val selectedPhoneNumbers = crmAdapter.getSelectedPhoneNumbers()
        if (selectedPhoneNumbers.isNotEmpty()) {
            val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("smsto:" + selectedPhoneNumbers.joinToString(";"))
                putExtra("sms_body", "")
            }
            startActivity(smsIntent)
        } else {
            Toast.makeText(requireContext(), "No items selected", Toast.LENGTH_SHORT).show()
        }
    }
}