package org.example

class SolutionModifier {
    fun localSearchGreedy(solution: Solution): Solution {
        val betterNeighbour = Neighbourhood.getNeighbour(solution)
            .firstOrNull { it.cost < solution.cost }
        return when {
            betterNeighbour != null -> localSearchGreedy(betterNeighbour)
            else -> solution
        }
    }

    fun localSearchSteepest(solution: Solution): Solution {
        val betterNeighbour = Neighbourhood.getNeighbour(solution).minBy { it.cost }
        return when {
            betterNeighbour.cost < solution.cost -> localSearchSteepest(betterNeighbour)
            else -> solution
        }
    }
}