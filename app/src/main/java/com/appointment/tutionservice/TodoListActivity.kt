package com.appointment.tutionservice

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.appointment.tutionservice.databinding.ActivityTodoListBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TodoListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTodoListBinding
    private val calendar: Calendar = Calendar.getInstance()
    private lateinit var todoList: List<TodoGetData>

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTodoListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getAllNotifications()

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnAddNow.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(this)
            val inflater = LayoutInflater.from(this).inflate(R.layout.todo_list_dialogbox, null)
            dialogBuilder.setView(inflater)
            val close = inflater.findViewById<ImageView>(R.id.ivClose)
            val date = inflater.findViewById<TextView>(R.id.tvCalender)
            val priority = inflater.findViewById<TextView>(R.id.tvPrority)
            val save = inflater.findViewById<TextView>(R.id.btnSendEnquiry)
            val details = inflater.findViewById<EditText>(R.id.etTaskDeatils)

            updateDateField(date)
            val dialog = dialogBuilder.create()
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            close.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()

            date.setOnClickListener {
                openDatePickerDialog(date)
            }


            priority.setOnClickListener { view ->
                val popupMenu = PopupMenu(this, view)
                popupMenu.menuInflater.inflate(R.menu.popup_priority, popupMenu.menu)

                popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
                    when (menuItem.itemId) {
                        R.id.menu_high -> {
                            priority.text = "High"
                            true
                        }
                        R.id.menu_medium -> {
                            priority.text = "Medium"
                            true
                        }
                        R.id.menu_low -> {
                            priority.text = "Low"
                            true
                        }
                        else -> false
                    }
                }

                popupMenu.show()
            }
            save.setOnClickListener {
                if (details.text.toString().isBlank()){
                    Toast.makeText(
                        this,
                        "Please fill in all fields",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else {
                    val status = when (priority.text.toString()) {
                        "High" -> "2"
                        "Medium" -> "3"
                        "Low" -> "4"
                        else -> "2"
                    }
                    val selectedDate = date.text.toString().trim()
                    val inputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

                    val date1= inputFormat.parse(selectedDate)
                    val formattedDate = date1?.let { outputFormat.format(it) }
                    val todoListSendData = TodoListSendData(
                        "0",
                        Constant.Provider_Name,
                        details.text.toString(),
                        formattedDate.toString(),
                        status,"0",
                        Constant.MOBILE_NO,
                        Constant.API_KEY,
                        Utility.getDeviceId(applicationContext),
                        Utility.deviceType,
                        Utility.deviceToken,
                        Constant.APP_USER_ID.toString(),
                        Constant.APP_VERSION_NAME.toString(),
                        Constant.APP_USER_KEY,
                        0.0,
                        0.0
                    )
                    Log.i("TAG", "onCreate: " + todoListSendData + formattedDate + " " )
                    dialog.dismiss()
                    appSaveTodoData(todoListSendData)
                }
            }

        }

    }


    private fun openDatePickerDialog(view: TextView) {
        val datePickerDialog = DatePickerDialog(
            this,R.style.DialogTheme,
            { _, year, monthOfYear, dayOfMonth ->
                calendar.set(year, monthOfYear, dayOfMonth)
                updateDateField(view)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun updateDateField(view: TextView) {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        view.text = sdf.format(calendar.time)
    }

    private fun appSaveTodoData(todoListSendData: TodoListSendData) {
        showProgressBar()
        val call = RetrofitClient.api.appSaveTodoData(todoListSendData)
        call.enqueue(object : Callback<TodoResponseData> {
            override fun onResponse(call: Call<TodoResponseData>, response: Response<TodoResponseData>) {
                hideProgressBar()
                if (response.isSuccessful) {
                        val updateProfileResponse = response.body()
                        Log.i("TAG", "onResponse: $updateProfileResponse")
                        val intent = Intent(applicationContext, TodoListActivity::class.java)
                        startActivity(intent)
                        finish()
                        Toast.makeText(applicationContext, "Details added successfully.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(applicationContext, response.message(), Toast.LENGTH_SHORT).show()
                    Log.i("TAG", "appUserServicesUpdate onResponse error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<TodoResponseData>, t: Throwable) {
                hideProgressBar()
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
                Log.i("TAG", "appUserServicesUpdate onFailure: ${t.message}")
            }
        })
    }

    private fun getAllNotifications() {
        showProgressBar()
        val call = RetrofitClient.api.getTodoListing(
            Constant.MOBILE_NO,
            Constant.API_KEY,
            Utility.getDeviceId(applicationContext),
            Utility.deviceToken,
            Constant.APP_USER_KEY,
            Constant.APP_USER_ID.toString(),
            Constant.APP_VERSION_NAME.toString()
        )

        call.enqueue(object : Callback<TodoGetResponse> {
            override fun onResponse(call: Call<TodoGetResponse>, response: Response<TodoGetResponse>) {
                if (response.isSuccessful) {
                    hideProgressBar()
                    todoList = response.body()?.data ?: emptyList()

                    val todoListAdapter = TodoListAdapter(applicationContext, todoList)
                    binding.rvTodo.layoutManager =
                        LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
                    binding.rvTodo.adapter = todoListAdapter

                    Log.i("TAG", "bannerImage: ${response.body()}")
                } else {
                    hideProgressBar()
                    val errorBody = response.errorBody()
                    Toast.makeText(applicationContext, response.message(), Toast.LENGTH_SHORT).show()
                    Log.i("TAG", "bannerImage: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<TodoGetResponse>, t: Throwable) {
                hideProgressBar()
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
                Log.i("TAG", "bannerImage: ${t.message}")

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