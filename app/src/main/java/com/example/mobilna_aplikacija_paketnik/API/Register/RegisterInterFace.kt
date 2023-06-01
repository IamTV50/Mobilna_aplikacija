package com.example.mobilna_aplikacija_paketnik.API.Register

import retrofit2.http.Body
import retrofit2.http.POST
interface RegisterInterFace {
    @POST("users/register")
    suspend fun register(@Body registerRequest: RegisterRequest):RegisterResponse
}