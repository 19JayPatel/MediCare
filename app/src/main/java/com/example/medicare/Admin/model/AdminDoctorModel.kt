package com.example.medicare.Admin.model

data class AdminDoctorModel(
    val id: String = "",
    val name: String = "",
    val specialization: String = "",
    val hospital: String = "",
    val timing: String = "",
    val status: String = "Active", // "Active" or "On Leave"
    val imageResId: Int? = null,
    val imageName: String? = null,
    val imageUrl: String = ""
)
