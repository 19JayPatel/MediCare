package com.example.medicare.Patient.activities

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.medicare.R
import com.google.android.material.button.MaterialButton

class BookingConfirmationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_confirmation)

        val doctorName = intent.getStringExtra("doctorName")
        val selectedDate = intent.getStringExtra("selectedDate")
        val selectedTime = intent.getStringExtra("selectedTime")

        findViewById<TextView>(R.id.tvDoctorName).text = doctorName
        findViewById<TextView>(R.id.tvDateTime).text = "$selectedDate | $selectedTime"

        findViewById<MaterialButton>(R.id.btnDone).setOnClickListener {
            finish()
        }
    }
}
