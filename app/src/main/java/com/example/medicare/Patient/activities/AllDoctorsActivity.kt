package com.example.medicare.Patient.activities

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medicare.R
import com.example.medicare.databinding.ActivitySearchDoctorBinding
import com.example.medicare.databinding.ItemDoctorListBinding
import com.google.firebase.database.*

class AllDoctorsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchDoctorBinding
    private lateinit var database: DatabaseReference
    private val doctorList = mutableListOf<DoctorListModel>()
    private lateinit var adapter: DoctorListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchDoctorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance().reference

        binding.backBtn.setOnClickListener { finish() }

        setupDoctorList()
        fetchDoctorsFromFirebase()
    }

    private fun setupDoctorList() {
        adapter = DoctorListAdapter(doctorList) { doctor ->
            val intent = Intent(this, DoctorDetailsActivity::class.java)
            intent.putExtra("doctorUid", doctor.doctorUid)
            startActivity(intent)
        }
        binding.doctorRecycler.layoutManager = LinearLayoutManager(this)
        binding.doctorRecycler.adapter = adapter
    }

    private fun fetchDoctorsFromFirebase() {
        // Using addListenerForSingleValueEvent to load data once (static feel)
        database.child("Doctors").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                doctorList.clear()
                for (postSnapshot in snapshot.children) {
                    try {
                        val doctor = postSnapshot.getValue(DoctorListModel::class.java)
                        doctor?.let {
                            it.doctorUid = postSnapshot.key ?: ""
                            doctorList.add(it)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                if (doctorList.isEmpty()) {
                    // Static fallback data if Firebase is empty or fails
                    doctorList.addAll(
                        arrayListOf(
                            DoctorListModel(
                                "Jay Virani",
                                "Cardiologist",
                                "Cardiology Center, USA",
                                "5.0",
                                imageUrl = "",
                                image = R.drawable.ic_doctor1
                            ),
                            DoctorListModel(
                                "Dharti Patel",
                                "Gynecologist",
                                "Women's Clinic, Seattle, USA",
                                "4.9",
                                imageUrl = "",
                                image = R.drawable.ic_doctor2
                            ),
                            DoctorListModel(
                                "Jatin Patel",
                                "Orthopedic Surgery",
                                "Maple Associates, NY, USA",
                                "4.7",
                                imageUrl = "",
                                image = R.drawable.ic_doctor3
                            )
                        )
                    )
                }

                binding.resultText.text = "${doctorList.size} founds"
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@AllDoctorsActivity,
                    "Failed to load doctors",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}

// Model fields updated to match your Firebase JSON structure exactly
data class DoctorListModel(
    var fullName: String = "",
    var specialty: String = "",
    var clinicAddress: String = "",
    var rating: String = "0.0", // Changed to String to fix the Double conversion crash
    var imageUrl: String = "",
    var doctorUid: String = "",
    var image: Int = 0 // For local static data
)

class DoctorListAdapter(
    private val list: List<DoctorListModel>,
    private val onClick: (DoctorListModel) -> Unit
) : RecyclerView.Adapter<DoctorListAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemDoctorListBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemDoctorListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.binding.doctorName.text = "Dr. ${item.fullName}"
        holder.binding.doctorSpecialty.text = item.specialty
        holder.binding.doctorLocation.text = item.clinicAddress

        // Convert string rating to double for star logic
        val ratingValue = item.rating.toDoubleOrNull() ?: 0.0
        holder.binding.doctorRating.text = getStarString(ratingValue)
        holder.binding.doctorReviews.text = "(${item.rating})"

        if (item.imageUrl.isNotEmpty()) {
            com.bumptech.glide.Glide.with(holder.itemView.context)
                .load(item.imageUrl)
                .placeholder(R.drawable.ic_user)
                .error(R.drawable.ic_user)
                .into(holder.binding.doctorImage)
        } else if (item.image != 0) {
            holder.binding.doctorImage.setImageResource(item.image)
        }

        holder.itemView.setOnClickListener { onClick(item) }
    }

    override fun getItemCount() = list.size

    private fun getStarString(rating: Double): String {
        val stars = Math.round(rating).toInt()
        return "⭐".repeat(stars.coerceIn(0, 5))
    }
}