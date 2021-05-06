package com.example.mediaplayer.medialist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mediaplayer.data.MediaStore
import com.example.mediaplayer.databinding.MediaViewItemBinding

class MediaListAdapter : ListAdapter<MediaStore, MediaViewHolder>(MediaStore.DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        return MediaViewHolder(MediaViewItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        val mediaItem = getItem(position)
        holder.bind(mediaItem)
    }
}

class MediaViewHolder(private val binding: MediaViewItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(mediaItem: MediaStore) {
        binding.mediastore = mediaItem
        binding.executePendingBindings()
    }
}

class OnClickListener(val clickListener: (mediaItem: MediaStore) -> Unit) {
    fun onClick(mediaItem: MediaStore) = clickListener(mediaItem)
}