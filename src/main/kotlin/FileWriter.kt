package org.example

import java.io.File

class FileWriter {
    fun clear() {
        File("cost-time-results.txt").writeText("")
        File("burnout-results.txt").writeText("")
        File("initial-final.txt").writeText("")
    }

    fun writeCostTimeResultsToFile(benchmarkResults: BenchmarkResult) {
        val file = File("cost-time-results.txt")
        file.appendText("${benchmarkResults.toJson()}${System.lineSeparator()}")
    }

    fun writeCostTimeResultsToFile(costOverTimeBenchmarkResult: CostOverTimeBenchmarkResult) {
        val file = File("burnout-results.txt")
        file.appendText("${costOverTimeBenchmarkResult.toJson()}${System.lineSeparator()}")
    }

    fun writeInitialVsFinalResultsToFile(initialVsFinalResult: InitialVsFinalResult) {
        val file = File("initial-final.txt")
        file.appendText("${initialVsFinalResult.toJson()}${System.lineSeparator()}")
    }
}

