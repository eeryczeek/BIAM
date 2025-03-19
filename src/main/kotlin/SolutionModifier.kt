package org.example

class SolutionModifier {
    fun localSearchGreedy(solution: Solution): Solution {
        val betterNeighbour = solution.getNeighbourhood()
            .firstOrNull { it.cost < solution.cost }
        return when {
            betterNeighbour != null -> localSearchGreedy(betterNeighbour)
            else -> solution
        }
    }

    fun localSearchSteepest(solution: Solution): Solution {
        val bestNeighbour = solution.getNeighbourhood().minBy { it.cost }
        return when {
            bestNeighbour.cost < solution.cost -> localSearchSteepest(bestNeighbour)
            else -> solution
        }
    }
}
