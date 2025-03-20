package org.example

class OptimalSolutionParser {
    fun applyOptimalPermutation(solution: Solution, optimalFilePath: String): Solution {
        val lines = java.io.File(optimalFilePath).readLines().filter { it.isNotBlank() }

        if (lines.size < 2) throw IllegalArgumentException("Invalid format in: $optimalFilePath")

        val optimalPermutation = lines[1]
            .trim().split("\\s+".toRegex())
            .map { it.toInt() - 1 }
            .toIntArray()

        return Solution(permutation = optimalPermutation)
    }
}
