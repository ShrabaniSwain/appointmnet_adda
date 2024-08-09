package com.appointment.tutionservice

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CustomerBanner(val context: Context, private val banners: List<Banner>) : RecyclerView.Adapter<CustomerBanner.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sliderImageView: ImageView = itemView.findViewById(R.id.bannerImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_banner, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context)
            .load(banners[position].bannerImage)
            .into(holder.sliderImageView)
        holder.sliderImageView.setOnClickListener {
            val bannerLink = banners[position].externalLink
            if (bannerLink.isNotEmpty() && Patterns.WEB_URL.matcher(bannerLink).matches()) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(bannerLink))
                context.startActivity(intent)
            } else {
                Toast.makeText(context, "Invalid URL", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int {
        return banners.size
    }
}