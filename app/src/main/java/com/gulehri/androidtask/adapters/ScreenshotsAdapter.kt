package com.gulehri.androidtask.adapters

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.gulehri.androidtask.Interfaces.ScreenshotClicks
import com.gulehri.androidtask.R
import com.gulehri.androidtask.databinding.Native7aDesignBinding
import com.gulehri.androidtask.databinding.SingleImageBinding
import com.gulehri.androidtask.utils.Extensions.debug
import com.gulehri.androidtask.utils.Image
import java.io.File

class ScreenshotsAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var itemList: MutableList<Image> = mutableListOf()
     var screenshotClicks:ScreenshotClicks?=null
    companion object {
        const val VIEW_TYPE_IMAGE = 0
        const val VIEW_TYPE_AD = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_IMAGE -> {
                val binding = SingleImageBinding.inflate(inflater, parent, false)
                ImageViewHolder(binding)
            }
            VIEW_TYPE_AD -> {
                val binding = Native7aDesignBinding.inflate(inflater, parent, false)
                AdViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ImageViewHolder -> {
                holder.binding.apply {
                    btnMenu.setOnClickListener {
                        screenshotClicks?.menuClick(position,it)
                        try {
                            val bitmap = BitmapFactory.decodeFile(itemList[position].path)
                            imageFilterView.setImageBitmap(bitmap)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            "Error loading image: ${e.message}".debug()
                        }

                    }

                }
            }
            is AdViewHolder -> {
                holder.binding.apply {

                }
            }
        }
    }

    override fun getItemCount(): Int {
        return itemList.size + (itemList.size / 6)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position != 0 && position % 6 == 0) {
            VIEW_TYPE_AD
        } else {
            VIEW_TYPE_IMAGE
        }
    }

    fun updateData(newList: MutableList<Image>) {
        itemList = newList
        notifyDataSetChanged()
    }

    // ViewHolder for image items
    class ImageViewHolder(val binding: SingleImageBinding) : RecyclerView.ViewHolder(binding.root)

    // ViewHolder for ad items
    class AdViewHolder(val binding: Native7aDesignBinding) : RecyclerView.ViewHolder(binding.root)

}