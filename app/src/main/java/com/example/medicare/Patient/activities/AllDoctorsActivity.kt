package com.example.medicare.Patient.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medicare.R
import com.example.medicare.databinding.ActivityAllDoctorsBinding
import com.example.medicare.databinding.ItemDoctorListBinding

class AllDoctorsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAllDoctorsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllDoctorsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtn.setOnClickListener { finish() }

        setupDoctorList()
    }

    private fun setupDoctorList() {
        val doctors = arrayListOf(
            DoctorListModel("Dr. Jay Virani", "Cardiologist", "Cardiology Center, USA", 5.0, 1872, R.drawable.ic_doctor1),
            DoctorListModel("Dr. Dharti Patel", "Gynecologist", "Women's Clinic, Seattle, USA", 4.9, 127, R.drawable.ic_doctor2),
            DoctorListModel("Dr. Jatin Patel", "Orthopedic Surgery", "Maple Associates, NY, USA", 4.7, 5223, R.drawable.ic_doctor3),
            DoctorListModel("Dr. Tanvi Desai", "Pediatrics", "Serenity Pediatrics Clinic", 5.0, 405, R.drawable.ic_doctor4),
            DoctorListModel("Dr. Emily Walker", "Pediatrics", "Serenity Pediatrics Clinic", 4.8, 230, R.drawable.ic_doctor1)
        )

        binding.doctorRecycler.layoutManager = LinearLayoutManager(this)
        binding.doctorRecycler.adapter = DoctorListAdapter(doctors) { doctor ->
            Toast.makeText(this, "Clicked: ${doctor.name}", Toast.LENGTH_SHORT).show()
        }
    }
}

data class DoctorListModel(
    val name: String,
    val specialty: String,
    val location: String,
    val rating: Double,
    val reviews: Int,
    val image: Int
)

class DoctorListAdapter(
    private val list: List<DoctorListModel>,
    private val onClick: (DoctorListModel) -> Unit
) : RecyclerView.Adapter<DoctorListAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemDoctorListBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDoctorListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.binding.doctorName.text = item.name
        holder.binding.doctorSpecialty.text = item.specialty
        holder.binding.doctorLocation.text = item.location
        holder.binding.doctorRating.text = getStarString(item.rating)
        holder.binding.doctorReviews.text = "${item.reviews} Reviews"
        holder.binding.doctorImage.setImageResource(item.image)
        holder.itemView.setOnClickListener { onClick(item) }
    }

    override fun getItemCount() = list.size

    private fun getStarString(rating: Double): String {
        val stars = Math.round(rating).toInt()
        return "⭐".repeat(stars.coerceIn(0, 5))
    }
}
