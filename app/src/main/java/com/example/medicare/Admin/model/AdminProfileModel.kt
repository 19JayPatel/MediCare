package com.example.medicare.Admin.model

data class AdminProfileModel(
    val name: String,
    val email: String,
    val role: String,
    val doctorsCount: String,
    val usersCount: String,
    val avgRating: String
)
