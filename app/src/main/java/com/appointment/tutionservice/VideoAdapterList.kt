package com.appointment.tutionservice

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.recyclerview.widget.RecyclerView
import com.appointment.tutionservice.databinding.ItemVideoListBinding
import com.bumptech.glide.Glide

class VideoAdapterList(private val notification: List<TutorialData>, val context: Context) : RecyclerView.Adapter<VideoAdapterList.CardViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_video_list, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = notification[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return notification.size
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemVideoListBinding.bind(itemView)

        fun bind(notificationText: TutorialData) {
            // Assuming you're using ViewBinding
            binding.tvDetails.text = notificationText.name

            val uri = Uri.parse(notificationText.files)
            binding.videoView.setVideoURI(uri)

            // Load thumbnail using Glide (Glide will automatically handle remote URLs)
            Glide.with(context)
                .asBitmap()
                .load(notificationText.files) // Load video thumbnail from URL
                .into(binding.thumbnailImageView) // Set thumbnail to ImageView

            // Media controller for video controls (play/pause etc.)
            val mediaController = MediaController(context)
            mediaController.setAnchorView(binding.videoView)
            binding.videoView.setMediaController(mediaController)

            // Initially, show the play button and thumbnail
            binding.playButton.visibility = View.VISIBLE
            binding.thumbnailImageView.visibility = View.VISIBLE

            // Add listener to the play button
            binding.playButton.setOnClickListener {
                binding.videoView.start() // Start video playback
                binding.playButton.visibility = View.GONE // Hide play button when video starts
                binding.thumbnailImageView.visibility = View.GONE // Hide thumbnail when video starts
            }

            // Add listener to reset the play button when the video is finished
            binding.videoView.setOnCompletionListener {
                binding.playButton.visibility = View.VISIBLE // Show play button when the video finishes
                binding.thumbnailImageView.visibility = View.VISIBLE // Show thumbnail when the video finishes
            }
        }


    }
}