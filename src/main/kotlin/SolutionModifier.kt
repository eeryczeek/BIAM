package org.example

class SolutionModifier {
    fun localSearchGreedy(
        solution: Solution,
        iterations: Long = 0,
        startTime: Long = System.currentTimeMillis()
    ): List<BestSolution> {
        val bestSolutions = mutableListOf(BestSolution(solution, iterations, System.currentTimeMillis() - startTime))
        var currentSolution = solution
        while (true) {
            val betterNeighbour = currentSolution.getNeighbourhood()
                .firstOrNull { it.cost < currentSolution.cost }
            if (betterNeighbour == null) {
                return bestSolutions
            }
            currentSolution = betterNeighbour
            bestSolutions.add(BestSolution(currentSolution, iterations, System.currentTimeMillis() - startTime))
        }
    }

    fun localSearchSteepest(
        solution: Solution,
        iterations: Long = 0,
        startTime: Long = System.currentTimeMillis()
    ): List<BestSolution> {
        val bestSolutions = mutableListOf(BestSolution(solution, iterations, System.currentTimeMillis() - startTime))
        var currentSolution = solution
        while (true) {
            val betterNeighbour = currentSolution.getNeighbourhood()
                .minBy { it.cost }.takeIf { it.cost < currentSolution.cost }
            if (betterNeighbour == null) {
                return bestSolutions
            }
            currentSolution = betterNeighbour
            bestSolutions.add(BestSolution(currentSolution, iterations, System.currentTimeMillis() - startTime))
        }
    }

    fun randomWalk(initialSolution: Solution, maxTime: Long): List<BestSolution> {
        var solution: Solution = initialSolution
        var iterations = 0L
        val startTime = System.currentTimeMillis()
        val bestSolutions: MutableList<BestSolution> =
            mutableListOf(BestSolution(Solution(), iterations, System.currentTimeMillis() - startTime))
        while (System.currentTimeMillis() - startTime < maxTime) {
            iterations += 1
            solution = solution.getNeighbourhood().first()
            if (solution.cost < bestSolutions.last().solution.cost)
                bestSolutions.add(BestSolution(solution, iterations, System.currentTimeMillis() - startTime))
        }
        return bestSolutions
    }
}
