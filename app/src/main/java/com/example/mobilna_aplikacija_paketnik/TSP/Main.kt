package com.example.mobilna_aplikacija_paketnik.TSP

import kotlin.math.sqrt
import kotlin.math.pow

fun main() {
    val populationSize = 100
    val crossoverRate = 0.8
    val mutationRate = 0.1
    val elitismCount = 2

    val problems = listOf("a280.tsp", "bays29.tsp", "dca1389.tsp", "eil101.tsp")
    val maxFesValues = listOf(1000, 10000, 100000, 1000000) // Different maxFes values

    ///*
    for (problem in problems) {
        for (maxFes in maxFesValues) {
            val tsp = TSP(problem)
            val ea = EA(tsp, maxFes)

            val results = mutableListOf<Double>() // Store results of each run

            // Run the algorithm 30 times for each problem and maxFes value
            repeat(30) { runIndex ->
                val bestSolution = ea.run(
                    populationSize = populationSize,
                    crossoverRate = crossoverRate,
                    mutationRate = mutationRate,
                    eliteCount = elitismCount
                )
                val tourLength = calculateTourLength(tsp, bestSolution)
                results.add(tourLength)
            }

            // Calculate statistics
            val minLength = results.minOrNull() ?: 0.0
            val avgLength = results.average()
            val stdDevLength = calculateStandardDeviation(results, avgLength)

            // Output statistics
            println("Statistics for $problem with maxFes = $maxFes:")
            println("Minimum tour length: ${roundToFiveDecimals(minLength)}")
            println("Average tour length: ${roundToFiveDecimals(avgLength)}")
            println("Standard deviation of tour lengths: ${roundToFiveDecimals(stdDevLength)}")
            println()
            println("----------------------------------------")
            println()
        }
    }
    //*/


    /*
    val tsp = TSP(problems[0])
    val rnd = RandomUtils()

    println(tsp.generateLocationsTour()) //json
    println()
    println()
    println()
    println(tsp.generateTSPTour(rnd)) //not json
     */
}

private fun calculateTourLength(tsp: TSP, tour: Tour): Double {
    var length = 0.0
    val cities = tour.getGeneratedTour()
    for (i in cities.indices) {
        val city1 = cities[i]
        val city2 = cities[(i + 1) % cities.size]
        length += tsp.calculateDistance(city1, city2)
    }
    return length
}

private fun calculateStandardDeviation(data: List<Double>, mean: Double): Double {
    val variance = data.map { (it - mean).pow(2) }.average()
    return sqrt(variance)
}

private fun roundToFiveDecimals(value: Double): String {
    return String.format("%.5f", value)
}

/*
fun main() {
    val populationSize = 100
    val crossoverRate = 0.8
    val mutationRate = 0.1
    val maxFes = 10000
    val elitismCount = 2

    //val tsp = TSP("bays29.tsp")
    val tsp = TSP("eil101.tsp")
    val ea = EA(tsp, maxFes)

    val bestSolution = ea.run(
        populationSize = populationSize,
        crossoverRate = crossoverRate,
        mutationRate = mutationRate,
        eliteCount = elitismCount
    )
    
    println("Best tour length: ${calculateTourLength(tsp, bestSolution)}")
}

private fun calculateTourLength(tsp: TSP, tour: Tour): Double {
    var length = 0.0
    val cities = tour.getGeneratedTour()
    for (i in cities.indices) {
        val city1 = cities[i]
        val city2 = cities[(i + 1) % cities.size]
        length += tsp.calculateDistance(city1, city2)
    }
    return length
}
 */
