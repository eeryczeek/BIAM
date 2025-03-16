package org.example

import kotlin.random.Random

data class TimeBenchmarkResult(
    val functionName: String,
    val totalRuns: Long,
    val avgTimePerIterationMillis: Double,
)

fun main() {
    val fileParser = FileParser()
    val filePath = "data/tai12a.dat"
    val solution = fileParser.parseFile(filePath)
    println(solution.evaluate())
}

fun shuffle(array: IntArray): IntArray {
    for (i in array.size - 1 downTo 1) {
        val randomIndex = Random.nextInt(i + 1)
        array[i] = array[randomIndex].also { array[randomIndex] = array[i] }
    }
    return array
}


fun randomsWithoutRepetition(range: Int): Pair<Int, Int> {
    val random1 = Random.nextInt(range)
    val random2 = (random1 + Random.nextInt(1, range)) % range
    return Pair(random1, random2)
}

fun timeBenchmark(functionName: String, block: () -> Any): TimeBenchmarkResult {
    val targetMillis = 2000L
    var totalRuns = 0L
    var elapsedMillis: Long
    val startTime = System.currentTimeMillis()

    do {
        block()
        totalRuns++
        elapsedMillis = System.currentTimeMillis() - startTime
    } while (elapsedMillis < targetMillis)

    val avgTimePerIterationMillis = elapsedMillis.toDouble() / totalRuns
    return TimeBenchmarkResult(functionName, totalRuns, avgTimePerIterationMillis)
}