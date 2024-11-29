package com.appointment.tutionservice

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.appointment.tutionservice.databinding.ActivitySelectquestionThreeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SelectquestionThree : AppCompatActivity() {

    private lateinit var binding: ActivitySelectquestionThreeBinding
    private val calendar: Calendar = Calendar.getInstance()
    private lateinit var jobPost: JobPost
    private var location = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectquestionThreeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updateDateField()
        updateTimeField()

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.tvLocationType.setOnClickListener {
            showLocationDialog()
        }
        binding.tvCalender.setOnClickListener {
            openDatePickerDialog()
        }

        binding.tvTime.setOnClickListener {
            openTimePickerDialog()
        }

        binding.btnPayBook.setOnClickListener {
//            if (Constant.BUDGET.isEmpty()){
//                Toast.makeText(
//                    this,
//                    "Please select the price",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//            else {
            if ( binding.etRequirements.text.toString().isEmpty()){
            Toast.makeText(
                this,
                "Please add your requirements",
                Toast.LENGTH_SHORT
            ).show()
        }
            else if(location.isEmpty()){
                Toast.makeText(
                    this,
                    "Please select the preferred location",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else{
                jobPost = JobPost(
                    Constant.JOBTITLE,binding.tvCalender.text.toString(),binding.tvTime.text.toString(),
                    binding.etRequirements.text.toString(),
                    Constant.BUDGET,
                    "0",
                    Constant.APP_ADDRESS_ID,
                    Constant.SERVICE_ID,
                    "0",
                    "1",
                    "1",
                    true,
                    Constant.questionnaireList,
                    Constant.MOBILE_NO,
                    Constant.API_KEY,
                    Utility.getDeviceId(applicationContext),
                    Utility.deviceType,
                    Utility.deviceToken,
                    Constant.APP_USER_ID.toString(),
                    Constant.APP_VERSION_NAME.toString(),
                    Constant.APP_USER_KEY,"",
                    0.0,
                    0.0,
                    Constant.SERVICE_PROVIDER_ID.toInt(),
                    location
                )

                customerJobPost(jobPost)
                Log.i("TAG", "onCreate: " + jobPost)
            }

        }

        val chooseFeeAdapter = ChoosefeeAdapter()
        binding.rvChooseFee.adapter = chooseFeeAdapter
        binding.rvChooseFee.layoutManager = GridLayoutManager(applicationContext, 3, GridLayoutManager.VERTICAL, false)
    }

    private fun showLocationDialog() {
        val locationOptions = arrayOf("My City", "My State", "All Over Country")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Location")
        builder.setItems(locationOptions) { _, which ->
            val selectedLocation = locationOptions[which]
            binding.tvLocationType.text = selectedLocation

            location = when (selectedLocation) {
                "My City" -> "1"
                "My State" -> "2"
                "All Over Country" -> "3"
                else -> "1"
            }
        }

        builder.show()
    }
    private fun updateDateField() {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        binding.tvCalender.text = sdf.format(calendar.time)
    }

    private fun updateTimeField() {
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        binding.tvTime.text = sdf.format(calendar.time)
    }

    private fun openDatePickerDialog() {
        val datePickerDialog = DatePickerDialog(
            this,R.style.DialogTheme,
            { _, year, monthOfYear, dayOfMonth ->
                calendar.set(year, monthOfYear, dayOfMonth)
                updateDateField()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun openTimePickerDialog() {
        val timePickerDialog = TimePickerDialog(
            this, R.style.DialogTheme,
            { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                updateTimeField()
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        )
        timePickerDialog.show()
    }

    private fun customerJobPost(job: JobPost) {
        showProgressBar()
        val call = RetrofitClient.api.customerJobPost(job)
        call.enqueue(object : Callback<JobPostResponse> {
            override fun onResponse(call: Call<JobPostResponse>, response: Response<JobPostResponse>) {
                hideProgressBar()
                if (response.isSuccessful) {
                    if (response.body()?.status == 1) {
                        val updateProfileResponse = response.body()
                        Log.i("TAG", "onResponse: $updateProfileResponse")
                        val transaction = supportFragmentManager.beginTransaction()
                        transaction.replace(R.id.request, CustomerRequestsFragment())
                        supportFragmentManager.popBackStack()
                        transaction.commit()
                        Toast.makeText(applicationContext, updateProfileResponse?.msg, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(applicationContext, response.message(), Toast.LENGTH_SHORT).show()
                    Log.i("TAG", "appUserServicesUpdate onResponse error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<JobPostResponse>, t: Throwable) {
                hideProgressBar()
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
                Log.i("TAG", "appUserServicesUpdate onFailure: ${t.message}")
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