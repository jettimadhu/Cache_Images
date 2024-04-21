package com.display.photos.ui

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.display.photos.R
import com.display.photos.databinding.FragmentMainBinding
import com.display.photos.ui.adapter.PhotosAdapter
import com.display.photos.util.Utils
import com.display.photos.util.cache.CachedDataImpl
import com.display.photos.util.cache.DiskCache
import com.display.photos.util.cache.MemoryCache
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel by viewModel<MainViewModel>()

    private val photosAdapter by lazy {
        val cacheDir = File(activity?.cacheDir, "image_cache")
        PhotosAdapter(mutableListOf(), CachedDataImpl(MemoryCache(), DiskCache(cacheDir)))
    }

    private var pastVisibleItems = 0
    private var visibleItemCount = 0
    private var totalItemCount = 0

    companion object {
        fun newInstance() = MainFragment()
        private const val LANDSCAPE_PHOTOS_SPAN_COUNT = 4
        private const val PORTRAIT_PHOTOS_SPAN_COUNT = 2
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    /*override fun onPause() {
        super.onPause()
        viewModel.clear()
    }*/

    private fun init() {
        setupObservers()
        setupRecyclerView()
    }

    private fun setupObservers() {
        viewModel.photosResponseLiveData.observe(viewLifecycleOwner) { list ->
            binding.errorView.isVisible = false
            binding.progress.isVisible = false
            photosAdapter.addData(list)
        }

        viewModel.errorLiveData.observe(viewLifecycleOwner) { message ->
            context?.let {
                binding.progress.isVisible = false
                //if we have already fetched images then we can show those images from cache.
                if (photosAdapter.itemCount == 0) {
                    binding.errorView.isVisible = true
                }
                Utils.displayToast(it, message ?: getString(R.string.error_text))
            }
        }
    }

    /**
     * To setup the RecyclerView and it fetch and loads the next set of images on scroll position.
     */
    private fun setupRecyclerView() {
        binding.unsplashRecyclerView.apply {
            layoutManager = getStaggeredLayoutManager()
            adapter = photosAdapter
        }

        photosAdapter.clear()
        photosAdapter.addData(viewModel.getTotalPhotosList())

        binding.unsplashRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val mLayoutManager =
                    binding.unsplashRecyclerView.layoutManager as? StaggeredGridLayoutManager
                        ?: return

                visibleItemCount = mLayoutManager.childCount
                totalItemCount = mLayoutManager.itemCount
                var firstVisibleItems: IntArray? = null

                firstVisibleItems = mLayoutManager.findFirstVisibleItemPositions(firstVisibleItems)
                if (firstVisibleItems != null && firstVisibleItems.isNotEmpty()) {
                    pastVisibleItems = firstVisibleItems[0]
                }

                if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                    if (viewModel.canRequestMore())
                        viewModel.getPhotos()
                }
            }
        })
    }

    private fun getStaggeredLayoutManager(): StaggeredGridLayoutManager {
        val spanCount =
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                LANDSCAPE_PHOTOS_SPAN_COUNT
            } else {
                PORTRAIT_PHOTOS_SPAN_COUNT
            }
        return StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.photosResponseLiveData.removeObservers(viewLifecycleOwner)
        viewModel.errorLiveData.removeObservers(viewLifecycleOwner)
        _binding = null
    }
}