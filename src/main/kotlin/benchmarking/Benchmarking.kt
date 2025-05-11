package org.example.benchmarking

import benchmarking.BestCost
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
import kotlinx.coroutines.withContext
import org.example.BestSolution
import org.example.FileWriter
import org.example.OptimalSolution
import org.example.Problem
import org.example.Solution
import org.example.solvers.heuristic
import org.example.solvers.localSearchGreedy
import org.example.solvers.localSearchSteepest
import org.example.solvers.randomSearch
import org.example.solvers.randomWalk
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

    suspend fun performCostBenchmark(repetitions: Long) {
        val functionToRuntime = localSearchFunctions.map { (functionName, function) ->
            val results = functionRunner(repetitions, functionName) {
                function(Solution(), System.currentTimeMillis(), 1, 1)
            }
            fileWriter.writeCostResultsToFile(results)
            functionName to results.bestSolutions.map { it.time }.average()
        }.toMap()

        val runtime = (functionToRuntime.values.max()).toLong()

        randomSearchFunctions.map { (functionName, function) ->
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

    suspend fun performInitialVsFinalBenchmark(repetitions: Long) = withContext(Dispatchers.IO) {
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
}
