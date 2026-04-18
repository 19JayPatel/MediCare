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
import com.example.medicare.Patient.activities.MainActivity
import com.example.medicare.R
import com.example.medicare.auth.SignupActivity

class LoginActivity : AppCompatActivity() {

    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

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

        // Simple Login Logic (No Database)
        getStartedBtn.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                // Successful Login Simulation
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        // Navigate to Signup
        bottomText.setOnClickListener {
             startActivity(Intent(this, SignupActivity::class.java))
             finish()
        }
    }
}