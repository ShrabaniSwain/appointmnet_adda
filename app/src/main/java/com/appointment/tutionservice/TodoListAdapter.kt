package com.appointment.tutionservice

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.appointment.tutionservice.databinding.ItemTodoListBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale

class TodoListAdapter(val context: Context, val todoList: List<TodoGetData>) : RecyclerView.Adapter<TodoListAdapter.CardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_todo_list, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = todoList[position]
        holder.bind(item)

    }

    override fun getItemCount(): Int {
        return todoList.size
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemTodoListBinding.bind(itemView)

        fun bind(supportDetails : TodoGetData) {
            Utility.itemBackGround(itemView)
            binding.tvPriorityRatings.text = supportDetails.status
            binding.tvTaskdetails.text = supportDetails.details
            binding.tvPending.text = supportDetails.job_status
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val date = inputFormat.parse(supportDetails.doc)
            val outputFormat = SimpleDateFormat("dd MMMM yyyy 'at' h:mm a", Locale.getDefault())
            val formattedDate = date?.let { outputFormat.format(it) }
            binding.tvDateTImeText.text = formattedDate

            val statusArray = arrayOf("Pending", "Complete")

            binding.tvPending.setOnClickListener {
                val context = binding.tvPending.context
                if (context is Activity && !context.isFinishing) {
                    val adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, statusArray)
                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("Select Status")
                    builder.setAdapter(adapter) { _, which ->
                        val selectedStatus = statusArray[which]
                        binding.tvPending.text = selectedStatus
                        val status = when (selectedStatus) {
                            "Pending" -> "0"
                            "Completed" -> "1"
                            else -> "0"
                        }

                        val jobStatusChange = TodoItem(
                            supportDetails.id, status, Constant.MOBILE_NO, Constant.API_KEY,
                            Utility.getDeviceId(context), Utility.deviceType, Utility.deviceToken,
                            Constant.APP_USER_ID.toString(), Constant.APP_VERSION_NAME.toString(),
                            Constant.APP_USER_KEY, 0.0, 0.0
                        )
                        appSaveTodoStatus(jobStatusChange)
                        Log.i("TAG", "bind: $jobStatusChange")
                    }
                    builder.show()
                }
            }


        }


        private fun appSaveTodoStatus(jobStatusChange: TodoItem) {
            val call = RetrofitClient.api.appSaveTodoStatus(jobStatusChange)
            call.enqueue(object : Callback<UpdateProfileResponse> {
                override fun onResponse(call: Call<UpdateProfileResponse>, response: Response<UpdateProfileResponse>) {
                    if (response.isSuccessful) {
                        val updateProfileResponse = response.body()
                        Log.i("TAG", "onResponse: " + response.body())
                        updateProfileResponse?.let {
                            if (it.status == 1) {
                                Toast.makeText(
                                    context,
                                    "Status changed successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Something went wrong",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        Toast.makeText(
                            context,
                            response.body()?.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e("API", "API call failed with code ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
                    Log.e("API", "API call failed with exception: ${t.message}")
                    Toast.makeText(
                        context,
                        "Something went wrong",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }

    }
}