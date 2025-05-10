package org.example

import org.example.benchmarking.BenchmarkResult
import org.example.benchmarking.CostOverTimeBenchmarkResult
import org.example.benchmarking.InitialVsFinalResult
import java.io.File

class FileWriter {
    fun clear() {
        File("results/cost-time-results.txt").writeText("")
//        File("results/burnout-results.txt").writeText("")
//        File("results/initial-final.txt").writeText("")
    }

    fun writeCostResultsToFile(benchmarkResults: BenchmarkResult) {
        val file = File("results/cost-time-results.txt")
        file.appendText("${benchmarkResults.toJson()}${System.lineSeparator()}")
    }

    fun writeBurnoutResultsToFile(costOverTimeBenchmarkResult: CostOverTimeBenchmarkResult) {
        val file = File("results/burnout-results.txt")
        file.appendText("${costOverTimeBenchmarkResult.toJson()}${System.lineSeparator()}")
    }

    fun writeInitialVsFinalResultsToFile(initialVsFinalResult: InitialVsFinalResult) {
        val file = File("results/initial-final.txt")
        file.appendText("${initialVsFinalResult.toJson()}${System.lineSeparator()}")
    }
}
