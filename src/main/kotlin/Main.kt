package org.example

import kotlin.math.max
import kotlin.random.Random

fun main() {
    val fileParser = FileParser()
    val files = listOf("input/tai12a")
    val generator = SolutionGenerator()
    val modifier = SolutionModifier()
    val benchmarking = Benchmarking()

    files.forEach { filePath ->
        fileParser.initializeProblem("$filePath.dat")
        fileParser.parseOptimalSolution("$filePath.sln")
        val solution = Solution()

        val greedyResults = benchmarking.generalBenchmark("greedy", 10) { modifier.localSearchGreedy(solution) }
        val steepestResults = benchmarking.generalBenchmark("steepest", 10) { modifier.localSearchSteepest(solution) }
        val randomWalkResults =
            benchmarking.generalBenchmark("randomWalk", 10) {
                modifier.randomWalk(
                    solution,
                    max(greedyResults.totalTimeMilliseconds, steepestResults.totalTimeMilliseconds) / 10
                )
            }
        val randomSearchResults: BenchmarkResult =
            benchmarking.generalBenchmark(
                "randomSearch",
                10
            ) {
                generator.randomSearch(
                    max(
                        greedyResults.totalTimeMilliseconds,
                        steepestResults.totalTimeMilliseconds
                    ) / 10
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
