package com.example.mediaplayer.medialist

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mediaplayer.databinding.FragmentMediaListBinding
import com.example.mediaplayer.repository.models.MediaItemList
import com.google.android.material.snackbar.Snackbar

private const val READ_EXTERNAL_STORAGE_REQUEST = 1000

class MediaListFragment : Fragment() {

    private lateinit var binding: FragmentMediaListBinding

    private val permissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
    )

    private val viewModel: MediaListViewModel by lazy {
        ViewModelProvider(this).get(MediaListViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMediaListBinding.inflate(inflater)

        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        val adapter = MediaListAdapter(OnClickListener { position ->
            findNavController().navigate(
                MediaListFragmentDirections.actionMediaListFragmentToMediaPlayerFragment2(
                    position,
                    MediaItemList(viewModel.mediaItemList.value)
                )
            )
        })

        binding.mediaGrid.also { view ->
            val orientation = resources.configuration.orientation
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                view.layoutManager = GridLayoutManager(this.requireContext(), 3)
            } else {
                view.layoutManager = GridLayoutManager(this.requireContext(), 2)
            }

            view.adapter = adapter
        }

        if (haveStoragePermission()) {
            viewModel.loadMedia()
        } else {
            requestPermission()
        }

        return binding.root
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            READ_EXTERNAL_STORAGE_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    viewModel.loadMedia()
                } else {
                    // This needs to be handled for onResume
                    if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        Snackbar.make(
                            binding.root,
                            "Please enable permissions to continue",
                            Snackbar.LENGTH_INDEFINITE
                        ).setAction("Grant") {
                            requestPermissions(permissions, READ_EXTERNAL_STORAGE_REQUEST)
                        }.show()
                    } else {
                        requestPermissions(permissions, READ_EXTERNAL_STORAGE_REQUEST)
                    }
                }
            }
        }
    }

    private fun haveStoragePermission() =
        ContextCompat.checkSelfPermission(
            this.requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

    private fun requestPermission() {
        if (!haveStoragePermission()) {
            requestPermissions(permissions, READ_EXTERNAL_STORAGE_REQUEST)
        }
    }
}