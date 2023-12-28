package com.gulehri.androidtask.ui.adapters

import android.content.Context
import android.provider.Settings.Global.getString
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.gulehri.androidtask.R
import com.gulehri.androidtask.ads.ExtraSmallNativeAdsHelper
import com.gulehri.androidtask.databinding.NativeAdsContainerBinding
import com.gulehri.androidtask.databinding.SingleImageBinding
import com.gulehri.androidtask.model.Image

class ScreenshotsAdapter(private val context: Context, private val onItemClick: (Image, View) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var itemList: MutableList<Image> = mutableListOf()


      companion object{
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
        when(holder){
            is ImageViewHolder ->{
                holder.bind(image = itemList[position])
            }
            is AdViewHolder ->{
                holder.bind()
            }
        }

    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun getItemViewType(position: Int): Int {
        Log.d("getItemViewType", "")
        return itemList[position].view_type


    }



    fun updateData(newList: MutableList<Image>) {
        itemList.clear()
        itemList = newList
        notifyDataSetChanged()
    }

    // ViewHolder for image items
    inner class ImageViewHolder(private val binding: SingleImageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(image: Image) {

            //LoadReal Image
            // binding.img.load(image.path)

            binding.img.load(image.path) {
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

            ExtraSmallNativeAdsHelper(binding.root.context).setNativeAdSmall(
                binding.nativeAdIncludeSmall.splashShimmer,
                binding.nativeAdIncludeSmall.adFrame,
                R.layout.extra_small_native,
            )
//                val nativeHelper = NativeHelper(binding.root.context)
//                nativeHelper.populateUnifiedNativeAdView(
//                    NativeHelper.adMobNativeAd,
//                    binding.nativeContainer,
//                    binding.adPlaceHolder, 110
//                ) {}
//
//                binding.nativeContainer.show()


        }
    }

}