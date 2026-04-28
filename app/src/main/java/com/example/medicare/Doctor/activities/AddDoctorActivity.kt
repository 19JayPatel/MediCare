package com.example.medicare.Doctor.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.medicare.Doctor.model.DoctorModel
import com.example.medicare.Doctor.utils.FirebaseDoctorHelper
import com.example.medicare.databinding.ActivityAddDoctorBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AddDoctorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddDoctorBinding
    private val doctorHelper = FirebaseDoctorHelper()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddDoctorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        binding.btnAddDoctor.setOnClickListener {
            validateAndSave()
        }
    }

    private fun validateAndSave() {
        val fullName = binding.etFullName.text.toString().trim()
        val designation = binding.etDesignation.text.toString().trim()
        val specialty = binding.etSpecialty.text.toString().trim()
        val experience = binding.etExperience.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val clinicName = binding.etClinicName.text.toString().trim()
        val clinicAddress = binding.etClinicAddress.text.toString().trim()
        val consultationFee = binding.etConsultationFee.text.toString().trim()
        val about = binding.etAbout.text.toString().trim()
        val imageUrl = binding.etImageUrl.text.toString().trim()

        // Validating only the remaining fields
        if (fullName.isEmpty() || designation.isEmpty() || specialty.isEmpty() || experience.isEmpty() ||
            phone.isEmpty() || email.isEmpty() || clinicName.isEmpty() || clinicAddress.isEmpty() ||
            consultationFee.isEmpty() || about.isEmpty() || imageUrl.isEmpty()
        ) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        binding.btnAddDoctor.isEnabled = false

        val doctorId = FirebaseDatabase.getInstance().getReference("Doctors").push().key ?: ""
        val doctorUid = auth.currentUser?.uid ?: ""

        val doctor = DoctorModel(
            doctorUid = doctorUid,
            doctorId = doctorId,
            fullName = fullName,
            doctorName = fullName,
            designation = designation,
            specialty = specialty,
            experience = experience,
            phone = phone,
            email = email,
            clinicName = clinicName,
            clinicAddress = clinicAddress,
            consultationFee = consultationFee,
            about = about,
            imageUrl = imageUrl,
            rating = "4.5",
            role = "doctor",
            createdAt = System.currentTimeMillis()
        )

        doctorHelper.saveDoctorProfile(doctor) { success, error ->
            binding.progressBar.visibility = View.GONE
            binding.btnAddDoctor.isEnabled = true

            if (success) {
                Toast.makeText(this, "Doctor Added Successfully", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error: $error", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
