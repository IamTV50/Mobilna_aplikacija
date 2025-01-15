package com.example.mobilna_aplikacija_paketnik.TSP

import kotlin.math.pow
import kotlin.math.sqrt

fun main() {
    val random = RandomUtils()

    val populationSize = 100
    val crossoverRate = 0.8
    val mutationRate = 0.1

    /*
    println("Testing with same seed:")
    random.setSeed(1234)
    runTestSameSeed("eil101.tsp", 1000, random, populationSize, crossoverRate, mutationRate)
    random.setSeed(1234)
    runTestSameSeed("eil101.tsp", 1000, random, populationSize, crossoverRate, mutationRate)
    random.setSeed(4444)
    runTestSameSeed("eil101.tsp", 1000, random, populationSize, crossoverRate, mutationRate)
    random.setSeed(5555)
    runTestSameSeed("eil101.tsp", 1000, random, populationSize, crossoverRate, mutationRate)
    */

    println("\nRunning full tests:")

    val problems = listOf("bays29.tsp", "a280.tsp", "dca1389.tsp", "eil101.tsp", "pr1002.tsp")
    val maxFesValues = listOf(1000, 10000, 100000, 1000000)

    random.setSeedFromTime()
    for (problem in problems) {
        println("\nProblem: $problem")
        for (maxFes in maxFesValues) {
            val results = mutableListOf<Double>()
            
            repeat(30) {
                random.setSeedFromTime()

                val tsp = TSP(problem, maxFes, random)
                val ga = GA(populationSize, crossoverRate, mutationRate, random)
                val bestTour = ga.execute(tsp)//toodo empty stiing happens here when bays

                results.add(bestTour.getDistance())
            }
            
            val min = results.minOrNull() ?: 0.0
            val avg = results.average()
            val std = sqrt(results.map { (it - avg).pow(2) }.average())
            
            println("MaxFES: $maxFes")
            println("Min: $min")
            println("Avg: $avg")
            println("Std: $std")
            println()
        }
    }
}

fun runTestSameSeed(problem: String, maxFes: Int, random: RandomUtils, pop: Int, cr:Double, pm: Double) {
    val tsp = TSP(problem, maxFes, random)
    val ga = GA(pop, cr, pm, random)
    val bestTour = ga.execute(tsp)
    println("Distance: ${bestTour.getDistance()} -> seed: ${random.getSeed()}")
}
