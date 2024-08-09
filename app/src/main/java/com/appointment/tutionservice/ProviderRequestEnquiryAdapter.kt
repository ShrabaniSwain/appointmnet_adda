package com.appointment.tutionservice

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.appointment.tutionservice.databinding.RequestEnquiryItemListBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale


class ProviderRequestEnquiryAdapter(val context: Context, val enquiry: List<CustomerEnquiry>) : RecyclerView.Adapter<ProviderRequestEnquiryAdapter.CardViewHolder>() {

    private var filteredMarketItems: List<CustomerEnquiry> = enquiry

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.request_enquiry_item_list, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = filteredMarketItems[position]
        holder.bind(item)

    }

    override fun getItemCount(): Int {
        return filteredMarketItems.size
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = RequestEnquiryItemListBinding.bind(itemView)

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(supportDetails : CustomerEnquiry) {
            Utility.itemBackGround(itemView)
            binding.tvTitle.text = supportDetails.job_title
            binding.tvCategory.text = supportDetails.service_name
            binding.tvName.visibility = View.VISIBLE
            binding.tvName.text = supportDetails.user_profile_name
            binding.tvCategoryDeatils.text = supportDetails.job_description
            val priority = if (supportDetails.job_priority_id == "1"){
                "Immediate"
            } else{
                "Standard"
            }
            binding.tvPriorityRatings.text = priority
            binding.rvServiceList.layoutManager = LinearLayoutManager(context)
            val adapter = QuestionAdapter(supportDetails.get_ques_ans)
            binding.rvServiceList.adapter = adapter

            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val date = inputFormat.parse(supportDetails.doc)
            val outputFormat = SimpleDateFormat("dd MMMM yyyy 'at' h:mm a", Locale.getDefault())
            val formattedDate = date?.let { outputFormat.format(it) }
            binding.tvDate.text = formattedDate
            val formattedTime = convertTimeTo12HourFormat(supportDetails.job_time)
            binding.tvFixedDay.text = "Required Date: " + supportDetails.job_date + " at " + formattedTime
            Glide.with(context).load(supportDetails.service_image ?: "").into(binding.ivImage)

            binding.btnEnquiry.visibility = View.VISIBLE

            binding.rvServiceList.layoutManager = LinearLayoutManager(context)
            val questionAdapter = QuestionAdapter(supportDetails.get_ques_ans)
            binding.rvServiceList.adapter = questionAdapter

            binding.btnEnquiry.setOnClickListener {
                Constant.CUSTOMER_ID = supportDetails.service_provider_id
                Constant.JOB_POST_ID = supportDetails.job_post_id

                val dialogBuilder = AlertDialog.Builder(context)
                val inflater = LayoutInflater.from(context).inflate(R.layout.bids_dialog_box, null)
                dialogBuilder.setView(inflater)
                val close = inflater.findViewById<ImageView>(R.id.ivClose)
                val fields = inflater.findViewById<EditText>(R.id.tvFields)
                val amount = inflater.findViewById<EditText>(R.id.tvAmount)
                val etRequirements = inflater.findViewById<EditText>(R.id.etRequirements)
                val save = inflater.findViewById<TextView>(R.id.btnSendEnquiry)

                val recyclerView = inflater.findViewById<RecyclerView>(R.id.rvService)

                recyclerView.layoutManager = LinearLayoutManager(context)
                val adapter = JobBitsAdapter(supportDetails.job_bits)
                recyclerView.adapter = adapter

                val dialog = dialogBuilder.create()
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                close.setOnClickListener {
                    dialog.dismiss()
                }
                dialog.show()
                save.setOnClickListener {
                    val bidData = BidData(
                        supportDetails.job_post_id,
                        fields.text.toString(),
                        amount.text.toString(),
                        etRequirements.text.toString(),
                        "2",
                        Constant.MOBILE_NO,
                        Constant.API_KEY,
                        Utility.getDeviceId(context),
                        Utility.deviceType,
                        Utility.deviceToken,
                        Constant.APP_USER_ID.toString(),
                        Constant.APP_VERSION_NAME.toString(),
                        Constant.APP_USER_KEY,
                        0.0,
                        0.0
                    )
                    Log.i("TAG", "bind: " + bidData)
                    if (amount.text.toString().isBlank() || etRequirements.text.toString().isBlank()){
                        Toast.makeText(
                            context,
                            "Please fill in all fields",
                            Toast.LENGTH_SHORT
                        ).show()
                    }else {
                        jobBidByServiceProvider(bidData)
                    }
                }
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun convertTimeTo12HourFormat(time: String?): String {
            return if (time == null) {
                "Time not provided" // Handle null time value
            } else {
                try {
                    val inputFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
                    val outputFormatter = DateTimeFormatter.ofPattern("hh:mm a")
                    val parsedTime = LocalTime.parse(time, inputFormatter)
                    parsedTime.format(outputFormatter)
                } catch (e: DateTimeParseException) {
                    "Invalid time format" // Handle invalid time format
                }
            }
        }


        private fun jobBidByServiceProvider(bidData: BidData) {
            val call = RetrofitClient.api.jobBidByServiceProvider(bidData)
            call.enqueue(object : Callback<UpdateProfileResponse> {
                override fun onResponse(call: Call<UpdateProfileResponse>, response: Response<UpdateProfileResponse>) {
                    if (response.isSuccessful) {
                        val updateProfileResponse = response.body()
                        Log.i("TAG", "onResponse: $updateProfileResponse")
                        val intent = Intent(context, ProviderMainActivity::class.java)
                        itemView.context.startActivity(intent)
                        Toast.makeText(context, "Details added successfully.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                        Log.i("TAG", "appUserServicesUpdate onResponse error: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
                    Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
                    Log.i("TAG", "appUserServicesUpdate onFailure: ${t.message}")
                }
            })
        }

    }



    fun updateListEnquiry(newList: List<CustomerEnquiry>) {
        filteredMarketItems = newList
        notifyDataSetChanged()
    }
}