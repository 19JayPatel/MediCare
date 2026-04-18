package com.example.medicare.Admin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.medicare.Admin.model.DoctorModel
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
            ivDoctor.setImageResource(doctor.imageRes)
        }
    }

    override fun getItemCount(): Int = doctors.size
}
