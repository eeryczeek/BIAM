package org.example

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class FileWriter {
    fun clear() {
        val file = File("benchmark-results.txt")
        file.writeText("")
    }

    fun writeBenchmarkResultsToFile(benchmarkResults: List<BenchmarkResult>) {
        val file = File("benchmark-results.txt")
        benchmarkResults.forEach {
            file.appendText("${Json.encodeToString(it)}${System.lineSeparator()}")
        }
    }
}
