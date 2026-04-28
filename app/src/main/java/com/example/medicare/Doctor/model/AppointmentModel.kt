package com.example.medicare.Doctor.model

data class AppointmentModel(
    val id: String = "",
    val patientName: String = "",
    val date: String = "",
    val time: String = "",
    val problem: String = "",
    var status: String = "Pending", // "Pending", "Accepted", "Rejected"
    val patientImage: Int? = null,
    val patientId: String = "",
    val doctorId: String = "",
    val doctorName: String = ""
)