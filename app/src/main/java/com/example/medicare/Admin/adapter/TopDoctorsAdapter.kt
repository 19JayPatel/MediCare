package com.example.medicare.Admin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.medicare.Admin.model.DoctorModel
import com.example.medicare.R
import com.example.medicare.databinding.ItemTopDoctorBinding

class TopDoctorsAdapter(private val doctors: List<DoctorModel>) :
    RecyclerView.Adapter<TopDoctorsAdapter.DoctorViewHolder>() {

    class DoctorViewHolder(val binding: ItemTopDoctorBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorViewHolder {
        val binding = ItemTopDoctorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DoctorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DoctorViewHolder, position: Int) {
        val doctor = doctors[position]
        holder.binding.apply {
            tvDoctorName.text = doctor.name
            tvSpecialization.text = "${doctor.specialization} · ${doctor.hospital}"
            tvRating.text = doctor.rating.toString()
            
            if (doctor.imageUrl.isNotEmpty()) {
                Glide.with(holder.itemView.context)
                    .load(doctor.imageUrl)
                    .placeholder(R.drawable.ic_user)
                    .error(R.drawable.ic_user)
                    .into(ivDoctor)
            } else {
                ivDoctor.setImageResource(R.drawable.ic_user)
            }
        }
    }

    override fun getItemCount(): Int = doctors.size
}
