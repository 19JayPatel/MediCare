package com.example.medicare.Admin.model

data class UserModel(
    val name: String,
    val email: String,
    val joinedDate: String,
    val bookingCount: Int,
    val imageRes: Int
)
