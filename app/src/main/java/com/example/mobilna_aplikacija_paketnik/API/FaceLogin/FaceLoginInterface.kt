package com.example.mobilna_aplikacija_paketnik.API.FaceLogin

import com.example.mobilna_aplikacija_paketnik.API.Register.RegisterRequest
import com.example.mobilna_aplikacija_paketnik.API.Register.RegisterResponse
import retrofit2.http.Body
import retrofit2.http.GET

interface FaceLoginInterface {
    @GET("users/loginFace")
    suspend fun loginFace(@Body faceloginRequest: FaceLoginRequest): FaceLoginRequest
}