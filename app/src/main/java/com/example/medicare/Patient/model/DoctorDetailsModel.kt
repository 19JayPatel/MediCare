package com.example.medicare.patient.model

data class DoctorDetailsModel(
    val name: String,
    val specialization: String,
    val hospitalName: String,
    val experienceYears: String,
    val rating: String,
    val reviewCount: String,
    val patientsCount: String,
    val about: String,
    val workingHoursMonFri: String,
    val workingHoursSat: String,
    val appointmentFee: String
)