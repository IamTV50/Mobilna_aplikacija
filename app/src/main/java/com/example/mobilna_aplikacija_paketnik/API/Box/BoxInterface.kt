package com.example.mobilna_aplikacija_paketnik.API.Box

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface BoxInterface {
    @GET("box/showBox/{id}")
    suspend fun getBox(@Path("id") boxId: String): Response<BoxResponse>

}