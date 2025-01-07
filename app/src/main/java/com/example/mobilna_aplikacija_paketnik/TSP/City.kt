package com.example.mobilna_aplikacija_paketnik.TSP

import kotlin.math.pow
import kotlin.math.sqrt

data class City(
    val city: String, //name
    val address: String,
    val location_description: String,
    val num_of_smart_packegers: String,
    val zip_code: String,
    val latitude: String,
    val longitude: String
) {
    fun distanceTo(other: City): Double {
        return sqrt((latitude.toDouble() - other.latitude.toDouble()).pow(2) + (longitude.toDouble() - other.longitude.toDouble()).pow(2))
    }
}