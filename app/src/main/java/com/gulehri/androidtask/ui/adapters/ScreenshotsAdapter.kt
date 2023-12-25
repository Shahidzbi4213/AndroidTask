package com.gulehri.androidtask.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.gulehri.androidtask.R
import com.gulehri.androidtask.ads.NativeHelper
import com.gulehri.androidtask.databinding.NativeAdsContainerBinding
import com.gulehri.androidtask.databinding.SingleImageBinding
import com.gulehri.androidtask.model.Image
import com.gulehri.androidtask.utils.Extensions.hide
import com.gulehri.androidtask.utils.Extensions.show

class ScreenshotsAdapter(private val onItemClick: (Image, View) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var itemList: MutableList<Image> = mutableListOf()

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
                val binding = NativeAdsContainerBinding.inflate(inflater, parent, false)
                AdViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ImageViewHolder -> {

                holder.bind(image = itemList[position])
            }

            is AdViewHolder -> {
                holder.bind()
            }
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
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
    inner class ImageViewHolder(private val binding: SingleImageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(image: Image) {

            //LoadReal Image
            // binding.img.load(image.path)

            binding.img.load(R.drawable.cat) {
                transformations(RoundedCornersTransformation(10f))
            }

            binding.btnMenu.setOnClickListener {
                onItemClick(image, it)
            }
        }
    }

    // ViewHolder for ad items
    class AdViewHolder(private val binding: NativeAdsContainerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            if (NativeHelper.adMobNativeAd != null) {

                val nativeHelper = NativeHelper(binding.root.context)
                nativeHelper.populateUnifiedNativeAdView(
                    NativeHelper.adMobNativeAd,
                    binding.nativeContainer,
                    binding.adPlaceHolder, 450
                ) {}

                binding.nativeContainer.show()

            } /*else {
                binding.root.hide()
            }*/
        }
    }

}