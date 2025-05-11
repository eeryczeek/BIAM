package org.example

import org.example.benchmarking.Benchmarking

suspend fun main() {
    val fileParser = FileParser()
    val files = listOf(
        "input/tai20b",
        "input/tai30b",
        "input/tai40b",
        "input/tai50b",
        "input/tai60b",
        "input/tai80b",
//        "input/tai100b",
//        "input/tai150b",
    )
    val benchmarking = Benchmarking()
    FileWriter().clear()

    files.forEach { filePath ->
        fileParser.initializeProblem("$filePath.dat")
        fileParser.parseOptimalSolution("$filePath.sln")
        benchmarking.performCostBenchmark(10L)
//        benchmarking.performBurnoutBenchmark(10L)
//        benchmarking.performInitialVsFinalBenchmark(100L)
    }
}
