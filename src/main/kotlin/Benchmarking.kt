package org.example

class Benchmarking {
    fun benchmarkGenerator(functionName: String, function: () -> Solution): BenchmarkResult {
        val results = mutableSetOf<Solution>()
        val startTime = System.currentTimeMillis()
        results.add(function())
        val endTime = System.currentTimeMillis()
        return BenchmarkResult(functionName, 1, endTime - startTime, results)
    }

    fun benchmarkModifier(functionName: String, function: (Solution) -> Solution): BenchmarkResult {
        val results = mutableSetOf<Solution>()
        val solution = SolutionGenerator().greedyInitialSolution()
        val startTime = System.currentTimeMillis()
        for (i in 0 until 100) results.add(function(solution))
        val endTime = System.currentTimeMillis()
        return BenchmarkResult(functionName, 100, endTime - startTime, results)
    }
}

data class BenchmarkResult(
    val functionName: String,
    val totalRuns: Long,
    val totalTimeMilliseconds: Long,
    val solutions: Set<Solution>,
) {
    override fun toString(): String =
        "functionName: $functionName, totalRuns: $totalRuns, totalTimeMilliseconds: $totalTimeMilliseconds, averageCost: ${
            solutions.map { it.cost }.average()
        }"
}