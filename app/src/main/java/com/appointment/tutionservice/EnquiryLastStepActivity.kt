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
import com.appointment.tutionservice.Constant.API_KEY
import com.appointment.tutionservice.Constant.APP_ADDRESS_ID
import com.appointment.tutionservice.Constant.APP_USER_ID
import com.appointment.tutionservice.Constant.APP_USER_KEY
import com.appointment.tutionservice.Constant.APP_VERSION_NAME
import com.appointment.tutionservice.Constant.JOBTITLE
import com.appointment.tutionservice.Constant.MOBILE_NO
import com.appointment.tutionservice.Constant.SERVICE_ID
import com.appointment.tutionservice.Constant.questionnaireList
import com.appointment.tutionservice.databinding.ActivityEnquiryLastStepBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EnquiryLastStepActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEnquiryLastStepBinding
    private var priority = ""
    private var location = ""
    private val calendar: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEnquiryLastStepBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updateDateField()
        updateTimeField()

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.tvCalender.setOnClickListener {
            openDatePickerDialog()
        }

        binding.tvTime.setOnClickListener {
            openTimePickerDialog()
        }

        binding.tvSelectType.setOnClickListener {
            showGenderOptionsDialog()
        }

        binding.tvLocationType.setOnClickListener {
            showLocationDialog()
        }
        binding.btnSendEnquiry.setOnClickListener {
       if(priority.isEmpty()){
                Toast.makeText(
                    this,
                    "Please select the job priority",
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
            else if ( binding.etRequirements.text.toString().isEmpty()){
           Toast.makeText(
               this,
               "Please add your requirements",
               Toast.LENGTH_SHORT
           ).show()
            }
            else {
                val jobPost = JobPost(
                    JOBTITLE,binding.tvCalender.text.toString(),binding.tvTime.text.toString(),
                    binding.etRequirements.text.toString(),
                    Constant.BUDGET,
                    "0",
                    APP_ADDRESS_ID,
                    SERVICE_ID,
                    "0",
                    "1",
                    priority,
                    true,
                    questionnaireList,
                    MOBILE_NO,
                    API_KEY,
                    Utility.getDeviceId(applicationContext),
                    Utility.deviceType,
                    Utility.deviceToken,
                    APP_USER_ID.toString(),
                    APP_VERSION_NAME.toString(),
                    APP_USER_KEY, "",
                    0.0,
                    0.0,
                    0,
                    location
                )

                customerJobPost(jobPost)

                Log.i("TAG", "onCreate: $jobPost")
            }

        }
        val chooseFeeAdapter = ChoosefeeAdapter()
        binding.rvChooseFee.adapter = chooseFeeAdapter
        binding.rvChooseFee.layoutManager = GridLayoutManager(applicationContext, 3, GridLayoutManager.VERTICAL, false)
    }

    private fun showGenderOptionsDialog() {
        val genderOptions = arrayOf("Immediate", "Standard")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Priority")
        builder.setItems(genderOptions) { _, which ->
            val selectedGender = genderOptions[which]
            binding.tvSelectType.text = selectedGender
            priority = if (binding.tvSelectType.text.toString() == "Immediate"){
                "1"
            } else{
                "2"
            }
        }

        val dialog = builder.create()
        dialog.show()
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
                        transaction.replace(R.id.appointment, CustomerRequestsFragment())
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