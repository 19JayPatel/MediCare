package com.example.medicare.Doctor.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.medicare.Doctor.model.AppointmentModel
import com.example.medicare.R
import com.example.medicare.databinding.ItemDoctorAppointmentBinding

class DoctorAppointmentsAdapter(private var appointments: List<AppointmentModel>) :
    RecyclerView.Adapter<DoctorAppointmentsAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemDoctorAppointmentBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDoctorAppointmentBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val appointment = appointments[position]
        holder.binding.apply {
            tvPatientName.text = appointment.patientName
            tvDateTime.text = "${appointment.date} · ${appointment.time}"
            tvDescription.text = appointment.problem
            tvStatusBadge.text = appointment.status

            // Status Styling
            updateStatusUI(this, appointment.status)

            // Button visibility based on status
            if (appointment.status == "Pending") {
                llActions.visibility = View.VISIBLE
            } else {
                llActions.visibility = View.GONE
            }

            btnAccept.setOnClickListener {
                appointment.status = "Accepted"
                notifyItemChanged(position)
            }

            btnReject.setOnClickListener {
                appointment.status = "Rejected"
                notifyItemChanged(position)
            }
        }
    }

    private fun updateStatusUI(binding: ItemDoctorAppointmentBinding, status: String) {
        val context = binding.root.context
        when (status) {
            "Accepted" -> {
                binding.tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.success))
                binding.tvStatusBadge.setBackgroundColor(ContextCompat.getColor(context, R.color.green_light))
            }
            "Rejected" -> {
                binding.tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.error))
                binding.tvStatusBadge.setBackgroundColor(ContextCompat.getColor(context, R.color.red_light))
            }
            else -> { // Pending
                binding.tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.warning))
                binding.tvStatusBadge.setBackgroundColor(ContextCompat.getColor(context, R.color.yellow_light))
            }
        }
    }

    override fun getItemCount() = appointments.size
}