package org.example.benchmarking

import benchmarking.BestCost
import benchmarking.CostOverTimeBenchmarkResult
import benchmarking.GeneralResult
import benchmarking.InitialVsFinal
import benchmarking.InitialVsFinalResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.example.BestSolution
import org.example.FileWriter
import org.example.OptimalSolution
import org.example.Problem
import org.example.Solution
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
import org.example.solvers.tabuSearch

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

    suspend fun performCostBenchmark(repetitions: Long) {
        val functionToRuntime = localSearchFunctions.map { (functionName, function) ->
            val results = functionRunner(repetitions, functionName) {
                function(Solution(), System.currentTimeMillis(), 1, 1)
            }
            fileWriter.writeCostResultsToFile(results)
            functionName to results.bestSolutions.map { it.time }.average()
        }.toMap()

//        val runtime = (functionToRuntime.values.max() / repetitions).toLong()

//        randomSearchFunctions.map { (functionName, function) ->
//            val results = functionRunner(repetitions, functionName) {
//                val initialSolution = Solution()
//                function(
//                    initialSolution,
//                    initialSolution,
//                    System.currentTimeMillis(),
//                    runtime,
//                    1,
//                    1
//                )
//            }
//            fileWriter.writeCostResultsToFile(results)
//        }

        heuristicFunction.map { (functionName, function) ->
            val results = functionRunner(repetitions, functionName) {
                function()
            }
            fileWriter.writeCostResultsToFile(results)
        }

        val simulatedAnnealingResults = functionRunner(repetitions, "simulatedAnnealing") {
            val initialSolution = Solution()
            simulatedAnnealing(initialSolution, initialSolution, System.currentTimeMillis(), 1, 1)
        }
        fileWriter.writeCostResultsToFile(simulatedAnnealingResults)

        val tabuSearchResults = functionRunner(repetitions, "tabuSearch") {
            val initialSolution = Solution()
            tabuSearch(initialSolution, initialSolution, System.currentTimeMillis(), 1, 1)
        }
        fileWriter.writeCostResultsToFile(tabuSearchResults)
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
    ): GeneralResult = withContext(Dispatchers.IO) {
        val loadingJob = launch {
            val spinner = listOf("\\", "|", "/", "-")
            var index = 0
            while (isActive) {
                print("\r${spinner[index++ % spinner.size]} Running [$functionName] on instance size [${Problem.n}]")
                delay(250)
            }
        }

        val results = (0 until repetitions).map {
            async { function() }
        }.awaitAll().toSet()
        loadingJob.cancelAndJoin()
        return@withContext when {
            results.sumOf { it.time } < 100L -> functionRunner(repetitions * 10L, functionName, function)
            else -> GeneralResult(
                functionName,
                Problem.n.toLong(),
                OptimalSolution.solution,
                results.map {
                    BestCost(
                        it.solution.cost,
                        it.time,
                        it.iterations,
                        it.evaluations
                    )
                }.toSet()
            )
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
