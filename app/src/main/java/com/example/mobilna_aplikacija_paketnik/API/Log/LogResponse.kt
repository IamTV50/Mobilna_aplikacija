package com.example.mobilna_aplikacija_paketnik.API.Log
import org.bson.types.ObjectId
import java.util.Date

data class LogResponse(
        val user: String,
        val opend: Date,
        val user_id: ObjectId,
        val boxId: Int,
        val force: Boolean = false
)