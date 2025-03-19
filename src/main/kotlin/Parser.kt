package org.example

class FileParser {
    fun initializeProblem(filePath: String) {
        val lines = java.io.File(filePath).readLines()
        val n = lines.first().trim().toInt()
        val distanceMatrix = lines.subList(2, n + 2)
            .map { line -> line.trim().split("\\s+".toRegex()).map { it.toInt() }.toTypedArray() }
            .toTypedArray()
        val flowMatrix = lines.subList(n + 3, n + n + 3)
            .map { line -> line.trim().split("\\s+".toRegex()).map { it.toInt() }.toTypedArray() }
            .toTypedArray()

        Problem.initialize(n, distanceMatrix, flowMatrix)
    }
}