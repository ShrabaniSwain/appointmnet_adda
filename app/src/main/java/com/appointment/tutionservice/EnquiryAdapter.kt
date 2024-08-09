package com.appointment.tutionservice

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.appointment.tutionservice.databinding.ItemEnquiryItemsBinding

class EnquiryAdapter(val context: Context, val businessList: List<BusinessService>) : RecyclerView.Adapter<EnquiryAdapter.CardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_enquiry_items, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = businessList[position]
        holder.bind(item)

    }

    override fun getItemCount(): Int {
        return businessList.size
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemEnquiryItemsBinding.bind(itemView)

        fun bind(name : BusinessService) {
            Utility.itemBackGround(itemView)
            binding.tvserviceName.text = name.service_name
            binding.tvServiceDetails1.text = name.service_description
            Glide.with(context).load(name.service_img_path ?: "").into(binding.ivImage)

            binding.btnEnquiry.setOnClickListener {
                Constant.JOBTITLE = name.service_name
                Constant.SERVICE_CAT_TYPE = name.service_type
                Constant.SERVICE_ID = name.service_id
                Constant.CAT_TYPE = name.service_id

                if (name.has_last_level) {
                    val intent = Intent(context, SubCategoryActivity::class.java)
                    itemView.context.startActivity(intent)
                }
                else{
                    if (name.has_question){
                        val intent = Intent(context, SelectQuestionOne::class.java)
                        Constant.ENQUIRY = "enquiry"
                        context.startActivity(intent)
                    }else {
                        val intent = Intent(context, EnquiryLastStepActivity::class.java)
                        itemView.context.startActivity(intent)
                    }
                }
//
//                if (!name.has_question){
//                    val intent = Intent(context, EnquiryLastStepActivity::class.java)
//                    itemView.context.startActivity(intent)
//                }else {
//                    val intent = Intent(context, SelectQuestionOne::class.java)
//                    Constant.ENQUIRY = "enquiry"
//                    context.startActivity(intent)
//                }
            }
        }

    }
}