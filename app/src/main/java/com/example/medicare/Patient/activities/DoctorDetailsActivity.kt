package com.example.medicare.Patient.activities

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.medicare.R
import com.example.medicare.Patient.models.DoctorDetailsModel
import com.google.android.material.button.MaterialButton

class DoctorDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_details)

        // 1. Initialize Dummy Data
        val doctor = DoctorDetailsModel(
            name = "Dr. Sarah Johnson",
            specialization = "Cardiologist",
            hospitalName = "Apollo Hospital",
            experienceYears = "10",
            rating = "4.9",
            reviewCount = "284",
            patientsCount = "1,284",
            about = "Dr. Sarah Johnson is a board-certified Cardiologist with over 10 years of experience in treating complex cardiac conditions. She completed her MBBS from AIIMS and MD from Apollo Institute of Medical Sciences.",
            workingHoursMonFri = "9:00 AM - 5:00 PM",
            workingHoursSat = "10:00 AM - 2:00 PM"
        )

        // 2. Bind Views
        setupUI(doctor)

        // 3. Click Listeners
        findViewById<android.view.View>(R.id.btnBack).setOnClickListener {
            finish()
        }

        findViewById<MaterialButton>(R.id.btnBookAppointment).setOnClickListener {
            Toast.makeText(this, "Booking Appointment with ${doctor.name}...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupUI(doctor: DoctorDetailsModel) {
        findViewById<TextView>(R.id.tvDoctorName).text = doctor.name
        findViewById<TextView>(R.id.tvSpecialization).text = doctor.specialization
        findViewById<TextView>(R.id.tvHospitalChip).text = doctor.hospitalName
        findViewById<TextView>(R.id.tvExpChip).text = "${doctor.experienceYears} Yrs Exp"
        findViewById<TextView>(R.id.tvRatingChip).text = "★ ${doctor.rating} (${doctor.reviewCount})"
        
        findViewById<TextView>(R.id.tvPatientsCount).text = doctor.patientsCount
        findViewById<TextView>(R.id.tvExpYears).text = doctor.experienceYears
        findViewById<TextView>(R.id.tvRatingStats).text = doctor.rating
        
        findViewById<TextView>(R.id.tvAbout).text = doctor.about
        
        findViewById<TextView>(R.id.tvWorkingHoursMonFri).text = doctor.workingHoursMonFri
        findViewById<TextView>(R.id.tvWorkingHoursSat).text = doctor.workingHoursSat
    }
}
