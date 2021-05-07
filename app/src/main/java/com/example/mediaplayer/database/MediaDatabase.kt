package com.example.mediaplayer.database

import android.content.Context
import androidx.room.*
import com.example.mediaplayer.repository.MediaItem

@Database(entities = [MediaItem::class], version = 1, exportSchema = false)
abstract class MediaDatabase : RoomDatabase() {

    abstract val mediaDao: MediaDao

    companion object {

        @Volatile
        private var INSTANCE: MediaDatabase? = null

        fun getInstance(context: Context): MediaDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        MediaDatabase::class.java,
                        "media_database"
                    ).fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}