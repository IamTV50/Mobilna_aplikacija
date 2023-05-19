package com.example.mobilna_aplikacija_paketnik.OpenBox
import retrofit2.http.Body
import retrofit2.http.POST

interface OpenInterface {
    @POST("Access/openbox")
    suspend fun openBox(@Body request: OpenBoxRequest): OpenBoxResponse
}
