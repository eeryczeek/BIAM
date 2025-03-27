package org.example

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class FileWriter {
    fun writeBenchmarkResultsToFile(benchmarkResults: List<BenchmarkResult>) {
        val file = File("benchmark-results.txt")
        benchmarkResults.forEach {
            file.appendText("${Json.encodeToString(it)}${System.lineSeparator()}")
        }
    }
}
