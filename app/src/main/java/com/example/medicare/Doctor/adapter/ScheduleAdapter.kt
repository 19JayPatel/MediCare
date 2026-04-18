package com.example.medicare.Doctor.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.medicare.Doctor.model.ScheduleModel
import com.example.medicare.databinding.ItemScheduleBinding

class ScheduleAdapter(private val schedules: MutableList<ScheduleModel>) :
    RecyclerView.Adapter<ScheduleAdapter.ViewHolder>() {

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
                Toast.makeText(root.context, "Edit ${schedule.day}", Toast.LENGTH_SHORT).show()
            }

            btnDelete.setOnClickListener {
                schedules.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, schedules.size)
                Toast.makeText(root.context, "Deleted ${schedule.day}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount() = schedules.size
}