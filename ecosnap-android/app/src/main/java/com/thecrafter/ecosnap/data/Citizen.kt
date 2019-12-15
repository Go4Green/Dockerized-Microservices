package com.thecrafter.ecosnap.data

data class Citizen (
    val uid: String,
    val socialSecurityNumber: Long,
    val name: String,
    val surname: String,
    val ecoPoints: Integer
)