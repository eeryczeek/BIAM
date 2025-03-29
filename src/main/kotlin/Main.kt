package org.example

import kotlin.math.max
import kotlin.random.Random

fun main() {
    val fileParser = FileParser()
    val files = listOf(
//        "input/tai10a",
        "input/tai20a",
        "input/tai30a",
//        "input/tai40a",
//        "input/tai50a",
//        "input/tai60a",
//        "input/tai80a",
//        "input/tai100a"
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
                    max(greedyResults.totalTimeMilliseconds, steepestResults.totalTimeMilliseconds) / 10
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
