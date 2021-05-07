package com.example.mediaplayer.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mediaplayer.repository.models.MediaItem

@Dao
interface MediaDao {

    @Query("Select * from media_table order by id DESC")
    fun getSavedMedia(): LiveData<List<MediaItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveMediaItem(mediaItem: MediaItem)

}