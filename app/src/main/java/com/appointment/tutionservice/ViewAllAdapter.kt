package com.appointment.tutionservice

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.appointment.tutionservice.databinding.ViewAllItemBinding


class ViewAllAdapter(service: List<ServiceItem>, val context: Context) : RecyclerView.Adapter<ViewAllAdapter.CardViewHolder>() {

    private var filteredMarketItems: List<ServiceItem> = service

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_all_item, parent, false)
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
        private val binding = ViewAllItemBinding.bind(itemView)

        fun bind(notificationText: ServiceItem) {
            Utility.itemBackGround(itemView)
            binding.tvserviceName.text = notificationText.service_name
            binding.tvServiceDeatails.text = notificationText.service_description
            Glide.with(context)
                .load(notificationText.img_path)
                .into(binding.ivImage)

            if (!notificationText.has_last_level){
                binding.btnBookAppointment.visibility = View.VISIBLE
                binding.tvEnquire.visibility = View.VISIBLE
            }
            else{
                binding.btnBookAppointment.visibility = View.GONE
                binding.tvEnquire.visibility = View.GONE

                itemView.setOnClickListener {
                    Constant.SERVICE_CAT_TYPE = notificationText.cat_type
                    Constant.CAT_TYPE = notificationText.service_id
                    val intent = Intent(context, SubCategoryActivity::class.java)
                    itemView.context.startActivity(intent)
                }
            }
            binding.btnBookAppointment.setOnClickListener {
                Constant.JOBTITLE = notificationText.service_name
                Constant.SERVICE_ID = notificationText.service_id
                Constant.Question = notificationText.has_question

                Log.i("TAG", "bind: " + Constant.JOBTITLE + " " + Constant.SERVICE_ID +" " + notificationText.cat_type)


                val intent = Intent(context, AppointmentListActivity::class.java)
                Constant.ENQUIRY = "appointment"
                itemView.context.startActivity(intent)
            }

            binding.tvEnquire.setOnClickListener {
                Constant.JOBTITLE = notificationText.service_name
                Constant.SERVICE_ID = notificationText.service_id
                Constant.Question = notificationText.has_last_level

                Log.i(
                    "TAG",
                    "bind: " + Constant.JOBTITLE + " " + Constant.SERVICE_ID
                )
                if (notificationText.has_question) {
                    val intent = Intent(context, SelectQuestionOne::class.java)
                    Constant.ENQUIRY = "enquiry"
                    itemView.context.startActivity(intent)

                } else {
                    val intent = Intent(context, EnquiryLastStepActivity::class.java)
                    itemView.context.startActivity(intent)
                }
            }

            }
        }


    fun updateList(newList: List<ServiceItem>) {
        filteredMarketItems = newList
        notifyDataSetChanged()
    }
}