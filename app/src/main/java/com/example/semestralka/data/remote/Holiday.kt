package com.example.semestralka.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class Holiday(
    val date: String,
    val name: String,
    val localName: String,
)