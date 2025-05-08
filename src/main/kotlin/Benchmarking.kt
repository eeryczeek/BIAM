package org.example

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.example.solvers.heuristic
import org.example.solvers.localSearchGreedy
import org.example.solvers.localSearchGreedyHistory
import org.example.solvers.localSearchSteepest
import org.example.solvers.localSearchSteepestHistory
import org.example.solvers.randomSearch
import org.example.solvers.randomSearchHistory
import org.example.solvers.randomWalk
import org.example.solvers.randomWalkHistory
import org.example.solvers.simulatedAnnealing

class Benchmarking(
    private val fileWriter: FileWriter = FileWriter()
) {
    private val heuristicFunction = mapOf(
        "heuristic" to ::heuristic,
    )
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

    fun performCostBenchmark(repetitions: Long) = runBlocking {
        val functionToRuntime = localSearchFunctions.map { (functionName, function) ->
            async {
                val results = functionRunner(repetitions, functionName) {
                    function(Solution(), System.currentTimeMillis(), 1, 1)
                }

                fileWriter.writeCostResultsToFile(results)
                functionName to results.totalTimeMilliseconds
            }
        }.awaitAll().toMap()

        val runtime = functionToRuntime.values.max() / repetitions

        randomSearchFunctions.map { (functionName, function) ->
            async {
                val results = functionRunner(repetitions, functionName) {
                    val initialSolution = Solution()
                    function(
                        initialSolution,
                        initialSolution,
                        System.currentTimeMillis(),
                        runtime,
                        1,
                        1
                    )
                }
                fileWriter.writeCostResultsToFile(results)
            }
        }.awaitAll()

        heuristicFunction.map { (functionName, function) ->
            async {
                val results = functionRunner(repetitions, functionName) {
                    function()
                }
                fileWriter.writeCostResultsToFile(results)
            }
        }.awaitAll()
        val results = functionRunner(repetitions, "simulatedAnnealing") {
            val initialSolution = Solution()
            simulatedAnnealing(
                initialSolution,
                initialSolution,
                System.currentTimeMillis(),
                1,
                1
            )
        }
        fileWriter.writeCostResultsToFile(results)
    }

    fun performBurnoutBenchmark(repetitions: Long) {
        val functionToRuntime = localSearchHistoryFunctions.map { (functionName, function) ->
            val initialSolution = Solution()
            val results = costOverTimeBenchmark(repetitions, functionName) {
                function(
                    initialSolution,
                    mutableListOf(BestSolution(initialSolution, 0, 1, 1)),
                    System.currentTimeMillis(),
                    1,
                    1
                )
            }
            fileWriter.writeBurnoutResultsToFile(results)
            functionName to results.totalTimeMilliseconds
        }.toMap()

        val runtime = functionToRuntime.values.max() / repetitions

        randomSearchHistoryFunctions.forEach { (functionName, function) ->
            val results = costOverTimeBenchmark(repetitions, functionName) {
                val initialSolution = Solution()
                function(
                    initialSolution,
                    mutableListOf(BestSolution(initialSolution, 0, 1, 1)),
                    System.currentTimeMillis(),
                    runtime,
                    1,
                    1
                )
            }
            fileWriter.writeBurnoutResultsToFile(results)
        }
    }

    fun performInitialVsFinalBenchmark(repetitions: Long) = runBlocking {
        localSearchFunctions.map { (functionName, function) ->
            val results = (0 until repetitions).map {
                async {
                    val initialSolution = Solution()
                    val finalSolution = function(initialSolution, System.currentTimeMillis(), 1, 1)
                    InitialVsFinal(initialSolution, finalSolution.solution)
                }
            }.awaitAll().toSet()
            fileWriter.writeInitialVsFinalResultsToFile(InitialVsFinalResult(functionName, results))
        }
    }

    private suspend fun functionRunner(
        repetitions: Long,
        functionName: String,
        function: () -> BestSolution
    ): BenchmarkResult = coroutineScope {
        val startTime = System.currentTimeMillis()
        val results = (0 until repetitions).map {
            async { function() }
        }.awaitAll().toSet()
        val endTime = System.currentTimeMillis()
        if (results.sumOf { it.time } < 100L) {
            return@coroutineScope functionRunner(repetitions * 10L, functionName, function)
        } else {
            return@coroutineScope BenchmarkResult(functionName, repetitions, endTime - startTime, results)
        }
    }

    private fun costOverTimeBenchmark(
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
data class InitialVsFinalResult(
    val functionName: String,
    val initialVsFinals: Set<InitialVsFinal>,
    val instanceSize: Long = Problem.n.toLong(),
) {
    fun toJson(): String {
        val json = Json { encodeDefaults = true }
        return json.encodeToString(this)
    }
}

@Serializable
data class InitialVsFinal(
    val initialSolution: Solution,
    val finalSolution: Solution,
)

@Serializable
data class CostOverTimeBenchmarkResult(
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
data class BenchmarkResult(
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
