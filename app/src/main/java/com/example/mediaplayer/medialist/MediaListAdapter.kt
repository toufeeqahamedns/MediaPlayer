package com.example.mediaplayer.medialist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mediaplayer.databinding.MediaViewItemBinding
import com.example.mediaplayer.repository.models.MediaItem

class MediaListAdapter(private val clickListener: OnClickListener) : ListAdapter<MediaItem, MediaViewHolder>(
    MediaItem.DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        return MediaViewHolder(MediaViewItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        val mediaItem = getItem(position)
        holder.bind(position, mediaItem, clickListener)
    }
}

class MediaViewHolder(private val binding: MediaViewItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(position: Int, mediaItem: MediaItem, clickListener: OnClickListener) {
        binding.root.setOnClickListener {
            clickListener.onClick(position)
        }

        binding.mediaItem = mediaItem
        binding.executePendingBindings()
    }
}

class OnClickListener(val clickListener: (position: Int) -> Unit) {
    fun onClick(position: Int) = clickListener(position)
}