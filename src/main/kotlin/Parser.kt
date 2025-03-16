package org.example

class FileParser {
    fun parseFile(filePath: String): Solution {
        val lines = java.io.File(filePath).readLines()
        val n = lines.first().trim().toInt()
        val A = lines.subList(2, n + 2)
            .map { line -> line.trim().split("\\s+".toRegex()).map { it.toInt() }.toTypedArray() }
            .toTypedArray()
        val B = lines.subList(n + 3, n + n + 3)
            .map { line -> line.trim().split("\\s+".toRegex()).map { it.toInt() }.toTypedArray() }
            .toTypedArray()

        return Solution(n, A, B)
    }
}