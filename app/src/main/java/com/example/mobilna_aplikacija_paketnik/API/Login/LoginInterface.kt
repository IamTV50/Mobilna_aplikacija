package com.example.mobilna_aplikacija_paketnik.API.Login
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginInterface {
    @POST("users/login")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse
}