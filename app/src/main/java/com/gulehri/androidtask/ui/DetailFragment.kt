package com.gulehri.androidtask.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import coil.load
import com.gulehri.androidtask.R
import com.gulehri.androidtask.databinding.FragmentDetailBinding
import com.gulehri.androidtask.model.Image

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!


    fun image() = arguments?.getParcelable<Image>("image")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //binding.imageView3.load(image()?.path)

        binding.imageView3.load(R.drawable.cat)

        binding.toolbar.setNavigationOnClickListener {
            if (findNavController().currentDestination?.id == R.id.mainFragment)
                findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}