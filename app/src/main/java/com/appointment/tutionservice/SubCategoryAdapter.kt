package com.appointment.tutionservice

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.appointment.tutionservice.databinding.ItemSubCategoryBinding

class SubCategoryAdapter(private val notification: List<ServiceItem>, val context: Context) : RecyclerView.Adapter<SubCategoryAdapter.CardViewHolder>() {

    private var filteredMarketItems: List<ServiceItem> = notification
    private val selectedServices: MutableList<Service> = mutableListOf()
    private val selectedStates = mutableMapOf<String, Boolean>()

    init {
        // Initialize selectedStates based on initial data
        notification.forEach { serviceItem ->
            selectedStates[serviceItem.service_id] = serviceItem.isSelected
            if (serviceItem.isSelected) {
                selectedServices.add(Service(service_id = serviceItem.service_id, service_name = serviceItem.service_name))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sub_category, parent, false)
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
        private val binding = ItemSubCategoryBinding.bind(itemView)

//        fun bind(service: ServiceItem) {
//            Utility.itemBackGround(itemView)
//            binding.serviceName.text = service.service_name
//            binding.serviceName.isChecked = selectedStates[service.service_id] ?: false
//
//            binding.serviceName.setOnClickListener {
//                val itemId = service.service_id
//                val currentState = selectedStates[itemId] ?: false
//
//                val newState = !currentState
//                selectedStates[itemId] = newState
//
//                binding.serviceName.isChecked = newState
//
//                if (newState) {
//                    selectedServices.add(Service(service_id = itemId, service_name = service.service_name))
//                } else {
//                    selectedServices.removeIf { it.service_id == itemId }
//                }
//
//                Log.i("TAG", "selectedServices: $selectedServices")
//            }
//        }

        fun bind(service: ServiceItem) {
            Utility.itemBackGround(itemView)
            binding.serviceName.text = service.service_name
            binding.serviceName.isChecked = selectedStates[service.service_id] ?: false

            binding.serviceName.setOnClickListener {
                val itemId = service.service_id
                val currentState = selectedStates[itemId] ?: false
                val newState = !currentState
                selectedStates.keys.forEach { id ->
                    selectedStates[id] = false
                }
                selectedStates[itemId] = newState
                binding.serviceName.isChecked = newState

                selectedServices.clear()
                if (newState) {
                    selectedServices.add(Service(service_id = itemId, service_name = service.service_name))
                }

                notifyDataSetChanged()

                Log.i("TAG", "selectedServices: $selectedServices")
            }
        }
    }

    fun updateList(newList: List<ServiceItem>) {
        filteredMarketItems = newList
//        // Reinitialize selectedStates based on new list
//        selectedStates.clear()
//        selectedServices.clear()
//        newList.forEach { serviceItem ->
//            selectedStates[serviceItem.service_id] = serviceItem.isSelected
//            if (serviceItem.isSelected) {
//                selectedServices.add(Service(service_id = serviceItem.service_id, service_name = serviceItem.service_name))
//            }
//        }
        notifyDataSetChanged()
    }

    fun getSelectedServices(): List<Service> {
        return selectedServices.toList()
    }
}

