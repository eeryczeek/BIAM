package org.example

class SolutionGenerator {

    fun greedyInitialSolution(): Solution {
        val n = Problem.n
        val permutation = IntArray(n)

        val flowSums =
            Problem.distanceMatrix.mapIndexed { index, row -> index to row.sum() }.sortedByDescending { it.second }
        val distanceSums = Problem.flowMatrix.mapIndexed { index, row -> index to row.sum() }.sortedBy { it.second }


        for (i in 0 until n) {
            permutation[flowSums[i].first] = distanceSums[i].first
        }

        val newA = Array(n) { i -> Problem.distanceMatrix[permutation[i]].copyOf() }
        for (i in 0 until n) {
            for (j in 0 until n) {
                newA[i][j] = Problem.distanceMatrix[permutation[i]][permutation[j]]
            }
        }

        return Solution()
    }

}