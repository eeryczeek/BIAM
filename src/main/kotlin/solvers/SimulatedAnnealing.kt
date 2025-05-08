package org.example.solvers

import org.example.BestSolution
import org.example.Solution
import kotlin.random.Random

tailrec fun simulatedAnnealing(
    bestSolution: Solution,
    solution: Solution,
    startTime: Long = System.currentTimeMillis(),
    iterations: Long = 0L,
    evaluations: Long = 0L,
    temperature: Double = 0.99,
    coolingRate: Double = 0.99
): BestSolution {
    val neighbourhood = solution.getNeighbourhood()
    val index =
        neighbourhood.indexOfFirst { Random.nextDouble() < getProbability(it.cost - solution.cost, temperature) }
    val newEvaluations = evaluations + index + 1
    val newSolution = neighbourhood.elementAtOrNull(index)

    val updatedBestSolution = if (newSolution != null && newSolution.cost < bestSolution.cost) {
        newSolution
    } else {
        bestSolution
    }

    return if (newSolution != null && (
                newSolution.cost < solution.cost || Random.nextDouble() < getProbability(
                    newSolution.cost - solution.cost,
                    temperature
                )
                )
    ) {
        simulatedAnnealing(
            updatedBestSolution,
            newSolution,
            startTime,
            iterations + 1,
            newEvaluations,
            temperature * coolingRate,
            coolingRate
        )
    } else {
        simulatedAnnealing(
            updatedBestSolution,
            solution,
            startTime,
            iterations + 1,
            newEvaluations,
            temperature * coolingRate,
            coolingRate
        )
    }
}

fun getProbability(
    deltaCost: Long,
    temperature: Double
): Double = if (deltaCost < 0) 1.0 else temperature
