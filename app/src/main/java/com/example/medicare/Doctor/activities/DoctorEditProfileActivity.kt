package com.example.medicare.Doctor.activities

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.medicare.Doctor.model.DoctorModel
import com.example.medicare.R
import com.example.medicare.databinding.ActivityDoctorEditProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class DoctorEditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDoctorEditProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private var selectedImageUri: Uri? = null
    private var currentImageUrl: String = ""

    // Image Picker Launcher
    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            binding.ivProfileImage.setImageURI(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoctorEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        setupClickListeners()
        fetchDoctorData()
    }

    private fun setupClickListeners() {
        binding.ivBack.setOnClickListener { finish() }

        binding.btnEditPhoto.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        binding.btnUpdateProfile.setOnClickListener {
            validateAndUpdate()
        }
    }

    private fun fetchDoctorData() {
        val doctorUid = auth.currentUser?.uid ?: return
        binding.progressBar.visibility = View.VISIBLE

        database.child("Doctors").child(doctorUid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.progressBar.visibility = View.GONE
                val doctor = snapshot.getValue(DoctorModel::class.java)
                doctor?.let {
                    prefillFields(it)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@DoctorEditProfileActivity, "Failed to load data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun prefillFields(doctor: DoctorModel) {
        binding.apply {
            etFullName.setText(doctor.fullName)
            etDesignation.setText(doctor.designation)
            etSpecialty.setText(doctor.specialty)
            etExperience.setText(doctor.experience)
            etAbout.setText(doctor.about)
            etPhone.setText(doctor.phone)
            etEmail.setText(doctor.email)
            etClinicName.setText(doctor.clinicName)
            etClinicAddress.setText(doctor.clinicAddress)
            etConsultationFee.setText(doctor.consultationFee)
            
            currentImageUrl = doctor.imageUrl
            if (currentImageUrl.isNotEmpty()) {
                Glide.with(this@DoctorEditProfileActivity)
                    .load(currentImageUrl)
                    .placeholder(R.drawable.ic_person)
                    .into(ivProfileImage)
            }
        }
    }

    private fun validateAndUpdate() {
        val fullName = binding.etFullName.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val specialty = binding.etSpecialty.text.toString().trim()

        if (fullName.isEmpty()) {
            binding.etFullName.error = "Name required"
            return
        }
        if (phone.isEmpty()) {
            binding.etPhone.error = "Phone required"
            return
        }
        if (specialty.isEmpty()) {
            binding.etSpecialty.error = "Specialty required"
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        binding.btnUpdateProfile.isEnabled = false

        if (selectedImageUri != null) {
            uploadImageAndUpdateProfile()
        } else {
            updateProfile(currentImageUrl)
        }
    }

    private fun uploadImageAndUpdateProfile() {
        val doctorUid = auth.currentUser?.uid ?: return
        val storageRef = FirebaseStorage.getInstance().reference
            .child("DoctorProfiles")
            .child("$doctorUid.jpg")

        selectedImageUri?.let { uri ->
            storageRef.putFile(uri)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        updateProfile(downloadUri.toString())
                    }
                }
                .addOnFailureListener {
                    binding.progressBar.visibility = View.GONE
                    binding.btnUpdateProfile.isEnabled = true
                    Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun updateProfile(imageUrl: String) {
        val doctorUid = auth.currentUser?.uid ?: return
        
        val updates = HashMap<String, Any>()
        updates["fullName"] = binding.etFullName.text.toString().trim()
        updates["doctorName"] = binding.etFullName.text.toString().trim()
        updates["designation"] = binding.etDesignation.text.toString().trim()
        updates["specialty"] = binding.etSpecialty.text.toString().trim()
        updates["experience"] = binding.etExperience.text.toString().trim()
        updates["about"] = binding.etAbout.text.toString().trim()
        updates["phone"] = binding.etPhone.text.toString().trim()
        updates["email"] = binding.etEmail.text.toString().trim()
        updates["clinicName"] = binding.etClinicName.text.toString().trim()
        updates["clinicAddress"] = binding.etClinicAddress.text.toString().trim()
        updates["consultationFee"] = binding.etConsultationFee.text.toString().trim()
        updates["imageUrl"] = imageUrl

        database.child("Doctors").child(doctorUid).updateChildren(updates)
            .addOnSuccessListener {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                binding.progressBar.visibility = View.GONE
                binding.btnUpdateProfile.isEnabled = true
                Toast.makeText(this, "Update failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
