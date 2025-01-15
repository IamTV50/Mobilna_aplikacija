package com.example.mobilna_aplikacija_paketnik

import kotlin.math.abs
import kotlin.random.Random
import kotlin.system.measureTimeMillis

class CompressionUtility {

    fun genNumbers(N: Int, M: Int? = null): List<Int> {
        return if (M == null) {
            List(N) { Random.nextInt(0, 256) }
        } else {
            val numbers = mutableListOf(Random.nextInt(0, 256))
            repeat(N) {
                val prevNum = numbers.last()
                val minVal = maxOf(0, prevNum - M)
                val maxVal = minOf(255, prevNum + M)
                numbers.add(Random.nextInt(minVal, maxVal))
            }
            numbers
        }
    }

    fun gen9BitAbsDict(): Map<Int, String> {
        val bitDict = mutableMapOf<Int, String>()
        var num = 255
        var i = 1

        while (num >= -255) {
            bitDict[num] = i.toString(2).padStart(9, '0')
            i++
            num--
        }

        bitDict.remove(0)
        return bitDict
    }

    val repetitions = mapOf(
        1 to "000",
        2 to "001",
        3 to "010",
        4 to "011",
        5 to "100",
        6 to "101",
        7 to "110",
        8 to "111"
    )

    val differences = mapOf(
        -2 to "00", -1 to "01", 1 to "10", 2 to "11",
        -6 to "000", -5 to "001", -4 to "010", -3 to "011",
        3 to "100", 4 to "101", 5 to "110", 6 to "111",
        -14 to "0000", -13 to "0001", -12 to "0010", -11 to "0011",
        -10 to "0100", -9 to "0101", -8 to "0110", -7 to "0111",
        7 to "1000", 8 to "1001", 9 to "1010", 10 to "1011",
        11 to "1100", 12 to "1101", 13 to "1110", 14 to "1111",
        -30 to "00000", -29 to "00001", -28 to "00010", -27 to "00011",
        -26 to "00100", -25 to "00101", -24 to "00110", -23 to "00111",
        -22 to "01000", -21 to "01001", -20 to "01010", -19 to "01011",
        -18 to "01100", -17 to "01101", -16 to "01110", -15 to "01111",
        15 to "10000", 16 to "10001", 17 to "10010", 18 to "10011",
        19 to "10100", 20 to "10101", 21 to "10110", 22 to "10111",
        23 to "11000", 24 to "11001", 25 to "11010", 26 to "11011",
        27 to "11100", 28 to "11101", 29 to "11110", 30 to "11111"
    )

    val absDict = gen9BitAbsDict()

    fun compress(numArray: List<Int>): String {
        val difs = mutableListOf(numArray[0])
        val binString = StringBuilder()

        for (i in 1 until numArray.size) {
            difs.add(numArray[i] - numArray[i - 1])
        }

        binString.append(difs[0].toString(2).padStart(8, '0'))

        var indx = 1
        while (indx < difs.size) {
            when {
                abs(difs[indx]) > 30 -> {
                    binString.append("10").append(absDict[difs[indx]])
                    indx++
                }
                difs[indx] == 0 -> {
                    binString.append("01")
                    val numRepeated = difs.subList(indx, difs.size).takeWhile { it == 0 }.size.coerceAtMost(8)
                    binString.append(repetitions[numRepeated])
                    indx += numRepeated
                }
                else -> {
                    binString.append("00")
                    val absDif = abs(difs[indx])
                    val sizePrefix = when {
                        absDif <= 2 -> "00"
                        absDif <= 6 -> "01"
                        absDif <= 14 -> "10"
                        else -> "11"
                    }
                    binString.append(sizePrefix).append(differences[difs[indx]])
                    indx++
                }
            }
        }

        binString.append("11")
        return binString.toString()
    }

    fun decompress(binString: String): List<Int> {
        val numArray = mutableListOf(binString.substring(0, 8).toInt(2))
        var i = 8

        while (i < binString.length - 2) {
            when (binString.substring(i, i + 2)) {
                "10" -> {
                    i += 2
                    val absValue = binString.substring(i, i + 9)
                    numArray.add(absDict.entries.first { it.value == absValue }.key)
                    i += 9
                }
                "01" -> {
                    i += 2
                    val repeatCount = repetitions.entries.first { it.value == binString.substring(i, i + 3) }.key
                    repeat(repeatCount) { numArray.add(0) }
                    i += 3
                }
                "00" -> {
                    i += 2
                    val diffSize = binString.substring(i, i + 2)
                    i += 2
                    val bitLength = when (diffSize) {
                        "00" -> 2
                        "01" -> 3
                        "10" -> 4
                        "11" -> 5
                        else -> throw IllegalArgumentException("Unexpected difference size encoding")
                    }
                    val diffValue = binString.substring(i, i + bitLength)
                    numArray.add(differences.entries.first { it.value == diffValue }.key)
                    i += bitLength
                }
            }
        }

        for (j in 1 until numArray.size) {
            numArray[j] += numArray[j - 1]
        }

        return numArray
    }

    fun logStuff(m: Int? = null) {
        val sizes = listOf(5, 50, 500, 5000)
        for (n in sizes) {
            val tmpArr = genNumbers(n, m)

            val compressTime = measureTimeMillis {
                val compressed = compress(tmpArr)
                val decompressTime = measureTimeMillis {
                    val decompressed = decompress(compressed)
                }

                val originalSize = tmpArr.size * 8
                val compressedSize = compressed.length
                val compressionRatio = compressedSize.toDouble() / originalSize

                println("Array size: $n (M = $m)")
                println("Compression ratio: %.2f".format(compressionRatio))

            }
            println("Compression time: $compressTime ms")
        }
    }
}