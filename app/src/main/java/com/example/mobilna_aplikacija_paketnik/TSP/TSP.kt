package com.example.mobilna_aplikacija_paketnik.TSP

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

class TSP(var problemPath: String) {
    val ResourceRoot = "app/src/main/java/com/example/mobilna_aplikacija_paketnik/resources/"
    private var cities = mutableListOf<Pair<Double, Double>>() // Store coordinates for EUC_2D
    private var distanceMatrix: Array<IntArray> = arrayOf()
    var edgeType: EdgeType? = null
    var edgeFormat: EdgeFormat? = null
    private var dimension: Int = 0

    init {
        this.problemPath = ResourceRoot + problemPath
        loadData(problemPath)
    }

    fun calculateDistance(city1: City, city2: City): Double {
        if (city1 == city2) return 0.0
        
        when (edgeType) {
            EdgeType.EXPLICIT -> {
                // Use pre-calculated distance matrix
                val i = cities.indexOfFirst { it.first == city1.latitude.toDouble() && it.second == city1.longitude.toDouble() }
                val j = cities.indexOfFirst { it.first == city2.latitude.toDouble() && it.second == city2.longitude.toDouble() }
                return if (i >= 0 && j >= 0) distanceMatrix[i][j].toDouble() else city1.distanceTo(city2)
            }
            EdgeType.EUC_2D -> {
                // Calculate Euclidean distance
                return city1.distanceTo(city2)
            }
            else -> return city1.distanceTo(city2)
        }
    }

    // For testing with .tsp files
    fun generateTSPTour(random: RandomUtils): MutableList<City> {
        val tour = mutableListOf<City>()

        //val a = random.nextDouble()
        //val c = random.getSeed()
        //val b = 1

        for (i in 0 until dimension) {
            tour.add(City(
                "City$i",
                "",
                "",
                "",
                "",
                (random.nextDouble() / 1000.0).toString(),
                (random.nextDouble() / 1000.0).toString()
            ))
        }
        return tour
    }

    // For actual locations from JSON (will be used in visualization)
    fun generateLocationsTour(): MutableList<City> {
        val file = File(ResourceRoot + "locations.json")
        val jsonString = file.readText()
        val gson = Gson()
        val cityListType = object : TypeToken<List<City>>() {}.type
        return gson.fromJson<List<City>>(jsonString, cityListType).toMutableList()
    }

    // load/parse .tsp files
    private fun loadData(filePath: String) {
        val lines = File(filePath).readLines()
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
            when (edgeType) {
                EdgeType.EXPLICIT -> {
                    if (edgeFormat == EdgeFormat.FULL_MATRIX) {
                        distanceMatrix = parseFullMatrix(edgeWeightSection, dimension)
                    } else {
                        throw IllegalArgumentException("Unsupported EDGE_WEIGHT_FORMAT")
                    }
                }
                EdgeType.EUC_2D -> {
                    cities = parseEUC2DCoordinates(edgeWeightSection, dimension)
                    distanceMatrix = calculateDistanceMatrix(cities)
                }
                else -> throw IllegalArgumentException("Unsupported EDGE_WEIGHT_TYPE")
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

    private fun parseEUC2DCoordinates(lines: List<String>, dimension: Int): MutableList<Pair<Double, Double>> {
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

        return coordinates
    }

    private fun calculateDistanceMatrix(coordinates: List<Pair<Double, Double>>): Array<IntArray> {
        val dimension = coordinates.size
        val matrix = Array(dimension) { IntArray(dimension) }
        
        for (i in 0 until dimension) {
            for (j in 0 until dimension) {
                if (i == j) {
                    matrix[i][j] = 0
                } else {
                    val dist = sqrt(
                        (coordinates[i].first - coordinates[j].first).pow(2) + 
                        (coordinates[i].second - coordinates[j].second).pow(2)
                    )
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
