package com.example.mobilna_aplikacija_paketnik.OpenBox
data class OpenBoxResponse(
    val result: Int,
    val message: String,
    val validationErrors: Map<String, String>,
    val errorNumber: Int,
    val data: String
)