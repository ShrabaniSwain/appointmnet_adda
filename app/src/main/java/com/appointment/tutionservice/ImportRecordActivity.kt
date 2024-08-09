package com.appointment.tutionservice

import android.Manifest
import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.appointment.tutionservice.databinding.ActivityImportRecordBinding
import java.io.File
import java.io.FileOutputStream

class ImportRecordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityImportRecordBinding
    private val READ_EXTERNAL_STORAGE_REQUEST_CODE = 123
    private val PICK_EXCEL_FILE_REQUEST_CODE = 124
    private var selectedFilePath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImportRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.etNameSelect.setOnClickListener {
            openFilePicker()
        }
        binding.tvDownload.setOnClickListener {
            val fileUrl = "https://appointmentadda.com/uploads/sample_mobile.csv"
            val fileName = "sample_mobile.csv"
            downloadFile(this, fileUrl, fileName)
        }

    }

    private fun openFilePicker() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    READ_EXTERNAL_STORAGE_REQUEST_CODE
                )
            } else {
                pickExcelFile()
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
                pickExcelFile()
            }
        }
    }

    private fun pickExcelFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "application/vnd.ms-excel"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(Intent.createChooser(intent, "Select Excel File"), PICK_EXCEL_FILE_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == READ_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickExcelFile()
            } else {
                Toast.makeText(
                    this,
                    "Permission denied. To use this feature, please grant the permission in App Settings.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PICK_EXCEL_FILE_REQUEST_CODE -> {
                    data?.data?.let { uri ->
                        selectedFilePath = getFilePath(uri)
                        selectedFilePath?.let { path ->
                            val fileName = File(path).name
                            binding.etNameSelect.setText(fileName)

                            val intent = Intent(this, ImportSuccessfullyActivity::class.java)
                            intent.putExtra("fileName", fileName)
                            intent.putExtra("filePath", path)
                            startActivity(intent)
                        }
                    }
                }
            }
        }
    }

    private fun getFilePath(uri: Uri): String? {
        // Try to get the file path from the Uri
        return uriToFilePath(uri)
    }

    private fun uriToFilePath(uri: Uri): String? {
        // Check if the Uri scheme is content
        if (uri.scheme == "content") {
            try {
                // Use the ContentResolver to get the file path
                contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
                        val fileName = cursor.getString(columnIndex)
                        val file = File(cacheDir, fileName)
                        contentResolver.openInputStream(uri)?.use { inputStream ->
                            FileOutputStream(file).use { outputStream ->
                                inputStream.copyTo(outputStream)
                            }
                        }
                        return file.absolutePath
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else if (uri.scheme == "file") {
            // For file Uris, simply return the file path
            return uri.path
        }
        return null
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

