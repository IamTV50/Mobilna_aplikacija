package com.example.mobilna_aplikacija_paketnik.API.FaceLogin


import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface FaceLoginInterface {
    @Multipart
    @POST("users/loginFace")
    suspend fun loginFace(@Part parts: List<MultipartBody.Part>): Response<FaceLoginResponse>
}