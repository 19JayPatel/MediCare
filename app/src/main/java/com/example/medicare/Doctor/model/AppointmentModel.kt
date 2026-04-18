package com.example.medicare.Doctor.model

data class AppointmentModel(
    val id: String,
    val patientName: String,
    val date: String,
    val time: String,
    val problem: String,
    var status: String, // "Pending", "Accepted", "Rejected"
    val patientImage: Int? = null
)