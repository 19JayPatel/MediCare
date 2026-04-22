package com.example.medicare.Patient.model

data class BookingModel(
    val doctorId: String? = null,
    val doctorName: String? = null,
    val specialization: String? = null,
    val hospital: String? = null,
    val selectedDate: String? = null,
    val selectedTime: String? = null
)
