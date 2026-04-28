package com.example.medicare.Doctor.model

data class DoctorModel(
    var doctorUid: String = "",
    var doctorId: String = "",
    var fullName: String = "",
    var doctorName: String = "",
    var designation: String = "",
    var specialty: String = "",
    var experience: String = "",
    var phone: String = "",
    var email: String = "",
    var clinicName: String = "",
    var clinicAddress: String = "",
    var consultationFee: String = "",
    var about: String = "",
    var imageUrl: String = "",
    var rating: String = "4.5",
    var role: String = "doctor",
    var startTime: String = "",
    var endTime: String = "",
    var createdAt: Long = 0L
)
