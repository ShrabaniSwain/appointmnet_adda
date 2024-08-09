package com.appointment.tutionservice

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.appointment.tutionservice.databinding.ItemSettingsBinding


class SearchServiceAdapter(private val notification: List<SearchBar>, val context: Context) : RecyclerView.Adapter<SearchServiceAdapter.CardViewHolder>() {

    private var filteredMarketItems: List<SearchBar> = notification

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_settings, parent, false)
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
        private val binding = ItemSettingsBinding.bind(itemView)

        fun bind(service: SearchBar) {
            Utility.itemBackGround(itemView)
            binding.serviceName.text = service.service_name
            binding.serviceName.setOnClickListener {
                Constant.JOBTITLE = service.service_name
                Constant.SERVICE_ID = service.service_id

                if (service.has_question == 0){
                    val intent = Intent(context, EnquiryLastStepActivity::class.java)
                    itemView.context.startActivity(intent)
                }else {
                    val intent = Intent(context, SelectQuestionOne::class.java)
                    Constant.ENQUIRY = "enquiry"
                    itemView.context.startActivity(intent)
                }

            }
        }
    }

    fun updateList(newList: List<SearchBar>) {
        filteredMarketItems = newList
        notifyDataSetChanged()
    }
}