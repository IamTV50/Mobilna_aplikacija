package com.example.mobilna_aplikacija_paketnik.API.Register

import retrofit2.http.Body

interface RegisterInterFace {
    suspend fun register(@Body registerRequest: RegisterRequest):RegisterResponse
}