package com.example.mobilna_aplikacija_paketnik.TSP

import java.util.Collections

class Tour() {
    private var cities: MutableList<City> = mutableListOf()

    fun getGeneratedTour(): MutableList<City> { return cities }

    fun addCityToTour(city: City){ cities.add(city) }

    fun removeCityFromTour(city: City){ cities.remove(city) }
}