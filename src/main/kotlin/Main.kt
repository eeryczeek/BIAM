package org.example

import kotlin.random.Random

data class TimeBenchmarkResult(
    val functionName: String,
    val counter: Long,
    val totalTime: Long,
)

fun main() {
    val shuffleResult = timeBenchmark("shuffle") { shuffle(IntArray(1000) { it + 1 }) }
    val randomsResult = timeBenchmark("randomsWithoutRepetition") { randomsWithoutRepetition(1000) }
    println(shuffleResult.toString())
    println(randomsResult.toString())
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
    val start = System.currentTimeMillis()
    var counter = 0L
    do {
        counter += 1L
        block()
    } while (System.currentTimeMillis() - start < 1000)
    val end = System.currentTimeMillis()
    return TimeBenchmarkResult(functionName, counter, end - start)
}