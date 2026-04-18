package com.example.medicare.Admin.model

data class DoctorModel(
    val name: String,
    val specialization: String,
    val hospital: String,
    val rating: Double = 0.0,
    val timing: String = "",
    val status: String = "Active", // "Active" or "On Leave"
    val imageRes: Int
)
