package org.example

import kotlinx.serialization.Serializable

class Benchmarking {
    fun generalBenchmark(
        functionName: String,
        repetitions: Long,
        function: (Solution) -> List<BestSolution>
    ): BenchmarkResult {
        val results = mutableSetOf<List<BestSolution>>()
        val solution = Solution()
        val startTime = System.currentTimeMillis()
        for (i in 0 until repetitions) results.add(function(solution))
        val endTime = System.currentTimeMillis()
        return BenchmarkResult(functionName, repetitions, endTime - startTime, results)
    }
}

@Serializable
data class BenchmarkResult(
    val functionName: String,
    val totalRuns: Long,
    val totalTimeMilliseconds: Long,
    val bestSolutions: Set<List<BestSolution>>,
    val instanceSize: Long = Problem.n.toLong(),
    val optimalSolutions: Solution? = OptimalSolution.solution
) {
    override fun toString(): String =
        "functionName: $functionName, totalRuns: $totalRuns, totalTimeMilliseconds: $totalTimeMilliseconds, averageCost: ${
            bestSolutions.map { it.last().solution.cost }.average()
        }"
}
