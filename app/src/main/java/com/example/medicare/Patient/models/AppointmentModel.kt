package com.example.medicare.Patient.models

data class AppointmentModel(
    var bookingId: String = "",
    val patientUid: String = "",
    val doctorUid: String = "",
    val doctorName: String = "",
    val patientName: String = "",
    val doctorImage: String = "",
    val doctorSpecialty: String = "",
    val appointmentDate: String = "",
    val appointmentDay: String = "",
    val appointmentTime: String = "",
    var status: String = "Upcoming", // "Upcoming", "Completed", "Cancelled"
    val createdAt: Long = System.currentTimeMillis()
)