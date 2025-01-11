package com.example.mobilna_aplikacija_paketnik.TSP

import kotlin.math.pow
import kotlin.math.sqrt

class City(
    val city: String, //name
    val address: String,
    val location_description: String,
    val num_of_smart_packegers: String,
    val zip_code: String,
    val latitude: String, //x
    val longitude: String, //y
    val index: Int
) {}