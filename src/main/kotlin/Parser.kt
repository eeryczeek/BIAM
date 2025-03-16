package org.example

class FileParser {
    fun parseFile(filePath: String): List<String> {
        return java.io.File(filePath).readLines()
    }
}