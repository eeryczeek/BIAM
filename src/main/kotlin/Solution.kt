package org.example

import kotlinx.serialization.Serializable

@Serializable
data class Solution(
    val permutation: IntArray = shuffle(IntArray(Problem.n) { it }),
    var cost: Int = 0
) {
    init {
        cost = evaluate()
    }

    override fun toString(): String = "cost: $cost, permutation: ${permutation.joinToString()}"
}

fun Solution.evaluate(): Int {
    return Problem.distanceMatrix.indices.sumOf { i ->
        Problem.distanceMatrix.indices.sumOf { j ->
            Problem.flowMatrix[permutation[i]][permutation[j]] * Problem.distanceMatrix[i][j]
        }
    }
}

fun Solution.getNeighbourhood(): Sequence<Solution> {
    val outerPermutation = shuffle(IntArray(Problem.n) { it })
    val innerPermutation = shuffle(IntArray(Problem.n) { it })
    return outerPermutation.asSequence().flatMap { i ->
        innerPermutation.filter { it > i }.asSequence().map { j ->
            val newPermutation = permutation.clone()
            newPermutation[i] = permutation[j].also { newPermutation[j] = permutation[i] }
            Solution(newPermutation)
        }
    }
}

@Serializable
data class BestSolution(
    val solution: Solution,
    val iterations: Long,
    val time: Long
)
