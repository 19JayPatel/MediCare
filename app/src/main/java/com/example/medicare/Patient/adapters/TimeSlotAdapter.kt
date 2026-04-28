package com.example.medicare.Patient.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.medicare.Patient.model.TimeSlotModel
import com.example.medicare.R
import com.google.android.material.card.MaterialCardView

class TimeSlotAdapter(
    private var slots: List<TimeSlotModel>,
    private val onSlotSelected: (TimeSlotModel) -> Unit
) : RecyclerView.Adapter<TimeSlotAdapter.SlotViewHolder>() {

    private var selectedPosition = -1

    class SlotViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardSlot: MaterialCardView = view.findViewById(R.id.cardSlot)
        val tvTime: TextView = view.findViewById(R.id.tvTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlotViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_time_slot, parent, false)
        return SlotViewHolder(view)
    }

    override fun onBindViewHolder(holder: SlotViewHolder, position: Int) {
        val slot = slots[position]
        holder.tvTime.text = slot.time

        val context = holder.itemView.context
        if (!slot.isAvailable) {
            holder.cardSlot.setCardBackgroundColor(ContextCompat.getColor(context, R.color.red_light))
            holder.tvTime.setTextColor(ContextCompat.getColor(context, R.color.text_tertiary))
            holder.cardSlot.strokeColor = ContextCompat.getColor(context, R.color.border)
            holder.cardSlot.isEnabled = false
        } else if (selectedPosition == position) {
            holder.cardSlot.setCardBackgroundColor(ContextCompat.getColor(context, R.color.accent))
            holder.tvTime.setTextColor(ContextCompat.getColor(context, R.color.white))
            holder.cardSlot.strokeColor = ContextCompat.getColor(context, R.color.accent)
            holder.cardSlot.isEnabled = true
        } else {
            holder.cardSlot.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white))
            holder.tvTime.setTextColor(ContextCompat.getColor(context, R.color.text_primary))
            holder.cardSlot.strokeColor = ContextCompat.getColor(context, R.color.border)
            holder.cardSlot.isEnabled = true
        }

        holder.itemView.setOnClickListener {
            if (slot.isAvailable) {
                val previousSelected = selectedPosition
                selectedPosition = holder.adapterPosition
                if (previousSelected != -1) {
                    notifyItemChanged(previousSelected)
                }
                notifyItemChanged(selectedPosition)
                onSlotSelected(slot)
            }
        }
    }

    override fun getItemCount(): Int = slots.size

    fun clearSelection() {
        if (selectedPosition != -1) {
            val prev = selectedPosition
            selectedPosition = -1
            notifyItemChanged(prev)
        }
    }

    fun updateData(newSlots: List<TimeSlotModel>) {
        slots = newSlots
        selectedPosition = -1
        notifyDataSetChanged()
    }

    fun getSelectedSlot(): TimeSlotModel? {
        return if (selectedPosition != -1) slots[selectedPosition] else null
    }
}