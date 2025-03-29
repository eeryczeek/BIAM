package org.example

import kotlin.math.max
import kotlin.random.Random

fun main() {
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
    val generator = SolutionGenerator()
    val modifier = SolutionModifier()
    val benchmarking = Benchmarking()
    FileWriter().clear()

    files.forEach { filePath ->
        fileParser.initializeProblem("$filePath.dat")
        fileParser.parseOptimalSolution("$filePath.sln")
        val repetitions = 10L

        val greedyResults =
            benchmarking.generalBenchmark("greedy", repetitions) { modifier.localSearchGreedy() }
        val steepestResults =
            benchmarking.generalBenchmark("steepest", repetitions) { modifier.localSearchSteepest() }
        val randomWalkResults =
            benchmarking.generalBenchmark("randomWalk", repetitions) {
                modifier.randomWalk(
                    max(greedyResults.totalTimeMilliseconds, steepestResults.totalTimeMilliseconds) / repetitions
                )
            }
        val randomSearchResults: BenchmarkResult =
            benchmarking.generalBenchmark(
                "randomSearch",
                repetitions
            ) {
                generator.randomSearch(
                    max(
                        greedyResults.totalTimeMilliseconds,
                        steepestResults.totalTimeMilliseconds
                    ) / repetitions
                )
            }
        
        val initialVsFinalGreedyResults =
            benchmarking.initialVsFinalBenchmark("greedy", repetitions) { modifier.localSearchGreedy() }
        val initialVsFinalSteepestResults =
            benchmarking.initialVsFinalBenchmark("steepest", repetitions) { modifier.localSearchSteepest() }
        FileWriter().writeBenchmarkResultsToFile(
            listOf(
                greedyResults,
                steepestResults,
                randomWalkResults,
                randomSearchResults
            )
        )
    }
}

fun shuffle(array: IntArray): IntArray {
    for (i in array.size - 1 downTo 1) {
        val randomIndex = Random.nextInt(i + 1)
        array[i] = array[randomIndex].also { array[randomIndex] = array[i] }
    }
    return array
}
