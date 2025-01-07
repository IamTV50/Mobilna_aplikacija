package com.example.mobilna_aplikacija_paketnik.TSP

import kotlin.random.Random

class RandomUtils {
    private var random = Random(1234) // Default seed for repeatability

    fun nextDouble(): Double = random.nextDouble()

    fun nextInt(bound: Int): Int = random.nextInt(bound)

    fun setSeedFromTime() {
        random = Random(System.currentTimeMillis())
    }

    fun setSeed(seed: Long) {
        random = Random(seed)
    }
}