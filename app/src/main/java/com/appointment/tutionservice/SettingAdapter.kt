package com.appointment.tutionservice

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.appointment.tutionservice.databinding.ItemSettingsBinding

class SettingAdapter(private val notification: List<ServiceItem>, val context: Context) : RecyclerView.Adapter<SettingAdapter.CardViewHolder>() {

    private var filteredMarketItems: List<ServiceItem> = notification.filter { it.isSelected }

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

        fun bind(service: ServiceItem) {
            Utility.itemBackGround(itemView)
            binding.serviceName.text = service.service_name
        }
    }

    fun updateList(newList: List<ServiceItem>) {
        filteredMarketItems = newList.filter { it.isSelected }
        notifyDataSetChanged()
    }
}
