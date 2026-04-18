package com.example.medicare.Admin.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.medicare.Admin.model.DoctorModel
import com.example.medicare.R
import com.example.medicare.databinding.ItemDoctorBinding

class DoctorsAdapter(private val doctors: List<DoctorModel>) :
    RecyclerView.Adapter<DoctorsAdapter.DoctorViewHolder>() {

    class DoctorViewHolder(val binding: ItemDoctorBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorViewHolder {
        val binding = ItemDoctorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DoctorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DoctorViewHolder, position: Int) {
        val doctor = doctors[position]
        holder.binding.apply {
            tvDoctorName.text = doctor.name
            tvSpecialization.text = "${doctor.specialization} · ${doctor.hospital}"
            tvTiming.text = doctor.timing
            ivDoctor.setImageResource(doctor.imageRes)
            tvStatus.text = doctor.status

            if (doctor.status == "Active") {
                tvStatus.setTextColor(ContextCompat.getColor(root.context, R.color.success))
                tvStatus.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(root.context, R.color.green_light))
            } else {
                tvStatus.setTextColor(ContextCompat.getColor(root.context, R.color.text_secondary))
                tvStatus.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(root.context, R.color.border))
            }
        }
    }

    override fun getItemCount(): Int = doctors.size
}
