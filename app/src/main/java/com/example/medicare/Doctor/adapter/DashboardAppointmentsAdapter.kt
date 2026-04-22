package com.example.medicare.Doctor.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.medicare.Doctor.model.AppointmentModel
import com.example.medicare.R
import com.example.medicare.databinding.ItemDashboardAppointmentBinding

class DashboardAppointmentsAdapter(private var appointments: List<AppointmentModel>) :
    RecyclerView.Adapter<DashboardAppointmentsAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemDashboardAppointmentBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDashboardAppointmentBinding.inflate(
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

            // Status Styling (only "Accepted" expected here as per filter logic)
            updateStatusUI(this, appointment.status)
        }
    }

    private fun updateStatusUI(binding: ItemDashboardAppointmentBinding, status: String) {
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