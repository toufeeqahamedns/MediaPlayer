package com.example.mediaplayer.player

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import com.example.mediaplayer.R
import com.example.mediaplayer.databinding.FragmentMediaPlayerBinding
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import kotlin.properties.Delegates


private const val TAG = "MediaPlayerFragment"


class MediaPlayerFragment : Fragment() {

    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition: Long = 0

    private lateinit var binding: FragmentMediaPlayerBinding
    private lateinit var playerView: PlayerView
    private lateinit var arguments: MediaPlayerFragmentArgs
    private var playerVolume by Delegates.notNull<Float>()

    private var player: SimpleExoPlayer? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentMediaPlayerBinding.inflate(inflater)

        playerView = binding.playerView

        arguments = MediaPlayerFragmentArgs.fromBundle(requireArguments())

        Log.d(TAG, "${arguments.mediaItemPosition} ${arguments.mediaItemList}")

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.mute_unmute, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.title == "Mute") {
            player?.volume = 0f
            item.title = "Unmute"
        } else {
            player?.volume = playerVolume
            item.title = "Mute"
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        if (Build.VERSION.SDK_INT >= 24) {
            initializePlayer()
        }
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT < 24) {
            initializePlayer()
        }
    }

    override fun onPause() {
        super.onPause()
        if (Build.VERSION.SDK_INT <= 23) {
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Build.VERSION.SDK_INT > 23) {
            releasePlayer()
        }
    }

    private fun initializePlayer() {
        player = SimpleExoPlayer.Builder(this.requireContext()).build()
        playerView.player = player
        playerVolume = player?.volume!!

        for (mediaItemFromList in arguments.mediaItemList.mediaItemList!!) {
            val secondMediaItem = MediaItem.fromUri(Uri.parse(mediaItemFromList.contentUri))
            player!!.addMediaItem(secondMediaItem)
        }

        currentWindow = arguments.mediaItemPosition

        player!!.playWhenReady = playWhenReady
        player!!.seekTo(currentWindow, playbackPosition)
        player!!.prepare()
    }

    private fun releasePlayer() {
        player?.let {
            playWhenReady = it.playWhenReady
            playbackPosition = it.currentPosition
            currentWindow = it.currentWindowIndex
            it.release()
        }

        player = null
    }
}