package org.example

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

fun main() = runBlocking {
    val fileParser = FileParser()
    val files = listOf(
        "input/tai20b",
        "input/tai30b",
        "input/tai40b",
        "input/tai50b",
        "input/tai60b",
        "input/tai80b",
        "input/tai100b",
        "input/tai150b",
    )
    val benchmarking = Benchmarking()
    FileWriter().clear()

    val jobs = files.map { filePath ->
        launch {
            fileParser.initializeProblem("$filePath.dat")
            fileParser.parseOptimalSolution("$filePath.sln")
            benchmarking.performGeneralBenchmarks(10L)
            benchmarking.performCostOverTimeBenchmark(40L)
            benchmarking.performInitialVsFinalBenchmark(300L)
        }
    }
    jobs.forEach { it.join() }
}

fun shuffle(array: IntArray): IntArray {
    for (i in array.size - 1 downTo 1) {
        val randomIndex = Random.nextInt(i + 1)
        array[i] = array[randomIndex].also { array[randomIndex] = array[i] }
    }
    return array
}
