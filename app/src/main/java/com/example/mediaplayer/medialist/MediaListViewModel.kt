package com.example.mediaplayer.medialist

import android.annotation.SuppressLint
import android.app.Application
import android.content.ContentResolver
import android.content.ContentUris
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mediaplayer.data.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

private const val TAG = "MediaListVM"

enum class MediaLoadingStatus { LOADING, ERROR, DONE }

class MediaListViewModel(application: Application) : AndroidViewModel(application) {

    private var contentObserver: ContentObserver? = null

    private val _mediaList = MutableLiveData<List<MediaStore>>()
    val mediaList: LiveData<List<MediaStore>>
        get() = _mediaList

    private val _status = MutableLiveData<MediaLoadingStatus>()
    val status: LiveData<MediaLoadingStatus>
        get() = _status

    fun loadMedia() {
        viewModelScope.launch {
            try {
                _status.value = MediaLoadingStatus.LOADING
                val imageList = queryImages()
                _mediaList.value = imageList
                _status.value = MediaLoadingStatus.DONE

                if (contentObserver == null) {
                    contentObserver =
                        getApplication<Application>().contentResolver.registerObserver(
                            android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        ) {
                            loadMedia()
                        }
                }
            } catch (e: Exception) {
                _status.value = MediaLoadingStatus.ERROR
                _mediaList.value = ArrayList()
            }
        }
    }

    private suspend fun queryImages(): List<MediaStore> {
        val images = mutableListOf<MediaStore>()

        withContext(Dispatchers.IO) {

            val projection = arrayOf(
                android.provider.MediaStore.Video.Media._ID,
                android.provider.MediaStore.Video.Media.DISPLAY_NAME,
                android.provider.MediaStore.Video.Media.DATE_ADDED
            )

            val selection = "${android.provider.MediaStore.Video.Media.DATE_ADDED} >= ?"

            val sortOrder = "${android.provider.MediaStore.Video.Media.DATE_ADDED} DESC"

            val selectionArgs = arrayOf(
                // Release day of the G1. :)
                dateToTimestamp(day = 22, month = 10, year = 2008).toString()
            )

            getApplication<Application>().contentResolver.query(
                android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
            )?.use { cursor ->

                val idColumn =
                    cursor.getColumnIndexOrThrow(android.provider.MediaStore.Video.Media._ID)
                val dateModifiedColumn =
                    cursor.getColumnIndexOrThrow(android.provider.MediaStore.Video.Media.DATE_ADDED)
                val displayNameColumn =
                    cursor.getColumnIndexOrThrow(android.provider.MediaStore.Video.Media.DISPLAY_NAME)

                Log.i(TAG, "Found ${cursor.count} images")
                while (cursor.moveToNext()) {

                    // Here we'll use the column indexs that we found above.
                    val id = cursor.getLong(idColumn)
                    val dateModified =
                        Date(TimeUnit.SECONDS.toMillis(cursor.getLong(dateModifiedColumn)))
                    val displayName = cursor.getString(displayNameColumn)

                    val contentUri = ContentUris.withAppendedId(
                        android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        id
                    )

                    val image = MediaStore(
                        id,
                        displayName,
                        SimpleDateFormat("MM-dd-yyyy").format(dateModified),
                        contentUri
                    )
                    images += image

                    // For debugging, we'll output the image objects we create to logcat.
                    Log.v(TAG, "Added image: $image")
                }
            }
        }

        Log.v(TAG, "Found ${images.size} images")
        return images
    }

    @SuppressLint("SimpleDateFormat")
    private fun dateToTimestamp(day: Int, month: Int, year: Int): Long =
        SimpleDateFormat("dd.MM.yyyy").let { formatter ->
            TimeUnit.MICROSECONDS.toSeconds(formatter.parse("$day.$month.$year")?.time ?: 0)
        }

    override fun onCleared() {
        contentObserver?.let {
            getApplication<Application>().contentResolver.unregisterContentObserver(it)
        }
    }
}

private fun ContentResolver.registerObserver(
    uri: Uri,
    observer: (selfChange: Boolean) -> Unit
): ContentObserver {
    val contentObserver = object : ContentObserver(Handler()) {
        override fun onChange(selfChange: Boolean) {
            observer(selfChange)
        }
    }
    registerContentObserver(uri, true, contentObserver)
    return contentObserver
}