package com.example.medicare.auth

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.medicare.Admin.activities.AdminMainActivity
import com.example.medicare.Doctor.activities.AddDoctorActivity
import com.example.medicare.Doctor.activities.DoctorMainActivity
import com.example.medicare.Patient.activities.MainActivity
import com.example.medicare.R
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class SignupActivity : AppCompatActivity() {

    private var isPasswordVisible = false
    private var selectedRole = "Patient" // Default selection
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        val appLogoText = findViewById<TextView>(R.id.appLogoText)
        val nameInput = findViewById<EditText>(R.id.nameInput)
        val emailInput = findViewById<EditText>(R.id.emailInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val passwordToggle = findViewById<ImageView>(R.id.passwordToggle)
        val getStartedBtn = findViewById<Button>(R.id.getStartedBtn)
        val loginText = findViewById<TextView>(R.id.loginText)

        // Role selection views
        val patientCard = findViewById<MaterialCardView>(R.id.patientCard)
        val doctorCard = findViewById<MaterialCardView>(R.id.doctorCard)
        val patientIcon = findViewById<ImageView>(R.id.patientIcon)
        val doctorIcon = findViewById<ImageView>(R.id.doctorIcon)

        // Set Logo Text (Fixed "Care" color for visibility)
        val logoText = "<font color='#81BD46'>Medi</font><font color='#2A6F97'>Care</font>"
        appLogoText.text = Html.fromHtml(logoText, Html.FROM_HTML_MODE_LEGACY)

        // Role Selection Logic
        patientCard.setOnClickListener {
            updateRoleSelection("Patient", patientCard, doctorCard, patientIcon, doctorIcon)
        }

        doctorCard.setOnClickListener {
            updateRoleSelection("Doctor", patientCard, doctorCard, patientIcon, doctorIcon)
        }

        // Password Visibility Toggle
        passwordToggle.setOnClickListener {
            if (isPasswordVisible) {
                passwordInput.transformationMethod = PasswordTransformationMethod.getInstance()
                passwordToggle.setImageResource(R.drawable.ic_visibility_off)
            } else {
                passwordInput.transformationMethod = HideReturnsTransformationMethod.getInstance()
                passwordToggle.setImageResource(R.drawable.ic_visibility)
            }
            isPasswordVisible = !isPasswordVisible
            passwordInput.setSelection(passwordInput.text.length)
        }

        // Firebase Signup Logic
        getStartedBtn.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val userId = auth.currentUser?.uid
                            
                            // Format current date for joinedDate
                            val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                            val currentDate = sdf.format(Date())
                            
                            val userMap = mapOf(
                                "fullName" to name,
                                "email" to email,
                                "role" to selectedRole,
                                "joinedDate" to currentDate,
                                "createdAt" to System.currentTimeMillis()
                            )

                            userId?.let {
                                database.reference.child("users").child(it).setValue(userMap)
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "Signup Successful", Toast.LENGTH_SHORT).show()
                                        navigateToMain(selectedRole)
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(this, "Database Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        } else {
                            Toast.makeText(this, "Authentication Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        // Navigate to Login
        loginText.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun navigateToMain(role: String) {
        val intent = when (role) {
            "Patient" -> Intent(this, MainActivity::class.java)
            "Doctor" -> Intent(this, AddDoctorActivity::class.java)
            "Admin" -> Intent(this, AdminMainActivity::class.java)
            else -> Intent(this, MainActivity::class.java)
        }
        startActivity(intent)
        finish()
    }

    private fun updateRoleSelection(
        role: String,
        patientCard: MaterialCardView,
        doctorCard: MaterialCardView,
        patientIcon: ImageView,
        doctorIcon: ImageView
    ) {
        selectedRole = role

        if (role == "Patient") {
            // Select Patient
            patientCard.setStrokeColor(ContextCompat.getColorStateList(this, R.color.primary))
            patientCard.setCardBackgroundColor(ContextCompat.getColorStateList(this, R.color.accent_light))
            patientCard.cardElevation = 4f * resources.displayMetrics.density
            patientIcon.setColorFilter(ContextCompat.getColor(this, R.color.primary))

            // Deselect Doctor
            doctorCard.setStrokeColor(ContextCompat.getColorStateList(this, R.color.border))
            doctorCard.setCardBackgroundColor(ContextCompat.getColorStateList(this, R.color.white))
            doctorCard.cardElevation = 2f * resources.displayMetrics.density
            doctorIcon.setColorFilter(ContextCompat.getColor(this, R.color.text_secondary))
        } else {
            // Select Doctor
            doctorCard.setStrokeColor(ContextCompat.getColorStateList(this, R.color.primary))
            doctorCard.setCardBackgroundColor(ContextCompat.getColorStateList(this, R.color.accent_light))
            doctorCard.cardElevation = 4f * resources.displayMetrics.density
            doctorIcon.setColorFilter(ContextCompat.getColor(this, R.color.primary))

            // Deselect Patient
            patientCard.setStrokeColor(ContextCompat.getColorStateList(this, R.color.border))
            patientCard.setCardBackgroundColor(ContextCompat.getColorStateList(this, R.color.white))
            patientCard.cardElevation = 2f * resources.displayMetrics.density
            patientIcon.setColorFilter(ContextCompat.getColor(this, R.color.text_secondary))
        }
    }
}
