package org.example.solvers

import org.example.BestSolution
import org.example.Problem
import org.example.Solution
import kotlin.random.Random

fun heuristic(): BestSolution {
    val startTime = System.currentTimeMillis()
    val permutation = IntArray(Problem.n) { -1 }

    val flowSums = Problem.distanceMatrix.map { it.sum() }
    val distanceSums = Problem.flowMatrix.map { it.sum() }

    val flowRanks = flowSums.indices.sortedByDescending { flowSums[it] }.toMutableList()
    val distanceRanks = distanceSums.indices.sortedBy { distanceSums[it] }.toMutableList()

    while (flowRanks.isNotEmpty()) {
        val facility = pickByRank(flowRanks)
        val location = pickByRank(distanceRanks)

        permutation[facility] = location

        flowRanks.remove(facility)
        distanceRanks.remove(location)
    }
    return BestSolution(Solution(permutation), System.currentTimeMillis() - startTime, 1, 1)
}

private fun pickByRank(candidates: List<Int>): Int {
    val weights = candidates.indices.map { 1.0 / (it + 1) }
    val total = weights.sum()
    val r = Random.nextDouble(total)

    var cumSum = 0.0
    for ((i, weight) in weights.withIndex()) {
        cumSum += weight
        if (r <= cumSum) return candidates[i]
    }
    return candidates.last()
}
