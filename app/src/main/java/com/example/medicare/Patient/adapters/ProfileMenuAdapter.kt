package com.example.medicare.Patient.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.medicare.databinding.ItemProfileMenuBinding
import com.example.medicare.Patient.models.ProfileMenuItem

class ProfileMenuAdapter(private val items: List<ProfileMenuItem>) :
    RecyclerView.Adapter<ProfileMenuAdapter.MenuViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = ItemProfileMenuBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MenuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(items[position], position == items.size - 1)
    }

    override fun getItemCount(): Int = items.size

    class MenuViewHolder(private val binding: ItemProfileMenuBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ProfileMenuItem, isLast: Boolean) {
            binding.ivMenuIcon.setImageResource(item.icon)
            binding.tvMenuTitle.text = item.title
            binding.divider.visibility = if (isLast) View.GONE else View.VISIBLE
            
            binding.root.setOnClickListener {
                item.onClick()
            }
        }
    }
}
