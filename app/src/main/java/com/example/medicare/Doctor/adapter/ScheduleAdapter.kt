package com.example.medicare.Doctor.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.medicare.Doctor.model.ScheduleModel
import com.example.medicare.databinding.ItemScheduleBinding

class ScheduleAdapter(
    private var schedules: List<ScheduleModel>,
    private val onEditClick: (ScheduleModel) -> Unit,
    private val onDeleteClick: (ScheduleModel) -> Unit
) : RecyclerView.Adapter<ScheduleAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemScheduleBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemScheduleBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val schedule = schedules[position]
        holder.binding.apply {
            tvDayName.text = schedule.day
            tvSlotDetails.text = "${schedule.startTime} – ${schedule.endTime} · ${schedule.slotDuration}"

            btnEdit.setOnClickListener {
                onEditClick(schedule)
            }

            btnDelete.setOnClickListener {
                onDeleteClick(schedule)
            }
        }
    }

    override fun getItemCount() = schedules.size

    fun updateData(newSchedules: List<ScheduleModel>) {
        schedules = newSchedules
        notifyDataSetChanged()
    }
}
