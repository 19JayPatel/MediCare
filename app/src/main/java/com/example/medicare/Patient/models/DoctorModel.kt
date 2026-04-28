package com.example.medicare.Patient.models

data class DoctorModel(
    var doctorUid: String = "",
    var doctorId: String = "",
    var doctorName: String = "", // Added missing field
    var fullName: String = "",
    var specialty: String = "",
    var clinicName: String = "",
    var clinicAddress: String = "", // Added missing field
    var experience: String = "",
    var email: String = "",
    var phone: String = "",
    var role: String = "Doctor", // Added missing field
    var rating: String = "4.5",
    var availableDays: String = "", 
    var workingDays: List<String> = listOf(),
    var startTime: String = "",
    var endTime: String = "",
    var slotDuration: String = "30", // Changed to String to prevent crash
    var consultationFee: String = "",
    var about: String = "",
    var imageUrl: String = "",
    var createdAt: Long = 0L
)
