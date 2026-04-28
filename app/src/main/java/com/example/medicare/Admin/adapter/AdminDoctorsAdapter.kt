package com.example.medicare.Admin.adapter

import android.content.Intent
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.medicare.Admin.model.AdminDoctorModel
import com.example.medicare.Patient.activities.DoctorDetailsActivity
import com.example.medicare.R
import com.example.medicare.databinding.ItemAdminDoctorBinding

class AdminDoctorsAdapter(private var doctors: List<AdminDoctorModel>) :
    RecyclerView.Adapter<AdminDoctorsAdapter.DoctorViewHolder>() {

    class DoctorViewHolder(val binding: ItemAdminDoctorBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorViewHolder {
        val binding = ItemAdminDoctorBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DoctorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DoctorViewHolder, position: Int) {
        val doctor = doctors[position]
        holder.binding.apply {
            tvDoctorName.text = doctor.name
            tvSpecialization.text = "${doctor.specialization} · ${doctor.hospital}"
            tvTiming.text = doctor.timing
            statusBadge.text = doctor.status

            val context = root.context
            
            // Set default background and clear any previous tints
            imgDoctor.imageTintList = null
            
            // Load Doctor Image dynamically
            if (doctor.imageUrl.isNotEmpty()) {
                Glide.with(context)
                    .load(doctor.imageUrl)
                    .placeholder(R.drawable.ic_user)
                    .error(R.drawable.ic_user)
                    .centerCrop()
                    .into(imgDoctor)
            } else {
                imgDoctor.setImageResource(R.drawable.ic_user)
                // Apply tint only for the placeholder icon
                if (doctor.status == "Active") {
                    imgDoctor.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.success))
                } else {
                    imgDoctor.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.primary))
                }
            }

            if (doctor.status == "Active") {
                // Status Badge
                statusBadge.setTextColor(ContextCompat.getColor(context, R.color.success))
                statusBadge.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green_light))
                
                // Icon Background
                imgDoctor.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green_light))
            } else {
                // Status Badge
                statusBadge.setTextColor(ContextCompat.getColor(context, R.color.text_tertiary))
                statusBadge.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.border))
                
                // Icon Background
                imgDoctor.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.blue_light))
            }

            // Click listener to open DoctorDetailsActivity
            root.setOnClickListener {
                val intent = Intent(context, DoctorDetailsActivity::class.java)
                intent.putExtra("doctor_id", doctor.id)
                context.startActivity(intent)
            }
        }
    }

    fun updateList(newList: List<AdminDoctorModel>) {
        doctors = newList
        notifyDataSetChanged()
    }

    override fun getItemCount() = doctors.size
}
