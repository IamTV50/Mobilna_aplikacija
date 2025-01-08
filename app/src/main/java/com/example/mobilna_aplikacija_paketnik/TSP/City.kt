package com.example.mobilna_aplikacija_paketnik.TSP

import kotlin.math.pow
import kotlin.math.roundToInt
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
        val xd = latitude.toDouble() - other.latitude.toDouble()
        val yd = longitude.toDouble() - other.longitude.toDouble()

        return sqrt(xd.pow(2) + yd.pow(2))
    }
}