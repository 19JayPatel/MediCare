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
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class BookingConfirmationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_confirmation)

        val doctorUid = intent.getStringExtra("doctorUid") ?: ""
        val doctorName = intent.getStringExtra("doctorName") ?: ""
        val specialty = intent.getStringExtra("specialty") ?: ""
        val selectedDate = intent.getStringExtra("selectedDate") ?: ""
        val selectedTime = intent.getStringExtra("selectedTime") ?: ""

        findViewById<TextView>(R.id.tvDoctorName).text = doctorName
        findViewById<TextView>(R.id.tvDateTime).text = "$selectedDate | $selectedTime"

        val auth = FirebaseAuth.getInstance()
        val database = FirebaseDatabase.getInstance().reference
        val patientUid = auth.currentUser?.uid ?: ""

        if (patientUid.isNotEmpty()) {
            // Fetch Patient Name first
            database.child("users").child(patientUid).child("fullName").get()
                .addOnSuccessListener { snapshot ->
                    val patientName = snapshot.value as? String ?: "Patient"
                    checkAndBookSlot(database, doctorUid, doctorName, specialty, selectedDate, selectedTime, patientUid, patientName)
                }
        }

        findViewById<MaterialButton>(R.id.btnDone).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun checkAndBookSlot(
        database: DatabaseReference,
        doctorUid: String,
        doctorName: String,
        specialty: String,
        selectedDate: String,
        selectedTime: String,
        patientUid: String,
        patientName: String
    ) {
        val appointmentsRef = database.child("Appointments")

        // 1. Double Booking Validation
        appointmentsRef.orderByChild("doctorUid").equalTo(doctorUid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var isAlreadyBooked = false
                    for (postSnapshot in snapshot.children) {
                        val appt = postSnapshot.getValue(AppointmentModel::class.java)
                        if (appt != null && appt.appointmentDate == selectedDate && 
                            appt.appointmentTime == selectedTime && appt.status != "Cancelled") {
                            isAlreadyBooked = true
                            break
                        }
                    }

                    if (isAlreadyBooked) {
                        Toast.makeText(this@BookingConfirmationActivity, "This time slot is already booked.", Toast.LENGTH_LONG).show()
                        finish()
                    } else {
                        // 2. Save Appointment
                        val bookingId = "BOOK_${System.currentTimeMillis()}"
                        
                        // Get Day from Date
                        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                        val date = sdf.parse(selectedDate)
                        val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
                        val appointmentDay = if (date != null) dayFormat.format(date) else ""

                        val appointment = AppointmentModel(
                            bookingId = bookingId,
                            patientUid = patientUid,
                            doctorUid = doctorUid,
                            doctorName = doctorName,
                            patientName = patientName,
                            doctorSpecialty = specialty,
                            appointmentDate = selectedDate,
                            appointmentDay = appointmentDay,
                            appointmentTime = selectedTime,
                            status = "Upcoming"
                        )

                        appointmentsRef.child(bookingId).setValue(appointment)
                            .addOnSuccessListener {
                                Toast.makeText(this@BookingConfirmationActivity, "Booking Successful!", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this@BookingConfirmationActivity, "Booking Failed", Toast.LENGTH_SHORT).show()
                            }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }
}
