package org.example

class SolutionModifier {
    fun localSearchGreedy(): List<BestSolution> {
        var solution = Solution()
        var iterations = 0L
        val startTime = System.currentTimeMillis()
        val bestSolutions = mutableListOf(BestSolution(solution, 0, System.currentTimeMillis() - startTime))
        while (true) {
            iterations += 1L
            val betterNeighbour = solution.getNeighbourhood()
                .firstOrNull { it.cost < solution.cost }
            if (betterNeighbour == null) {
                return bestSolutions
            }
            solution = betterNeighbour
            bestSolutions.add(BestSolution(solution, iterations, System.currentTimeMillis() - startTime))
        }
    }

    fun localSearchSteepest(): List<BestSolution> {
        var solution = Solution()
        var iterations = 0L
        val startTime = System.currentTimeMillis()
        val bestSolutions = mutableListOf(BestSolution(solution, iterations, System.currentTimeMillis() - startTime))
        while (true) {
            iterations += 1L
            val betterNeighbour = solution.getNeighbourhood()
                .minBy { it.cost }.takeIf { it.cost < solution.cost }
            if (betterNeighbour == null) {
                return bestSolutions
            }
            solution = betterNeighbour
            bestSolutions.add(BestSolution(solution, iterations, System.currentTimeMillis() - startTime))
        }
    }

    fun randomWalk(maxTime: Long): List<BestSolution> {
        var solution = Solution()
        var iterations = 0L
        val startTime = System.currentTimeMillis()
        val bestSolutions: MutableList<BestSolution> =
            mutableListOf(BestSolution(solution, iterations, System.currentTimeMillis() - startTime))
        while (System.currentTimeMillis() - startTime < maxTime) {
            iterations += 1
            solution = solution.getNeighbourhood().first()
            if (solution.cost < bestSolutions.last().solution.cost)
                bestSolutions.add(BestSolution(solution, iterations, System.currentTimeMillis() - startTime))
        }
        return bestSolutions
    }
}
