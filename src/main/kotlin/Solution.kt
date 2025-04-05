package org.example

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.random.Random

@Serializable
class Solution(
    private val permutation: IntArray = shuffle(IntArray(Problem.n) { it }),
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
}

fun shuffle(array: IntArray): IntArray {
    for (i in array.size - 1 downTo 1) {
        val randomIndex = Random.nextInt(i + 1)
        array[i] = array[randomIndex].also { array[randomIndex] = array[i] }
    }
    return array
}

@Serializable
data class BestSolution(
    val solution: Solution,
    val time: Long = System.currentTimeMillis(),
    val iterations: Long = 0,
    val evaluations: Long = 0
) {

    fun toJson(): String {
        val json = Json { encodeDefaults = true }
        return json.encodeToString(this)
    }
}
