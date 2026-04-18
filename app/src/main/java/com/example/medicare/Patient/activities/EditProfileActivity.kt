package com.example.medicare.Patient.activities

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.medicare.databinding.ActivityEditProfileBinding
import java.util.Calendar
import java.util.Locale

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupClickListeners()
    }

    private fun setupUI() {
        // Pre-fill with some data for demonstration
        binding.etFullName.setText("Ananya Singh")
        binding.tvDob.text = "Mar 15, 1995"
        binding.toggleGroupGender.check(binding.btnFemale.id)
        binding.etEmail.setText("ananya.singh@gmail.com")
        binding.etPhone.setText("+91 98765 43210")
        binding.etAddress.setText("New Delhi, India")
    }

    private fun setupClickListeners() {
        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.layoutDob.setOnClickListener {
            showDatePicker()
        }

        binding.btnSave.setOnClickListener {
            Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT).show()
            finish()
        }

        binding.btnEditPhoto.setOnClickListener {
            Toast.makeText(this, "Change Photo Clicked", Toast.LENGTH_SHORT).show()
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
