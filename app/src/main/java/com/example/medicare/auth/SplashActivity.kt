package com.example.medicare.auth

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.medicare.Admin.activities.AdminMainActivity
import com.example.medicare.Doctor.activities.DoctorMainActivity
import com.example.medicare.Patient.activities.MainActivity
import com.example.medicare.R
import com.example.medicare.utils.SessionManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SplashActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        auth = FirebaseAuth.getInstance()
        sessionManager = SessionManager(this)

        val logoCircle = findViewById<FrameLayout>(R.id.logoCircle)
        val zoomInAnim = AnimationUtils.loadAnimation(this, R.anim.zoom_in)
        logoCircle.startAnimation(zoomInAnim)

        // Splash screen delay
        Handler(Looper.getMainLooper()).postDelayed({
            checkSession()
        }, 2500)
    }

    private fun checkSession() {
        val currentUser = auth.currentUser
        if (currentUser != null && sessionManager.isLoggedIn()) {
            // User is logged in, check role from session or Firebase
            val role = sessionManager.getUserRole()
            if (role != null) {
                navigateToDashboard(role)
            } else {
                // Fallback: Fetch role from Firebase if not in session
                fetchRoleAndNavigate(currentUser.uid)
            }
        } else {
            // No session, go to Onboarding or Login
            startActivity(Intent(this, OnboardingScreen01Activity::class.java))
            finish()
        }
    }

    private fun fetchRoleAndNavigate(uid: String) {
        val database = FirebaseDatabase.getInstance().reference
        database.child("users").child(uid).child("role").get()
            .addOnSuccessListener { snapshot ->
                val role = snapshot.value as? String ?: "Patient"
                sessionManager.saveLoginSession(auth.currentUser?.email ?: "", role)
                navigateToDashboard(role)
            }
            .addOnFailureListener {
                // On failure, default to Login
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
    }

    private fun navigateToDashboard(role: String) {
        val intent = when (role) {
            "Patient" -> Intent(this, MainActivity::class.java)
            "Doctor" -> Intent(this, DoctorMainActivity::class.java)
            "Admin" -> Intent(this, AdminMainActivity::class.java)
            else -> Intent(this, MainActivity::class.java)
        }
        startActivity(intent)
        finish()
    }
}
