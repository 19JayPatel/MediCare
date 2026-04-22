package com.example.medicare.Patient.model

data class TimeSlotModel(
    val time: String,
    var isSelected: Boolean = false,
    val isAvailable: Boolean = true,
    val type: SlotType = SlotType.MORNING
)

enum class SlotType {
    MORNING, AFTERNOON
}
