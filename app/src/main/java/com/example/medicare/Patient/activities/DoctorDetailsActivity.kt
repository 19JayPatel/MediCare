package com.example.medicare.Patient.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.medicare.R
import com.example.medicare.Patient.models.DoctorModel
import com.example.medicare.databinding.ActivityDoctorDetailsBinding
import com.google.firebase.database.*

class DoctorDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDoctorDetailsBinding
    private lateinit var database: DatabaseReference
    private var doctorUid: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoctorDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        doctorUid = intent.getStringExtra("doctorUid")
        database = FirebaseDatabase.getInstance().reference

        if (doctorUid == null) {
            Toast.makeText(this, "Doctor ID not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        fetchDoctorDetails()

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun fetchDoctorDetails() {
        val uid = doctorUid ?: return

        database.child("Doctors").child(uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val doctor = snapshot.getValue(DoctorModel::class.java)
                    doctor?.let {
                        // Map Firebase fields to UI using View Binding
                        binding.tvDoctorName.text = "Dr. ${it.fullName}"
                        binding.tvSpecialization.text = it.specialty
                        binding.tvHospitalChip.text = it.clinicName
                        binding.tvExpChip.text = "${it.experience} Yrs Exp"
                        binding.tvRatingChip.text = "★ ${it.rating}"

                        // Stats section
                        binding.tvExpYears.text = it.experience
                        binding.tvRatingStats.text = it.rating
                        binding.tvAbout.text = it.about

                        // Working Hours - If empty in Doctor node, fetch from DoctorSchedules
                        if (!it.availableDays.isNullOrEmpty()) {
                            binding.tvWorkingDays.text = it.availableDays
                        }
                        if (!it.startTime.isNullOrEmpty() && !it.endTime.isNullOrEmpty()) {
                            binding.tvWorkingHoursTime.text = "${it.startTime} - ${it.endTime}"
                        }
                        
                        fetchDoctorSchedule(uid)
                        
                        // Load Doctor Image using Glide
                        Glide.with(this@DoctorDetailsActivity)
                            .load(it.imageUrl)
                            .placeholder(R.drawable.ic_user)
                            .error(R.drawable.ic_user)
                            .into(binding.ivDoctorProfile)

                        // Handle Book Appointment Click
                        binding.btnBookAppointment.setOnClickListener { _ ->
                            val intent = Intent(
                                this@DoctorDetailsActivity,
                                SelectTimeSlotActivity::class.java
                            ).apply {
                                putExtra("doctorUid", it.doctorUid)
                                putExtra("doctorName", "Dr. ${it.fullName}")
                                putExtra("specialty", it.specialty)
                                putExtra("clinicName", it.clinicName)
                            }
                            startActivity(intent)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@DoctorDetailsActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun fetchDoctorSchedule(uid: String) {
        database.child("DoctorSchedules").child(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val days = mutableListOf<String>()
                        var startTime: String? = null
                        var endTime: String? = null

                        for (child in snapshot.children) {
                            val day = child.child("day").getValue(String::class.java)
                            val start = child.child("startTime").getValue(String::class.java)
                            val end = child.child("endTime").getValue(String::class.java)

                            day?.let { days.add(it.take(3)) }
                            if (startTime == null && !start.isNullOrEmpty()) startTime = start
                            if (endTime == null && !end.isNullOrEmpty()) endTime = end
                        }

                        if (days.isNotEmpty()) {
                            binding.tvWorkingDays.text = days.distinct().joinToString(", ")
                        }
                        if (!startTime.isNullOrEmpty() && !endTime.isNullOrEmpty()) {
                            binding.tvWorkingHoursTime.text = "$startTime - $endTime"
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }
}
