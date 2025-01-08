package com.example.mobilna_aplikacija_paketnik.TSP

import kotlin.random.Random

class RandomUtils {
    private var seed: Long = 1234
    private var random = Random(seed)

    fun nextDouble(): Double = random.nextDouble() * 505000

    fun nextInt(bound: Int): Int = random.nextInt(bound)

    fun setSeedFromTime() {
        seed = System.currentTimeMillis()
        random = Random(seed)
    }

    fun getSeed(): Long = this.seed
    fun getRandom(): Random = this.random
}
