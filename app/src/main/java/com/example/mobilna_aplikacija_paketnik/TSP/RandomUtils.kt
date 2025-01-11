package com.example.mobilna_aplikacija_paketnik.TSP

import java.util.*;

class RandomUtils {
    private var seed: Long = 1234
    private val random = Random(seed)

    fun setSeed(seed: Long) {
        this.seed = seed
        random.setSeed(seed)
    }

    fun setSeedFromTime() {
        seed = System.currentTimeMillis()
        random.setSeed(seed)
    }

    fun getSeed(): Long = this.seed

    fun nextDouble(): Double = this.random.nextDouble()

    fun nextInt(upperBound: Int): Int = this.random.nextInt(upperBound)

    fun nextInt(lowerBound: Int, upperBound: Int): Int {
        return  lowerBound + random.nextInt(upperBound - lowerBound)
    }
}
