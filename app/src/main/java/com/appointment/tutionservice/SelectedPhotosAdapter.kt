package com.appointment.tutionservice

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class SelectedPhotosAdapter(private val context: Context, private val photos: MutableList<Uri>,     private val onAddPhotoClickListener: OnAddPhotoClickListener
) : RecyclerView.Adapter<SelectedPhotosAdapter.PhotoViewHolder>() {

    private val TYPE_CAMERA = 0
    private val TYPE_PHOTO = 1

    inner class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = if (viewType == TYPE_CAMERA) {
            LayoutInflater.from(context).inflate(R.layout.item_image, parent, false)
        } else {
            LayoutInflater.from(context).inflate(R.layout.item_image, parent, false)
        }
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        if (getItemViewType(position) == TYPE_CAMERA) {
            holder.imageView.setImageResource(R.drawable.baseline_camera_alt_24)
            holder.imageView.setOnClickListener {
                onAddPhotoClickListener.openPhotoGallery()
            }
        } else {
            Glide.with(context)
                .load(photos[position])
                .into(holder.imageView)
        }
    }

    override fun getItemCount(): Int {
        return photos.size + 1 // One extra for the camera icon
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == photos.size) TYPE_CAMERA else TYPE_PHOTO
    }

    fun addPhoto(uri: Uri) {
        photos.add(uri)
        notifyItemInserted(photos.size - 1)
    }
}

interface OnAddPhotoClickListener {
    fun openPhotoGallery()
}
