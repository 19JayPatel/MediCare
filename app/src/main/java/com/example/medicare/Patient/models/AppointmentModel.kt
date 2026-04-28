package com.example.medicare.Patient.models

data class AppointmentModel(
    val appointmentId: String = "",
    val patientUid: String = "",
    val doctorUid: String = "",
    val doctorName: String = "",
    val specialty: String = "",
    val clinicName: String = "",
    val bookingDate: String = "",
    val bookingTime: String = "",
    val status: String = "Confirmed",
    val createdAt: Long = System.currentTimeMillis()
)