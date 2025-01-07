package com.example.mobilna_aplikacija_paketnik.TSP

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

class TSP(var problemPath: String) {
    val ResourceRoot = "app/src/main/java/com/example/mobilna_aplikacija_paketnik/resources/"

    init {
        this.problemPath = ResourceRoot + problemPath
        loadData(problemPath)
    }

    private var distanceMatrix: Array<IntArray> = arrayOf()
    var edgeType: EdgeType? = null // EDGE_WEIGHT_TYPE
    var edgeFormat: EdgeFormat? = null // EDGE_WEIGHT_FORMAT

    fun calculateDistance(city1: City, city2: City): Double {
        if (city1 == city2) { return 0.0 }
        return city1.distanceTo(city2)
    }

    //done? (poc - proof of concept)
    fun generateTour(city: City?): MutableList<City> {
        //tood read all SELECTED locations in app (add necessary arguments)

        val file = File(ResourceRoot + "locations.json")
        val jsonString = file.readText() // Read the file as a String
        val gson = Gson()

        // Use TypeToken to parse the JSON into a list of City objects
        val cityListType = object : TypeToken<List<City>>() {}.type
        val locs: List<City> = gson.fromJson(jsonString, cityListType)

        println("json???: " + locs.size)
        println("json???: " + locs[0])
        println()

        val tour = Tour()
        for(c in locs){
            tour.addCityToTour(c)
        }

        return tour.getGeneratedTour()
    }

    // load/parse .tsp files
    private fun loadData(filePath: String) {
        val lines = File(filePath).readLines()
        var dimension = 0
        val edgeWeightSection = mutableListOf<String>()

        for (line in lines) {
            when {
                line.startsWith("DIMENSION") -> {
                    dimension = line.split(":")[1].trim().toInt()
                }
                line.startsWith("EDGE_WEIGHT_TYPE") -> {
                    edgeType = EdgeType.valueOf(line.split(":")[1].trim())
                }
                line.startsWith("EDGE_WEIGHT_FORMAT") -> {
                    edgeFormat = EdgeFormat.valueOf(line.split(":")[1].trim())
                }
                line.startsWith("EDGE_WEIGHT_SECTION") -> {
                    edgeWeightSection.addAll(lines.dropWhile { it != line }.drop(1))
                    break
                }
                line.startsWith("NODE_COORD_SECTION") -> {
                    edgeWeightSection.addAll(lines.dropWhile { it != line }.drop(1))
                    break
                }
            }
        }

        if (dimension > 0 && edgeWeightSection.isNotEmpty()) {
            if (edgeType == EdgeType.EXPLICIT && edgeFormat == EdgeFormat.FULL_MATRIX) {
                distanceMatrix = parseFullMatrix(edgeWeightSection, dimension)
            } else if (edgeType == EdgeType.EUC_2D) {
                distanceMatrix = parseEUC2D(edgeWeightSection, dimension)
            } else {
                throw IllegalArgumentException("Unsupported EDGE_WEIGHT_TYPE or EDGE_WEIGHT_FORMAT")
            }
        } else {
            throw IllegalArgumentException("File read error: missing data")
        }
    }

    private fun parseFullMatrix(lines: List<String>, dimension: Int): Array<IntArray> {
        val matrix = Array(dimension) { IntArray(dimension) }
        var rowIndex = 0
        var colIndex = 0

        for (line in lines) {
            if (line.isBlank()) continue
            if (line[0].isLetter()) break

            val values = line.trim().split(Regex("\\s+")).mapNotNull { it.toIntOrNull() }
            for (value in values) {
                if (colIndex >= dimension) {
                    rowIndex++
                    colIndex = 0
                }
                if (rowIndex < dimension) {
                    matrix[rowIndex][colIndex] = value
                    colIndex++
                }
            }
        }

        if (rowIndex != dimension - 1 || colIndex != dimension) {
            throw IllegalArgumentException("Matrix read error: mismatching dimensions")
        }

        return matrix
    }

    private fun parseEUC2D(lines: List<String>, dimension: Int): Array<IntArray> {
        val coordinates = mutableListOf<Pair<Double, Double>>()

        for (line in lines) {
            if (line.isBlank() || line[0].isLetter()) continue

            val parts = line.trim().split(Regex("\\s+"))
            if (parts.size >= 3) {
                val x = parts[1].toDoubleOrNull()
                val y = parts[2].toDoubleOrNull()
                if (x != null && y != null) {
                    coordinates.add(Pair(x, y))
                }
            }
        }

        if (coordinates.size != dimension) {
            throw IllegalArgumentException("Coordinates read error: mismatching dimensions")
        }

        val matrix = Array(dimension) { IntArray(dimension) }
        for (i in 0 until dimension) {
            for (j in 0 until dimension) {
                if (i == j) {
                    matrix[i][j] = 0
                } else {
                    val dist = sqrt((coordinates[i].first - coordinates[j].first).pow(2) + (coordinates[i].second - coordinates[j].second).pow(2))
                    matrix[i][j] = dist.roundToInt()
                }
            }
        }

        return matrix
    }
}

enum class EdgeType {
    EXPLICIT, EUC_2D
}

enum class EdgeFormat {
    FULL_MATRIX
}