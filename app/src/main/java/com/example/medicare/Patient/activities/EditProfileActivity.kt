package com.example.medicare.Patient.activities

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.medicare.databinding.ActivityEditProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.Calendar
import java.util.Locale

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private val calendar = Calendar.getInstance()
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        setupClickListeners()
        fetchUserProfile()
    }

    private fun fetchUserProfile() {
        val userId = auth.currentUser?.uid ?: return
        database.child("users").child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val fullName = snapshot.child("fullName").value as? String ?: ""
                val dob = snapshot.child("dob").value as? String ?: "Select Date"
                val gender = snapshot.child("gender").value as? String ?: ""
                val email = snapshot.child("email").value as? String ?: ""
                val phone = snapshot.child("phone").value as? String ?: ""
                val address = snapshot.child("address").value as? String ?: ""

                binding.etFullName.setText(fullName)
                binding.tvDob.text = dob
                binding.etEmail.setText(email)
                binding.etPhone.setText(phone)
                binding.etAddress.setText(address)

                when (gender) {
                    "Female" -> binding.toggleGroupGender.check(binding.btnFemale.id)
                    "Male" -> binding.toggleGroupGender.check(binding.btnMale.id)
                    "Other" -> binding.toggleGroupGender.check(binding.btnOther.id)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@EditProfileActivity, "Failed to load profile", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupClickListeners() {
        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.layoutDob.setOnClickListener {
            showDatePicker()
        }

        binding.btnSave.setOnClickListener {
            saveProfileChanges()
        }

        binding.btnEditPhoto.setOnClickListener {
            Toast.makeText(this, "Change Photo Clicked", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveProfileChanges() {
        val userId = auth.currentUser?.uid ?: return
        val fullName = binding.etFullName.text.toString().trim()
        val dob = binding.tvDob.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val address = binding.etAddress.text.toString().trim()
        
        val gender = when (binding.toggleGroupGender.checkedButtonId) {
            binding.btnFemale.id -> "Female"
            binding.btnMale.id -> "Male"
            binding.btnOther.id -> "Other"
            else -> ""
        }

        if (fullName.isEmpty()) {
            Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        val userUpdates = mapOf(
            "fullName" to fullName,
            "dob" to dob,
            "email" to email,
            "phone" to phone,
            "address" to address,
            "gender" to gender
        )

        database.child("users").child(userId).updateChildren(userUpdates)
            .addOnSuccessListener {
                Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                
                val sdf = java.text.SimpleDateFormat("MMM dd, yyyy", Locale.US)
                binding.tvDob.text = sdf.format(calendar.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }
}
