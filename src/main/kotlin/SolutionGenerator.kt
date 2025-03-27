package org.example

class SolutionGenerator {

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

    fun randomSearch(maxTime: Long): List<BestSolution> {
        var solution: Solution
        var iterations = 0L
        val startTime = System.currentTimeMillis()
        val bestSolutions: MutableList<BestSolution> =
            mutableListOf(BestSolution(Solution(), iterations, System.currentTimeMillis() - startTime))
        while (System.currentTimeMillis() - startTime < maxTime) {
            iterations += 1
            solution = Solution()
            if (solution.cost < bestSolutions.last().solution.cost)
                bestSolutions.add(BestSolution(solution, iterations, System.currentTimeMillis() - startTime))
        }
        return bestSolutions
    }
}
