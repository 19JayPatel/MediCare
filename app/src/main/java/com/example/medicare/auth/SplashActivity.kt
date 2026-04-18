package com.example.medicare.auth

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.medicare.auth.OnboardingScreen01Activity
import com.example.medicare.R

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val logoCircle = findViewById<FrameLayout>(R.id.logoCircle)
        val zoomInAnim = AnimationUtils.loadAnimation(this, R.anim.zoom_in)
        logoCircle.startAnimation(zoomInAnim)

        // Splash screen delay, then go to Onboarding 1
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, OnboardingScreen01Activity::class.java))
            finish()
        }, 2500)
    }
}