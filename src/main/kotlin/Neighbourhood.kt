package org.example


object Neighbourhood {
    fun getNeighbour(solution: Solution): Sequence<Solution> {
        val permutation = solution.permutation.clone()
        return (0 until solution.permutation.size).asSequence().flatMap { i ->
            (i + 1 until solution.permutation.size).asSequence().map { j ->
                val newPermutation = permutation.clone()
                newPermutation[i] = permutation[j].also { newPermutation[j] = permutation[i] }
                Solution(newPermutation)
            }
        }
    }
}