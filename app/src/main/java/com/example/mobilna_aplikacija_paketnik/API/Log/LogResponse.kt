package com.example.mobilna_aplikacija_paketnik.API.Log
import android.util.Log
import org.bson.types.ObjectId
import java.time.LocalDate
import java.util.Date

data class LogResponse(
        val user: String,
        val opend: Date,
        val user_id: ObjectId?,
        val boxId: Int,
        val force: Boolean,
        val logs:String
)


data class GetLogResponse(
        val logs: List<LogItem>
)

data class LogItem(
        val _id: String,
        val user: String,
        val opend: String,
        val boxId: Int,
        val force: Boolean,
        val __v: Int
)