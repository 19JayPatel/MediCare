package com.example.medicare.Patient.models

data class UserProfile(
    val name: String,
    val dob: String,
    val gender: String,
    val email: String,
    val phone: String,
    val address: String,
    val profileImage: Int // Resource ID for now
)
