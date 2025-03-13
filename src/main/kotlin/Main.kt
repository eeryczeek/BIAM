package org.example

import kotlin.random.Random

data class TimeBenchmarkResult(
    val functionName: String,
    val totalRuns: Long,
    val avgTimePerIterationMillis: Double,
)

fun main() {
    val shuffleResult = timeBenchmark("shuffle") { shuffle(IntArray(1000) { it + 1 }) }
    val randomsResult = timeBenchmark("randomsWithoutRepetition") { randomsWithoutRepetition(1000) }
    val sleepResult = timeBenchmark("sleep10ms") { Thread.sleep(10) }
    println(shuffleResult.toString())
    println(randomsResult.toString())
    println(sleepResult.toString())
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
    val benchmarkSeconds = 1 // Number of seconds to run the benchmark, adjustable
    val minTimeMillis = benchmarkSeconds * 1_000L
    val start = System.currentTimeMillis()
    var totalRuns = 0L

    do {
        totalRuns++
        block()
    } while (System.currentTimeMillis() - start < minTimeMillis)

    val elapsedMillis = System.currentTimeMillis() - start
    val avgTimePerIterationMillis = elapsedMillis.toDouble() / totalRuns
    return TimeBenchmarkResult(functionName, totalRuns, avgTimePerIterationMillis)
}