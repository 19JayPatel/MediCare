package com.example.medicare.Patient.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.medicare.Patient.models.AppointmentModel
import com.example.medicare.R
import com.example.medicare.databinding.ItemBookingBinding

class BookingAdapter(
    private val list: List<AppointmentModel>,
    private val onCancel: (AppointmentModel) -> Unit
) : RecyclerView.Adapter<BookingAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemBookingBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBookingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        val context = holder.itemView.context
        
        holder.binding.doctorName.text = "Dr. ${item.doctorName}"
        holder.binding.doctorSpecialty.text = item.doctorSpecialty
        holder.binding.bookingDate.text = "${item.appointmentDate} - ${item.appointmentTime}"
        holder.binding.clinicName.text = item.appointmentDay
        
        // Status Badge UI - Matching Screenshot Style (Completed = Green)
        holder.binding.tvStatusBadge.text = item.status
        when (item.status) {
            "Completed" -> {
                holder.binding.tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.success))
                holder.binding.tvStatusBadge.setBackgroundResource(R.drawable.bg_search_bar)
                holder.binding.actionButtons.visibility = View.GONE
            }
            "Upcoming" -> {
                holder.binding.tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.primary))
                holder.binding.tvStatusBadge.setBackgroundResource(R.drawable.bg_search_bar)
                holder.binding.actionButtons.visibility = View.VISIBLE
            }
            "Cancelled" -> {
                holder.binding.tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.error))
                holder.binding.tvStatusBadge.setBackgroundResource(R.drawable.bg_search_bar)
                holder.binding.actionButtons.visibility = View.GONE
            }
            else -> {
                holder.binding.tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.text_secondary))
                holder.binding.tvStatusBadge.setBackgroundResource(R.drawable.bg_search_bar)
                holder.binding.actionButtons.visibility = View.GONE
            }
        }

        // Load Image using Glide
        if (item.doctorImage.isNotEmpty()) {
            Glide.with(context)
                .load(item.doctorImage)
                .placeholder(R.drawable.ic_user)
                .into(holder.binding.doctorImage)
        } else {
            holder.binding.doctorImage.setImageResource(R.drawable.ic_user)
        }

        holder.binding.btnCancel.setOnClickListener { onCancel(item) }
        holder.binding.btnReschedule.visibility = View.GONE
    }

    override fun getItemCount() = list.size
}
