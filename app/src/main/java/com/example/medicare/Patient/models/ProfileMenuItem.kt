package com.example.medicare.Patient.models

import androidx.annotation.DrawableRes

data class ProfileMenuItem(
    val id: Int,
    val title: String,
    @DrawableRes val icon: Int,
    val onClick: () -> Unit
)
