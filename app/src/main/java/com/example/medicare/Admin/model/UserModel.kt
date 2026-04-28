package com.example.medicare.Admin.model

data class UserModel(
    val name: String = "",
    val email: String = "",
    val joinedDate: String = "",
    val bookingCount: Int = 0,
    val imageRes: Int = 0,
    val imageName: String? = null,
    val imageUrl: String = "",
    val userId: String = ""
)
