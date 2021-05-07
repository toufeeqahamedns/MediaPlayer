package com.example.mediaplayer.repository

import android.app.Application
import com.example.mediaplayer.database.MediaDao
import com.example.mediaplayer.database.MediaDatabase

class Repository(application: Application) {

    private val mediaDao: MediaDao = MediaDatabase.getInstance(application).mediaDao

    val savedMedia = mediaDao.getSavedMedia()

    suspend fun saveMediaItem(mediaItem: MediaItem) {
        mediaDao.saveMediaItem(mediaItem)
    }

}