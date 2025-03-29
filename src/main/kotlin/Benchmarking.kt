package org.example

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

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
class BenchmarkResult(
    val functionName: String,
    val totalRuns: Long,
    val totalTimeMilliseconds: Long,
    val bestSolutions: Set<List<BestSolution>>,
    val instanceSize: Long = Problem.n.toLong(),
    val optimalSolution: Solution? = OptimalSolution.solution
) {

    fun toJson(): String {
        val json = Json { encodeDefaults = true }
        return json.encodeToString(this)
    }
}