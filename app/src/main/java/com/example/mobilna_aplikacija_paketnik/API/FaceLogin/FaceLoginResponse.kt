package com.example.mobilna_aplikacija_paketnik.API.FaceLogin

data class FaceLoginResponse(
    var a :String,
    var userGood:Boolean,
    val images : List<ByteArray>
)
