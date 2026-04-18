package com.example.medicare.Patient.adapters

data class Category(val name: String, val icon: Int, val color: Int)
data class Doctor(val name: String, val specialty: String, val rating: Double, val image: Int)
data class Hospital(val name: String, val address: String, val rating: Double, val reviews: Int, val distance: String, val type: String, val image: Int)
