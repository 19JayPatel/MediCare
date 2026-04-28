package com.example.medicare.Doctor.model

data class ScheduleModel(
    val id: String = "",
    val day: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val slotDuration: String = ""
)