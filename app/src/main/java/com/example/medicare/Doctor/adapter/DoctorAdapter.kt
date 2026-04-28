package com.example.medicare.Doctor.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.medicare.Doctor.model.DoctorModel
import com.example.medicare.R
import com.example.medicare.databinding.ItemDoctorBinding

class DoctorAdapter(private var doctorList: List<DoctorModel>) :
    RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder>() {

    class DoctorViewHolder(val binding: ItemDoctorBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorViewHolder {
        val binding = ItemDoctorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DoctorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DoctorViewHolder, position: Int) {
        val doctor = doctorList[position]
        holder.binding.apply {
            tvDoctorName.text = doctor.fullName
            tvSpecialization.text = doctor.specialty
            tvTiming.text = "${doctor.startTime} - ${doctor.endTime}"
            tvStatus.text = doctor.designation
            
            Glide.with(holder.itemView.context)
                .load(doctor.imageUrl)
                .placeholder(R.drawable.ic_doctor1)
                .error(R.drawable.ic_doctor1)
                .into(ivDoctor)
        }
    }

    override fun getItemCount(): Int = doctorList.size

    fun updateList(newList: List<DoctorModel>) {
        doctorList = newList
        notifyDataSetChanged()
    }
}
