package com.example.medicare.Patient.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.medicare.Patient.adapters.Banner
import com.example.medicare.databinding.ItemBannerBinding

class BannerAdapter(private val banners: List<Banner>) :
    RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {

    inner class BannerViewHolder(val binding: ItemBannerBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val binding = ItemBannerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BannerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        val banner = banners[position % banners.size]
        holder.binding.bannerTitle.text = banner.title
        holder.binding.bannerSub.text = banner.subtitle
        holder.binding.bannerImage.setImageResource(banner.imageRes)
    }

    override fun getItemCount(): Int = if (banners.isEmpty()) 0 else Int.MAX_VALUE

    fun getRealCount(): Int = banners.size
}