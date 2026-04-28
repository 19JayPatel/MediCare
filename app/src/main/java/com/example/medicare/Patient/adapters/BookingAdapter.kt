package com.example.medicare.Patient.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.medicare.databinding.ItemBookingBinding

data class BookingModel(
    val date: String = "",
    val doctorName: String = "",
    val specialty: String = "",
    val clinic: String = "",
    val image: Int = 0,
    val imageName: String? = null,
    val bookingId: String = "",
    val time: String = ""
)

class BookingAdapter(
    private val list: List<BookingModel>,
    private val onCancel: (BookingModel) -> Unit,
    private val onReschedule: (BookingModel) -> Unit
) : RecyclerView.Adapter<BookingAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemBookingBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBookingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.binding.bookingDate.text = "${item.date} - ${item.time}"
        holder.binding.doctorName.text = item.doctorName
        holder.binding.doctorSpecialty.text = item.specialty
        holder.binding.clinicName.text = item.clinic
        
        if (item.image != 0) {
            holder.binding.doctorImage.setImageResource(item.image)
        } else if (item.imageName != null) {
            val resId = holder.itemView.context.resources.getIdentifier(item.imageName, "drawable", holder.itemView.context.packageName)
            if (resId != 0) holder.binding.doctorImage.setImageResource(resId)
        }

        holder.binding.btnCancel.setOnClickListener { onCancel(item) }
        holder.binding.btnReschedule.setOnClickListener { onReschedule(item) }
    }

    override fun getItemCount() = list.size
}
