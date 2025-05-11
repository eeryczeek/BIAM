package org.example

import benchmarking.GeneralResult
import benchmarking.InitialVsFinalResult
import java.io.File

class FileWriter {
    fun clear() {
        File("results/cost-time-results1.txt").writeText("")
//        File("results/burnout-results.txt").writeText("")
//        File("results/initial-final.txt").writeText("")
    }

    fun writeCostResultsToFile(benchmarkResults: GeneralResult) {
        val file = File("results/cost-time-results1.txt")
        file.appendText("${benchmarkResults.toJson()}${System.lineSeparator()}")
    }

    fun writeInitialVsFinalResultsToFile(initialVsFinalResult: InitialVsFinalResult) {
        val file = File("results/initial-final.txt")
        file.appendText("${initialVsFinalResult.toJson()}${System.lineSeparator()}")
    }
}
