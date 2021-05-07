package com.example.mediaplayer.repository

import android.app.Application
import com.example.mediaplayer.database.MediaDao
import com.example.mediaplayer.database.MediaDatabase
import com.example.mediaplayer.repository.models.MediaItem

class Repository(application: Application) {

    private val mediaDao: MediaDao = MediaDatabase.getInstance(application).mediaDao

    val savedMedia = mediaDao.getSavedMedia()

    suspend fun saveMediaItem(mediaItem: MediaItem) {
        mediaDao.saveMediaItem(mediaItem)
    }

    suspend fun searchMedia(searchText: String): List<MediaItem> {
        return mediaDao.searchMedia(searchText)
    }
}