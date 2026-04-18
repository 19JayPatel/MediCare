package com.example.medicare.Doctor.model

data class DoctorProfileModel(
    val name: String,
    val email: String,
    val role: String,
    val patientsCount: String,
    val appointmentsCount: String,
    val rating: String,
    val profileImage: Int? = null
)