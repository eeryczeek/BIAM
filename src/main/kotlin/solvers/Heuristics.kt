package org.example.solvers

import org.example.Problem
import org.example.Solution

fun heuristic(): Solution {
    val permutation = IntArray(Problem.n) { it }

    val flowSums = Problem.distanceMatrix.map { it.sum() }
    val distanceSums = Problem.flowMatrix.map { it.sum() }

    val sortedFlowIndices = flowSums.indices.sortedByDescending { flowSums[it] }
    val sortedDistanceIndices = distanceSums.indices.sortedBy { distanceSums[it] }

    for (i in 0 until Problem.n) {
        permutation[sortedFlowIndices[i]] = sortedDistanceIndices[i]
    }
    return Solution(permutation)
}
