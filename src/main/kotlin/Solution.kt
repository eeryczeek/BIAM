package org.example

data class Solution(
    val permutation: IntArray = shuffle(IntArray(Problem.n) { it }),
    var cost: Int = 0
) {
    init {
        this.cost = this.evaluate()
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
    val permutation = this.permutation.clone()
    return (0 until this.permutation.size).asSequence().flatMap { i ->
        (i + 1 until this.permutation.size).asSequence().map { j ->
            val newPermutation = permutation.clone()
            newPermutation[i] = permutation[j].also { newPermutation[j] = permutation[i] }
            Solution(newPermutation)
        }
    }
}
