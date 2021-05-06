package com.example.mediaplayer

import android.net.Uri
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.mediaplayer.data.MediaStore
import com.example.mediaplayer.medialist.MediaLoadingStatus
import com.example.mediaplayer.medialist.MediaListAdapter

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<MediaStore>?) {
    val adapter = recyclerView.adapter as MediaListAdapter
    adapter.submitList(data)
}

@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUri: Uri?) {
    imgUri?.let {
        Glide.with(imgView.context)
            .load(imgUri)
            .thumbnail(0.33f)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.ic_broken_image)
            )
            .into(imgView)
    }
}

@BindingAdapter("mediaLoadingStatus")
fun bindStatus(statusImageView: ImageView, status: MediaLoadingStatus?) {
    when (status) {
        MediaLoadingStatus.LOADING -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.loading_animation)
        }
        MediaLoadingStatus.ERROR -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.ic_broken_image)
        }
        MediaLoadingStatus.DONE -> {
            statusImageView.visibility = View.GONE
        }
    }
}