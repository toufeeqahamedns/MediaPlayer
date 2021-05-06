package com.example.mediaplayer.data

import android.net.Uri
import androidx.recyclerview.widget.DiffUtil

data class MediaStore(
    val id: Long,
    val displayName: String = "",
    val dateAdded: String = "",
    val contentUri: Uri
) {
    companion object {
        val DiffCallback = object : DiffUtil.ItemCallback<MediaStore>() {
            override fun areItemsTheSame(oldItem: MediaStore, newItem: MediaStore) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: MediaStore, newItem: MediaStore) =
                oldItem == newItem
        }
    }
}