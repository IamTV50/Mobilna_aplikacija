package com.example.mobilna_aplikacija_paketnik.API.Log
import org.bson.types.ObjectId
import retrofit2.http.Header
import java.util.Date

data class LogRequest(
    val user: String,
    val opend: Date,
    val boxId: Int,
    val force: Boolean
)