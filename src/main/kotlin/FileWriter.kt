package org.example

import java.io.File

class FileWriter {
    fun clear() {
        val file = File("benchmark-results.txt")
        file.writeText("")
    }

    fun writeBenchmarkResultsToFile(benchmarkResults: List<BenchmarkResult>) {
        val file = File("benchmark-results.txt")
        benchmarkResults.forEach {
            file.appendText("${it.toJson()}${System.lineSeparator()}")
        }
    }
}
