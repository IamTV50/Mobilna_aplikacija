package com.example.mobilna_aplikacija_paketnik.API.Register

data class RegisterRequest(
    val username : String,
    val password : String,
    val email : String,
    val images : List<ByteArray>
)
