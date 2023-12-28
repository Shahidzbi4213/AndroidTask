package com.gulehri.androidtask.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.gulehri.androidtask.R
import com.gulehri.androidtask.ads.InterstitialHelper
import com.gulehri.androidtask.ui.adapters.ScreenshotsAdapter
import com.gulehri.androidtask.databinding.FragmentMainBinding
import com.gulehri.androidtask.utils.Extensions
import com.gulehri.androidtask.model.Image
import com.gulehri.androidtask.ui.vm.ImageViewModel
import com.gulehri.androidtask.utils.Extensions.hide
import com.gulehri.androidtask.utils.Extensions.show

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private var _adapter: ScreenshotsAdapter? = null
    val adapter get() = _adapter!!


    private val imageViewModel by viewModels<ImageViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        loadImages()


        //PreLoadInterAd
        if (Extensions.isNetworkAvailable(view.context))
            activity?.let { InterstitialHelper.loadInterAd(it) }


        binding.swipeRefreshLayout.setOnRefreshListener {
            loadImages()
        }
    }

    private fun loadImages() {

        // Observe the LiveData
        imageViewModel.imageList.observe(viewLifecycleOwner) { images ->
            val updatedImageList = mutableListOf<Image>()

            for ((index, image) in images.withIndex()) {
                updatedImageList.add(image)

                // Add an ad item after every 6 images
                if ((index + 1) % 6 == 0 && index + 1 < images.size) {
                    val adItem = Image(path = "ad", view_type = 1) // Assuming view_type 1 is for ad items
                    updatedImageList.add(adItem)
                }
            }

            Log.d("loadImages", "$updatedImageList")

            adapter.updateData(updatedImageList)

            binding.swipeRefreshLayout.isRefreshing = false

            if (updatedImageList.isEmpty()) {
                binding.noDataPlaceHolder.show()
            } else {
                binding.noDataPlaceHolder.hide()
            }
        }

        imageViewModel.loadImages()
    }


    private fun setupRecyclerView() {
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
        _adapter = ScreenshotsAdapter(requireContext(),::menuClick)
        binding.recView.adapter = adapter

    }

    private fun menuClick(image: Image, anchorView: View) {
        val popupMenu = PopupMenu(requireContext(), anchorView)
        popupMenu.inflate(R.menu.menu)

        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.menu_open -> {

                    navigateToNext(image)
                    true
                }

                R.id.menu_share -> {
                    activity?.let {
                        Extensions.shareImage(it, image.path)
                    }

                    true
                }

                R.id.menu_delete -> {
                    imageViewModel.deleteFile(image.path)
                    true
                }

                else -> false
            }
        }

        popupMenu.show()
    }

    private fun navigateToNext(image: Image) {

        activity?.let {
            InterstitialHelper.showAndLoadInterAd(it, true) {
                if (findNavController().currentDestination?.id == R.id.mainFragment) {

                    findNavController().navigate(
                        R.id.action_mainFragment_to_detailFragment,
                        bundleOf("image" to image)
                    )
                }
            }

        }
    }


    override fun onDestroyView() {
        super.onDestroyView()

        _adapter = null
        _binding = null
    }
}