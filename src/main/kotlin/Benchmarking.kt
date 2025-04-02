package org.example

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.example.solvers.*
import kotlin.math.max

class Benchmarking(
    private val fileWriter: FileWriter = FileWriter()
) {
    private val localSearchFunctions = mapOf(
        "localSearchGreedy" to ::localSearchGreedy,
        "localSearchSteepest" to ::localSearchSteepest
    )

    private val randomSearchFunctions = mapOf(
        "randomWalk" to ::randomWalk,
        "randomSearch" to ::randomSearch
    )

    private val localSearchHistoryFunctions = mapOf(
        "localSearchGreedy" to ::localSearchGreedyHistory,
        "localSearchSteepest" to ::localSearchSteepestHistory
    )

    private val randomSearchHistoryFunctions = mapOf(
        "randomWalk" to ::randomWalkHistory,
        "randomSearch" to ::randomSearchHistory
    )

    fun performGeneralBenchmarks(
        repetitions: Long
    ) {
        val functionToRuntime = mutableMapOf<String, Long>()
        for ((functionName, function) in localSearchFunctions) {
            val results =
                generalBenchmark(repetitions, functionName) {
                    function(
                        Solution(),
                        System.currentTimeMillis(),
                        0,
                        0
                    )
                }
            functionToRuntime[functionName] = results.totalTimeMilliseconds
            fileWriter.writeCostTimeResultsToFile(results)
        }
        val runtime =
            max(functionToRuntime["localSearchGreedy"]!!, functionToRuntime["localSearchSteepest"]!!) / repetitions
        for ((functionName, function) in randomSearchFunctions) {
            val results = generalBenchmark(repetitions, functionName) {
                val initialSolution = Solution()
                function(
                    initialSolution,
                    BestSolution(initialSolution.cost, 0, 0, 0),
                    System.currentTimeMillis(),
                    runtime,
                    0,
                    0
                )
            }
            fileWriter.writeCostTimeResultsToFile(results)
        }
    }

    fun performCostOverTimeBenchmark(
        repetitions: Long
    ) {
        val functionToRuntime = mutableMapOf<String, Long>()
        for ((functionName, function) in localSearchHistoryFunctions) {
            val initialSolution = Solution()
            val results =
                costOverTimeBenchmark(repetitions, functionName) {
                    function(
                        initialSolution,
                        mutableListOf(
                            BestSolution(initialSolution.cost, System.currentTimeMillis(), 0, 0)
                        ),
                        System.currentTimeMillis(),
                        0,
                        0
                    )
                }
            functionToRuntime[functionName] = results.totalTimeMilliseconds
            fileWriter.writeCostTimeResultsToFile(results)
        }
        val runtime =
            max(functionToRuntime["localSearchGreedy"]!!, functionToRuntime["localSearchSteepest"]!!) / repetitions
        for ((functionName, function) in randomSearchHistoryFunctions) {
            val results = costOverTimeBenchmark(repetitions, functionName) {
                val initialSolution = Solution()
                function(
                    initialSolution,
                    mutableListOf(
                        BestSolution(initialSolution.cost, 0, 0, 0)
                    ),
                    System.currentTimeMillis(),
                    runtime,
                    0,
                    0
                )
            }
            fileWriter.writeCostTimeResultsToFile(results)
        }
    }


    fun performInitialVsFinalBenchmark(
        repetitions: Long
    ) {
        for ((functionName, function) in localSearchFunctions) {
            val results = mutableSetOf<InitialVsFinal>()
            for (i in 0 until repetitions) {
                val initialSolution = Solution()
                val finalSolution = function(
                    initialSolution,
                    System.currentTimeMillis(),
                    0,
                    0
                ).cost
                results.add(InitialVsFinal(initialSolution.cost, finalSolution))
            }
            fileWriter.writeInitialVsFinalResultsToFile(InitialVsFinalResult(functionName, results))
        }
    }

    fun generalBenchmark(
        repetitions: Long,
        functionName: String,
        function: () -> BestSolution
    ): BenchmarkResult {
        val results = mutableSetOf<BestSolution>()
        val startTime = System.currentTimeMillis()
        for (i in 0 until repetitions) results.add(function())
        val endTime = System.currentTimeMillis()
        return BenchmarkResult(functionName, repetitions, endTime - startTime, results)
    }

    fun costOverTimeBenchmark(
        repetitions: Long,
        functionName: String,
        function: () -> List<BestSolution>
    ): CostOverTimeBenchmarkResult {
        val results = mutableSetOf<List<BestSolution>>()
        val startTime = System.currentTimeMillis()
        for (i in 0 until repetitions) results.add(function())
        val endTime = System.currentTimeMillis()
        return CostOverTimeBenchmarkResult(functionName, repetitions, endTime - startTime, results)
    }
}

@Serializable
class InitialVsFinalResult(
    val functionName: String,
    val initialVsFinals: Set<InitialVsFinal>
) {
    fun toJson(): String {
        val json = Json { encodeDefaults = true }
        return json.encodeToString(this)
    }
}

@Serializable
data class InitialVsFinal(
    val initialCost: Long,
    val finalCost: Long,
)


@Serializable
class CostOverTimeBenchmarkResult(
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

@Serializable
class BenchmarkResult(
    val functionName: String,
    val totalRuns: Long,
    val totalTimeMilliseconds: Long,
    val bestSolutions: Set<BestSolution>,
    val instanceSize: Long = Problem.n.toLong(),
    val optimalSolution: Solution? = OptimalSolution.solution
) {

    fun toJson(): String {
        val json = Json { encodeDefaults = true }
        return json.encodeToString(this)
    }
}