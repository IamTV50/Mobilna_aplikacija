package com.example.mobilna_aplikacija_paketnik.TSP

class Tour {
    private var distance: Double = 0.0
    private var dimension: Int = 0
    private var path: MutableList<City> = mutableListOf()

    constructor(tour: Tour) {
        distance = tour.distance
        dimension = tour.dimension
        path = tour.path.toMutableList()
    }

    constructor(dimension: Int) {
        this.dimension = dimension
        path = MutableList(dimension) {
            City("", "", "", "", "", "", "", 0)
        }
        distance = Double.MAX_VALUE
    }

    fun clone(): Tour { return Tour(this) }

    fun getDistance(): Double = this.distance

    fun setDistance(newDistance: Double) { this.distance = newDistance }

    fun getPath(): MutableList<City> { return path }

    fun setPath(path: MutableList<City>) { this.path = path.toMutableList() }

    fun setCity(index: Int, city: City) { path[index] = city }

    fun getDimension(): Int { return this.dimension }
}
