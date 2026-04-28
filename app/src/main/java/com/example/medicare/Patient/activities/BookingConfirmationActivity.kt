package com.example.medicare.Patient.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.medicare.Patient.models.AppointmentModel
import com.example.medicare.R
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class BookingConfirmationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_confirmation)

        // 1. Get data from Intent
        val doctorUid = intent.getStringExtra("doctorUid") ?: ""
        val doctorName = intent.getStringExtra("doctorName") ?: ""
        val specialty = intent.getStringExtra("specialty") ?: ""
        val clinicName = intent.getStringExtra("clinicName") ?: ""
        val selectedDate = intent.getStringExtra("selectedDate") ?: ""
        val selectedTime = intent.getStringExtra("selectedTime") ?: ""

        // 2. Set UI
        findViewById<TextView>(R.id.tvDoctorName).text = doctorName
        findViewById<TextView>(R.id.tvDateTime).text = "$selectedDate | $selectedTime"

        // 3. Save to Firebase with Double Booking Validation
        val patientUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        if (patientUid.isNotEmpty()) {
            checkAndBookSlot(doctorUid, doctorName, specialty, clinicName, selectedDate, selectedTime, patientUid)
        }

        findViewById<MaterialButton>(R.id.btnDone).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun checkAndBookSlot(
        doctorUid: String,
        doctorName: String,
        specialty: String,
        clinicName: String,
        selectedDate: String,
        selectedTime: String,
        patientUid: String
    ) {
        val appointmentsRef = FirebaseDatabase.getInstance().getReference("Appointments")
        
        // Double check if slot is already booked before saving
        appointmentsRef.orderByChild("doctorUid").equalTo(doctorUid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var isAlreadyBooked = false
                    for (postSnapshot in snapshot.children) {
                        val appt = postSnapshot.getValue(AppointmentModel::class.java)
                        if (appt != null && appt.bookingDate == selectedDate && appt.bookingTime == selectedTime) {
                            isAlreadyBooked = true
                            break
                        }
                    }

                    if (isAlreadyBooked) {
                        Toast.makeText(this@BookingConfirmationActivity, "This time slot is already booked. Please select another slot.", Toast.LENGTH_LONG).show()
                        finish() // Go back to selection
                    } else {
                        val appointmentId = appointmentsRef.push().key ?: ""
                        val appointment = AppointmentModel(
                            appointmentId = appointmentId,
                            patientUid = patientUid,
                            doctorUid = doctorUid,
                            doctorName = doctorName,
                            specialty = specialty,
                            clinicName = clinicName,
                            bookingDate = selectedDate,
                            bookingTime = selectedTime
                        )

                        appointmentsRef.child(appointmentId).setValue(appointment)
                            .addOnSuccessListener {
                                Log.d("Booking", "Appointment saved successfully")
                                Toast.makeText(this@BookingConfirmationActivity, "Appointment Confirmed!", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this@BookingConfirmationActivity, "Failed to save booking", Toast.LENGTH_SHORT).show()
                            }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@BookingConfirmationActivity, "Database Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
