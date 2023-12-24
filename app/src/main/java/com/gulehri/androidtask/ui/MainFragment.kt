package com.gulehri.androidtask.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.gulehri.androidtask.Interfaces.ScreenshotClicks
import com.gulehri.androidtask.R
import com.gulehri.androidtask.adapters.ScreenshotsAdapter
import com.gulehri.androidtask.databinding.FragmentMainBinding
import com.gulehri.androidtask.utils.Extensions.debug
import com.gulehri.androidtask.utils.Image
import com.gulehri.androidtask.utils.ImageViewModel

class MainFragment : Fragment(),ScreenshotClicks {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter:ScreenshotsAdapter
    private lateinit var imageViewModel: ImageViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        setupRecyclerView()
        loadImages()

        binding.swipeRefreshLayout.setOnRefreshListener {
            loadImages()
        }
        return binding.root
    }

    private fun loadImages() {
        imageViewModel = ViewModelProvider(this)[ImageViewModel::class.java]

        // Observe the LiveData
        imageViewModel.imageList.observe(viewLifecycleOwner) { images ->
            for (image in images) {
                image.debug()
                adapter.updateData(images.toMutableList())
            }
            binding.swipeRefreshLayout.isRefreshing = false
        }

        imageViewModel.loadImages()
    }

    private fun setupRecyclerView() {

        adapter = ScreenshotsAdapter()
        binding.recView.adapter = adapter
        val layoutManager = GridLayoutManager(requireContext(), 3)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (adapter.getItemViewType(position) == ScreenshotsAdapter.VIEW_TYPE_AD) {
                    layoutManager.spanCount
                } else {
                    1
                }
            }
        }
        binding.recView.layoutManager = layoutManager
        adapter.screenshotClicks = this

    }


    override fun menuClick(position: Int, anchorView: View) {
        val popupMenu = PopupMenu(requireContext(), anchorView)
        popupMenu.inflate(R.menu.menu)

        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.menu_open -> {
                    // Handle open action
                    true
                }
                R.id.menu_share -> {
                    // Handle share action
                    true
                }
                R.id.menu_delete -> {
                    // Handle delete action
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}