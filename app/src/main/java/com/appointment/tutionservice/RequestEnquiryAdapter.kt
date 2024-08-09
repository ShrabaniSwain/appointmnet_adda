package com.appointment.tutionservice

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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


class RequestEnquiryAdapter(val context: Context, val enquiry: List<CustomerEnquiry>) : RecyclerView.Adapter<RequestEnquiryAdapter.CardViewHolder>() {

    private var filteredMarketItems: List<CustomerEnquiry> = enquiry
    private lateinit var bids: List<JobPostLog>
    private lateinit var customerEnquiryDialogAdapter: CustomerEnquiryDialogAdapter
    private lateinit var rvDialogList: RecyclerView
    private lateinit var noData: TextView

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
            binding.tvCategoryDeatils.text = supportDetails.job_description
            val priority = if (supportDetails.job_priority_id == "1"){
                "Immediate"
            } else{
                "Standard"
            }
            binding.tvPriorityRatings.text = priority
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
            val adapter = QuestionAdapter(supportDetails.get_ques_ans)
            binding.rvServiceList.adapter = adapter

            binding.btnEnquiry.setOnClickListener {
                Constant.CUSTOMER_ID = supportDetails.service_provider_id
                Constant.JOB_POST_ID = supportDetails.job_post_id
                Constant.STATUS = supportDetails.job_post_status

                val jobPostRequest = JobPostRequest(supportDetails.job_post_id, Constant.current_app_user_type, Constant.MOBILE_NO, Constant.API_KEY, Utility.getDeviceId(context), Utility.deviceType,
                    Utility.deviceToken, Constant.APP_USER_ID.toString(), Constant.APP_VERSION_NAME.toString(), Constant.APP_USER_KEY, 0.0,0.0)
                getJobBidPersonByServiceProvider(jobPostRequest)
                Log.i("TAG", "bind: " + jobPostRequest)

                val dialogBuilder = AlertDialog.Builder(context)
                val inflater = LayoutInflater.from(context).inflate(R.layout.customer_enquiry_dialog_box, null)
                dialogBuilder.setView(inflater)
                val close = inflater.findViewById<ImageView>(R.id.ivClose)
                rvDialogList = inflater.findViewById(R.id.rvDialogList)
                noData = inflater.findViewById(R.id.noData)
                val dialog = dialogBuilder.create()
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                close.setOnClickListener {
                    dialog.dismiss()
                }
                dialog.show()

            }

        }
        @RequiresApi(Build.VERSION_CODES.O)
        fun convertTimeTo12HourFormat(time: String): String {
            return try {
                val inputFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
                val outputFormatter = DateTimeFormatter.ofPattern("hh:mm a")
                val parsedTime = LocalTime.parse(time, inputFormatter)
                parsedTime.format(outputFormatter)
            } catch (e: DateTimeParseException) {
                "Invalid time format" // Handle invalid time format
            }
        }
        private fun getJobBidPersonByServiceProvider(serviceCategoryResponse: JobPostRequest) {
            val call = RetrofitClient.api.getJobBidPersonByServiceProvider(serviceCategoryResponse)
            call.enqueue(object : Callback<JobBidResponse> {
                override fun onResponse(call: Call<JobBidResponse>, response: Response<JobBidResponse>) {
                    if (response.isSuccessful) {
                        if (response.body()?.status == 1) {
                            val updateProfileResponse = response.body()
                            bids = updateProfileResponse?.data ?: emptyList()
                            if (bids.isEmpty()){
                                noData.visibility = View.VISIBLE
                            }
                            customerEnquiryDialogAdapter = CustomerEnquiryDialogAdapter(context, bids)
                            rvDialogList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                            rvDialogList.adapter = customerEnquiryDialogAdapter
                            Log.i("TAG", "onResponse: $updateProfileResponse")
//                            val intent = Intent(context, SettingsActivity::class.java)
//                            itemView.context.startActivity(intent)
                        }
                    } else {
                        Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                        Log.i("TAG", "appUserServicesUpdate onResponse error: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<JobBidResponse>, t: Throwable) {
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