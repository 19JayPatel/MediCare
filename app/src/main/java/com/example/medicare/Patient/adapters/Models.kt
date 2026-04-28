package com.example.medicare.Patient.adapters

data class Category(
    val name: String = "",
    val icon: Int = 0,
    val color: Int = 0,
    val iconName: String? = null,
    val colorName: String? = null
)

data class Doctor(
    val name: String = "",
    val specialty: String = "",
    val rating: Double = 0.0,
    val image: Int = 0,
    val imageName: String? = null,
    val doctorId: String = ""
)

data class Hospital(
    val name: String = "",
    val address: String = "",
    val rating: Double = 0.0,
    val reviews: Int = 0,
    val distance: String = "",
    val type: String = "",
    val image: Int = 0,
    val imageName: String? = null
)
