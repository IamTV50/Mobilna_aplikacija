package com.example.mobilna_aplikacija_paketnik.API.Login

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

data class LoginResponse (
    val username : String,
    val password : String,
    val email : String,
    val _id:String,
    var userGood:Boolean,
    val images : List<ByteArray>
)
