package com.example.mobilna_aplikacija_paketnik.TSP

import android.content.Context
import kotlin.math.pow
import kotlin.math.sqrt
import java.io.File

class TSP(
    var problemPath: String,
    maxEvaluations: Int,
    var random: RandomUtils,
    private val context: Context? = null,
    var ResourceRoot: String = "app/src/main/java/com/example/mobilna_aplikacija_paketnik/resources/"
) {
    var start: City = City("", "", "", "", "", "", "", 0)
    var cities: MutableList<City> = mutableListOf()
    var numOfCities: Int = 0
    var weights: Array<DoubleArray> = emptyArray()
    private var numberOfEvaluations: Int = 0
    private var maxEvaluations: Int = 0
    var distanceType = DistanceType.EUCLIDEAN
    var edgeType: EdgeType? = null
    var edgeFormat: EdgeFormat? = null
    private var dimension: Int = 0
    private var distanceMatrix: Array<IntArray> = arrayOf()
    private var displayCoordinates = mutableListOf<Pair<Double, Double>>() // for visualization in case of FULL_MATRIX

    init {
        when {
            context != null -> {
                if (problemPath.startsWith(ResourceRoot)) {
                    // Load from assets
                    loadFromAssets(problemPath.removePrefix(ResourceRoot))
                } else {
                    // Load from internal storage
                    loadFromInternal(problemPath)
                }
            }
            else -> loadData(ResourceRoot + problemPath)
        }
        this.maxEvaluations = maxEvaluations
        this.numberOfEvaluations = 0
    }

    fun evaluate(tour: Tour) {
        var distance = 0.0
        distance += calculateDistance(start, tour.getPath()[0])
        for (index in 0 until numOfCities) {
            if (index + 1 < numOfCities) distance += calculateDistance(
                tour.getPath()[index],
                tour.getPath()[index + 1]
            )
            else distance += calculateDistance(tour.getPath()[index], start)
        }
        tour.setDistance(distance)
        numberOfEvaluations++
    }

    private fun calculateDistance(from: City?, to: City?): Double {
        var dist = Double.MAX_VALUE

        if (from == null || to == null) return dist
        if (from == to) return 0.0

        when (distanceType) {
            DistanceType.EUCLIDEAN -> {
                val dx = to.latitude.toDouble() - from.latitude.toDouble()
                val dy = to.longitude.toDouble() - from.longitude.toDouble()
                dist = sqrt(dx.pow(2.0) + dy.pow(2))
            }
            DistanceType.WEIGHTED -> {
                // Get weights from matrix
                dist = this.weights[from.index][to.index]
            }
        }

        return dist
    }

    private fun loadFromAssets(assetPath: String) {
        try {
            context?.assets?.open(assetPath)?.bufferedReader()?.use { reader ->
                parseFileContent(reader.readLines())
            }
        } catch (e: Exception) {
            println("Error reading asset file: ${e.message}")
            return
        }
    }

    private fun loadFromInternal(fileName: String) {
        context?.openFileInput(fileName)?.bufferedReader()?.use { reader ->
            parseFileContent(reader.readLines())
        }
    }

    private fun loadData(path: String) {
        val file = File(path)
        if (!file.exists()) {
            println("File $path not found!")
            return
        }
        parseFileContent(file.readLines())
    }

    private fun parseFileContent(lines: List<String>) {
        var readingNodes = false
        var readingWeights = false
        var readingDisplay = false

        lines.forEach { line ->
            when {
                line.startsWith("DIMENSION") -> {
                    dimension = line.split(":")[1].trim().toInt()
                    cities = MutableList(dimension) { City("", "", "", "", "", "", "", it) }
                    distanceMatrix = Array(dimension) { IntArray(dimension) }
                }
                line.startsWith("EDGE_WEIGHT_TYPE") -> {
                    edgeType = when (line.split(":")[1].trim()) {
                        "EUC_2D" -> EdgeType.EUC_2D
                        "EXPLICIT" -> EdgeType.EXPLICIT
                        else -> throw IllegalArgumentException("Unsupported edge weight type")
                    }
                }
                line.startsWith("EDGE_WEIGHT_FORMAT") -> {
                    edgeFormat = when (line.split(":")[1].trim()) {
                        "FULL_MATRIX" -> EdgeFormat.FULL_MATRIX
                        else -> throw IllegalArgumentException("Unsupported edge format type")
                    }
                }
                line.startsWith("NODE_COORD_SECTION") -> {
                    readingNodes = true
                    readingWeights = false
                }
                line.startsWith("EDGE_WEIGHT_SECTION") -> {
                    readingNodes = false
                    readingWeights = true
                    readingDisplay = false
                    weights = Array(dimension) { DoubleArray(dimension) }
                }
                line.startsWith("DISPLAY_DATA_SECTION") -> {
                    readingWeights = false
                    readingDisplay = true
                }
                line.startsWith("EOF") -> return@forEach
                else -> {
                    when {
                        readingNodes -> {
                            val parts = line.trim().split(Regex("\\s+"))
                            if (parts.size >= 3) {
                                val index = parts[0].toInt() - 1
                                cities[index] = City(
                                    "City$index",
                                    "",
                                    "",
                                    "",
                                    "",
                                    parts[1], // x coordinate
                                    parts[2], // y coordinate
                                    index
                                )
                            }
                        }
                        readingWeights -> {
                            val values = line.trim().split(Regex("\\s+"))
                                .filter { it.isNotEmpty() }
                                .map { it.toDouble() }
                            if (values.isNotEmpty()) {
                                // Fill weights matrix
                                var row = 0
                                var col = 0
                                values.forEach { value ->
                                    weights[row][col] = value
                                    col++
                                    if (col >= dimension) {
                                        col = 0
                                        row++
                                    }
                                }
                            }
                        }
                        readingDisplay -> parseDisplayData(line)
                    }
                }
            }
        }

        // Define cities coorinates from displayCoordinates if edge format is full_matrix
        for (index in displayCoordinates.indices) {
            cities[index] = City(
                "City$index",
                "",
                "",
                "",
                "",
                displayCoordinates[index].first.toString(), // x coordinate
                displayCoordinates[index].second.toString(), // y coordinate
                index
            )
        }

        // Set starting city
        start = cities[0]
        numOfCities = dimension
    }

    private fun parseDisplayData(line: String) {
        val parts = line.trim().split(Regex("\\s+"))
        if (parts.size >= 3) {
            val x = parts[1].toDoubleOrNull()
            val y = parts[2].toDoubleOrNull()
            if (x != null && y != null) {
                displayCoordinates.add(Pair(x, y))
            }
        }
    }

    fun generateTour(): Tour {
        val tour = Tour(cities.size)
        val indices = (0 until cities.size).toMutableList()
        val shuffledCities = mutableListOf<City>()

        while (indices.isNotEmpty()) {
            val index = random.nextInt(indices.size)
            shuffledCities.add(cities[indices[index]])
            indices.removeAt(index)
        }
        
        tour.setPath(shuffledCities)
        return tour
    }

    fun getMaxEvaluations(): Int { return maxEvaluations }

    fun getNumberOfEvaluations(): Int { return numberOfEvaluations }

    fun getDisplayCoordinates(): List<Pair<Double, Double>> { return displayCoordinates }
}

enum class EdgeType {
    EXPLICIT, EUC_2D
}

enum class EdgeFormat {
    FULL_MATRIX
}

enum class DistanceType {
    EUCLIDEAN, WEIGHTED
}
