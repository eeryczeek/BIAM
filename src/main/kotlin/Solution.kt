package org.example

import kotlinx.serialization.Serializable

@Serializable
class Solution(
    val permutation: IntArray = shuffle(IntArray(Problem.n) { it }),
    var cost: Long = 0L
) {
    init {
        if (cost == 0L) cost = evaluate()
    }

    private fun evaluate(): Long {
        return Problem.distanceMatrix.indices.sumOf { i ->
            Problem.distanceMatrix.indices.sumOf { j ->
                Problem.flowMatrix[permutation[i]][permutation[j]] * Problem.distanceMatrix[i][j]
            }
        }.toLong()
    }

    fun getDeltaCost(i: Int, j: Int): Long {
        val n = Problem.n
        var deltaCost = 0L

        for (k in 0 until n) {
            if (k != i && k != j) {
                deltaCost += (Problem.flowMatrix[permutation[i]][permutation[k]] - Problem.flowMatrix[permutation[j]][permutation[k]]) *
                        (Problem.distanceMatrix[j][k] - Problem.distanceMatrix[i][k])
                deltaCost += (Problem.flowMatrix[permutation[k]][permutation[i]] - Problem.flowMatrix[permutation[k]][permutation[j]]) *
                        (Problem.distanceMatrix[k][j] - Problem.distanceMatrix[k][i])
            }
        }

        return deltaCost
    }


    fun getNeighbourhood(ordered: Boolean = false): Sequence<Solution> {
        val outerPermutation: IntArray
        val innerPermutation: IntArray

        if (ordered) {
            outerPermutation = IntArray(Problem.n) { it }
            innerPermutation = IntArray(Problem.n) { it }
        } else {
            outerPermutation = shuffle(IntArray(Problem.n) { it })
            innerPermutation = shuffle(IntArray(Problem.n) { it })
        }

        return outerPermutation.asSequence().flatMap { i ->
            innerPermutation.filter { it > i }.asSequence().map { j ->
                val newPermutation = permutation.clone()
                newPermutation[i] = permutation[j].also { newPermutation[j] = permutation[i] }
                val newCost = cost + getDeltaCost(i, j)
                Solution(newPermutation, newCost)
            }
        }
    }

    override fun toString(): String = "cost: $cost, permutation: ${permutation.joinToString()}"
}


@Serializable
data class BestSolution(
    val solution: Solution,
    val iterations: Long,
    val time: Long
)
