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
import com.example.medicare.Admin.activities.AdminMainActivity
import com.example.medicare.Doctor.activities.DoctorMainActivity
import com.example.medicare.Patient.activities.MainActivity
import com.example.medicare.R
import com.example.medicare.utils.SessionManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {

    private var isPasswordVisible = false
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        sessionManager = SessionManager(this)

        val appLogoText = findViewById<TextView>(R.id.appLogoText)
        val emailInput = findViewById<EditText>(R.id.emailInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val passwordToggle = findViewById<ImageView>(R.id.passwordToggle)
        val getStartedBtn = findViewById<Button>(R.id.getStartedBtn)
        val bottomText = findViewById<TextView>(R.id.bottomText)

        // Set Logo Text (Matching SignupActivity style)
        val logoText = "<font color='#81BD46'>Medi</font><font color='#2A6F97'>Care</font>"
        appLogoText.text = Html.fromHtml(logoText, Html.FROM_HTML_MODE_LEGACY)

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

        // Firebase Login Logic
        getStartedBtn.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val userId = auth.currentUser?.uid
                            userId?.let { uid ->
                                database.reference.child("users").child(uid).child("role").get()
                                    .addOnSuccessListener { snapshot ->
                                        val role = snapshot.value as? String ?: "Patient"
                                        
                                        // Save Session
                                        sessionManager.saveLoginSession(email, role)
                                        
                                        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                                        navigateToMain(role)
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(this, "Error fetching user role", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        } else {
                            Toast.makeText(this, "Login Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        // Navigate to Signup
        bottomText.setOnClickListener {
             startActivity(Intent(this, SignupActivity::class.java))
             finish()
        }
    }

    private fun navigateToMain(role: String) {
        val intent = when (role) {
            "Patient" -> Intent(this, MainActivity::class.java)
            "Doctor" -> Intent(this, DoctorMainActivity::class.java)
            "Admin" -> Intent(this, AdminMainActivity::class.java)
            else -> Intent(this, MainActivity::class.java)
        }
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
