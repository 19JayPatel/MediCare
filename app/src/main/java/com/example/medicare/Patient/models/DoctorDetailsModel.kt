package com.example.medicare.Patient.models

data class DoctorDetailsModel(
    val name: String = "",
    val specialization: String = "",
    val hospitalName: String = "",
    val experienceYears: String = "",
    val rating: String = "",
    val reviewCount: String = "",
    val patientsCount: String = "",
    val about: String = "",
    val workingHoursMonFri: String = "",
    val workingHoursSat: String = "",
    val image: Int = 0,
    val imageName: String? = null
)