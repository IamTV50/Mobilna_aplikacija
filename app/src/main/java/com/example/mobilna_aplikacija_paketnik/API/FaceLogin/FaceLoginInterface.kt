package com.example.mobilna_aplikacija_paketnik.API.FaceLogin


import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface FaceLoginInterface {
    @Multipart
    @POST("users/loginFace/{username}")
    suspend fun loginFace(
        @Path("username") username: String,
        @Part images: List<MultipartBody.Part>): Response<FaceLoginResponse>
}