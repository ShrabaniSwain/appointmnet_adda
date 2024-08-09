package com.appointment.tutionservice

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.appointment.FullScreenImageActivity
import com.appointment.tutionservice.databinding.ItemPhotosBinding

class PhotosAdapter(
    private val context: Context,
    private val appUserData: AppUserData
) : RecyclerView.Adapter<PhotosAdapter.CardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val binding = ItemPhotosBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val photoUrl = appUserData.photo_gallery_list[position]
        holder.bind(photoUrl)
    }

    override fun getItemCount(): Int {
        return appUserData.photo_gallery_list.size
    }

    inner class CardViewHolder(private val binding: ItemPhotosBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(photoUrl: String) {
            Glide.with(context)
                .load(photoUrl)
                .into(binding.ivPhotos)

            binding.imageview.setOnClickListener {
                val context = binding.root.context
                val intent = Intent(context, FullScreenImageActivity::class.java)
                intent.putExtra("IMAGE_URL", photoUrl)
                itemView.context.startActivity(intent)
            }
        }
    }
}