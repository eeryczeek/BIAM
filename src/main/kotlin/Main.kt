package org.example

import kotlin.random.Random

data class TimeBenchmarkResult(
    val functionName: String,
    val totalRuns: Long,
    val avgTimePerIterationMillis: Double,
)

fun main() {
    val fileParser = FileParser()
    val filePath = "input/tai12a.dat"
    val solution = fileParser.parseFile(filePath)
    println("Initial solution cost: ${solution.cost}")
    val initialSolution = greedyInitialSolution(solution.A, solution.B)
    println("Greedy initial solution cost: ${initialSolution.cost}")
    val finalSolution = localSearch(initialSolution)
    println("Final solution cost: ${finalSolution.cost}")
}

fun localSearch(solution: Solution): Solution {
    val betterNeighbour = Neighbourhood.getNeighbour(solution)
        .firstOrNull { it.cost < solution.cost }
    return when {
        betterNeighbour != null -> localSearch(betterNeighbour)
        else -> solution
    }
}

fun shuffle(array: IntArray): IntArray {
    for (i in array.size - 1 downTo 1) {
        val randomIndex = Random.nextInt(i + 1)
        array[i] = array[randomIndex].also { array[randomIndex] = array[i] }
    }
    return array
}

fun randomsWithoutRepetition(range: Int): Pair<Int, Int> {
    val random1 = Random.nextInt(range)
    val random2 = (random1 + Random.nextInt(1, range)) % range
    return Pair(random1, random2)
}

fun timeBenchmark(functionName: String, block: () -> Any): TimeBenchmarkResult {
    val targetMillis = 2000L
    var totalRuns = 0L
    var elapsedMillis: Long
    val startTime = System.currentTimeMillis()

    do {
        block()
        totalRuns++
        elapsedMillis = System.currentTimeMillis() - startTime
    } while (elapsedMillis < targetMillis)

    val avgTimePerIterationMillis = elapsedMillis.toDouble() / totalRuns
    return TimeBenchmarkResult(functionName, totalRuns, avgTimePerIterationMillis)
}

fun greedyInitialSolution(A: Array<Array<Int>>, B: Array<Array<Int>>): Solution {
    val n = A.size

    val flowSums = A.mapIndexed { index, row -> index to row.sum() }.sortedByDescending { it.second }
    val distanceSums = B.mapIndexed { index, row -> index to row.sum() }.sortedBy { it.second }

    val permutation = IntArray(n)
    for (i in 0 until n) {
        permutation[flowSums[i].first] = distanceSums[i].first
    }

    val newA = Array(n) { i -> A[permutation[i]].copyOf() }
    for (i in 0 until n) {
        for (j in 0 until n) {
            newA[i][j] = A[permutation[i]][permutation[j]]
        }
    }

    return Solution(n, newA, B)
}
