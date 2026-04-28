package com.example.medicare.Doctor.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.medicare.Patient.models.AppointmentModel
import com.example.medicare.R
import com.example.medicare.databinding.ItemDoctorAppointmentBinding

class DoctorAppointmentsAdapter(
    private var appointments: List<AppointmentModel>,
    private val onAction: (AppointmentModel, String) -> Unit
) : RecyclerView.Adapter<DoctorAppointmentsAdapter.ViewHolder>() {

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
            tvDateTime.text = "${appointment.appointmentDate} · ${appointment.appointmentTime}"
            tvDescription.text = "Specialty: ${appointment.doctorSpecialty}"
            tvStatusBadge.text = appointment.status

            // Status Styling
            updateStatusUI(this, appointment.status)

            // Button visibility (Only Upcoming items show Complete/Cancel buttons)
            if (appointment.status == "Upcoming") {
                llActions.visibility = View.VISIBLE
                divider.visibility = View.VISIBLE
            } else {
                llActions.visibility = View.GONE
                divider.visibility = View.GONE
            }

            // In your XML, btnAccept is used for "Complete", btnReject for "Cancel"
            btnAccept.text = "Complete"
            btnAccept.setOnClickListener {
                onAction(appointment, "Completed")
            }

            btnReject.text = "Cancel"
            btnReject.setOnClickListener {
                onAction(appointment, "Cancelled")
            }
        }
    }

    private fun updateStatusUI(binding: ItemDoctorAppointmentBinding, status: String) {
        val context = binding.root.context
        when (status) {
            "Completed" -> {
                binding.tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.success))
                binding.tvStatusBadge.setBackgroundColor(ContextCompat.getColor(context, R.color.green_light))
            }
            "Cancelled" -> {
                binding.tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.error))
                binding.tvStatusBadge.setBackgroundColor(ContextCompat.getColor(context, R.color.red_light))
            }
            "Upcoming" -> {
                binding.tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.primary))
                binding.tvStatusBadge.setBackgroundColor(ContextCompat.getColor(context, R.color.blue_light))
            }
            else -> {
                binding.tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.warning))
                binding.tvStatusBadge.setBackgroundColor(ContextCompat.getColor(context, R.color.yellow_light))
            }
        }
    }

    override fun getItemCount() = appointments.size
}
