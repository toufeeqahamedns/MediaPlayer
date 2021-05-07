package com.example.mediaplayer.repository

import androidx.recyclerview.widget.DiffUtil
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "media_table")
data class MediaItem(
    @PrimaryKey
    val id: Long,
    val displayName: String,
    val dateAdded: String,
    val contentUri: String,
) {
    companion object {
        val DiffCallback = object : DiffUtil.ItemCallback<MediaItem>() {
            override fun areItemsTheSame(oldItem: MediaItem, newItem: MediaItem) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: MediaItem, newItem: MediaItem) =
                oldItem == newItem
        }
    }
}