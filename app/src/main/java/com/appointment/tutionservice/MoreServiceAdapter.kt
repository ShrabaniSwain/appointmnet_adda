package com.appointment.tutionservice

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.appointment.tutionservice.databinding.ItemServicesBinding


class MoreServiceAdapter(val context: Context, val featuredService: List<FeaturedService>) : RecyclerView.Adapter<MoreServiceAdapter.CardViewHolder>() {

    private var filteredMarketItems: List<FeaturedService> = featuredService

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_services, parent, false)
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
        private val binding = ItemServicesBinding.bind(itemView)

        fun bind(name: FeaturedService) {
            binding.tvserviceName.text = name.service_name
            binding.description.text = name.service_description
            Glide.with(context).load(name.service_img_path ?: "").into(binding.ivImage)

            Utility.itemBackGround(itemView)

            binding.cardview.setOnClickListener {
                Constant.SERVICE_ID = name.service_id
                Constant.CAT_TYPE = name.service_id
                Constant.JOBTITLE = name.service_name
                Constant.SERVICE_CAT_TYPE = name.service_type

                val intent = Intent(context, TeacherActivity::class.java)
                itemView.context.startActivity(intent)
            }

        }

    }

    fun updateList(newList: List<FeaturedService>) {
        filteredMarketItems = newList
        notifyDataSetChanged()
    }
}