package com.example.mobilna_aplikacija_paketnik.API.Box

import com.google.gson.annotations.SerializedName


data class BoxResponse(
    val _id: String,
    val name: String,
    val boxId: Int,
    @SerializedName("user_id")
    val userIds: List<String>
    // Add other properties as needed
)