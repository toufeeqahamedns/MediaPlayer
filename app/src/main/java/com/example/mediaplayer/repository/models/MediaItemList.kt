package com.example.mediaplayer.repository.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class MediaItemList(
    val mediaItemList: @RawValue List<MediaItem>?
) : Parcelable