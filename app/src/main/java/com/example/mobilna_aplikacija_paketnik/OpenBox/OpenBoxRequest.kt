package com.example.mobilna_aplikacija_paketnik.OpenBox

data class OpenBoxRequest(
    val deliveryId: Int,
    val boxId: Int,
    val tokenFormat: Int,
    val latitude: Double,
    val longitude: Double,
    val qrCodeInfo: String,
    val terminalSeed: Int,
    val isMultibox: Boolean,
    val doorIndex: Int,
    val addAccessLog: Boolean
)