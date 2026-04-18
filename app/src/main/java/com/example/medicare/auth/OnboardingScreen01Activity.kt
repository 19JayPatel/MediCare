package com.example.medicare.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.medicare.R

class OnboardingScreen01Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding_screen01)

        val getStartedBtn = findViewById<Button>(R.id.getStartedBtn)
        val skipText = findViewById<TextView>(R.id.skipText)

        getStartedBtn.setOnClickListener {
            val intent = Intent(this, OnboardingScreen02Activity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            finish()
        }

        skipText.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}