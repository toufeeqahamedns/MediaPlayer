package com.example.mediaplayer.medialist

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mediaplayer.R
import com.example.mediaplayer.databinding.FragmentMediaListBinding
import com.example.mediaplayer.repository.models.MediaItemList
import com.google.android.material.snackbar.Snackbar


private const val TAG = "MediaListFragment"

private const val READ_EXTERNAL_STORAGE_REQUEST = 1000

class MediaListFragment : Fragment() {

    private lateinit var searchView: SearchView
    private lateinit var binding: FragmentMediaListBinding
    private lateinit var adapter: MediaListAdapter

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

        adapter = MediaListAdapter(OnClickListener { position ->
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

        viewModel.searchedMediaItemList.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_item, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        searchView = item.actionView as SearchView
        searchView.setOnQueryTextListener(onQueryTextListener)

        searchView.findViewById<ImageView>(R.id.search_close_btn).setOnClickListener {
            val res = viewModel.mediaItemList.value
            adapter.submitList(res)
            searchView.setQuery("", false)
            searchView.clearFocus()
            item.collapseActionView()
        }

        return super.onOptionsItemSelected(item)
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

    private val onQueryTextListener: SearchView.OnQueryTextListener =
        object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                getDealsFromDb(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                getDealsFromDb(newText)
                return true
            }

            private fun getDealsFromDb(searchText: String) {
                if (searchText.isNotEmpty()) {
                    viewModel.searchMedia("$searchText%")
                } else {
                    adapter.submitList(viewModel.mediaItemList.value)
                }
            }
        }
}