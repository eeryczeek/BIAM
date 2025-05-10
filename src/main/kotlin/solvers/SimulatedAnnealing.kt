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
    neighbourhood.find { Random.nextDouble() < getProbability(it.cost - solution.cost, temperature) }?.let {
        return simulatedAnnealing(
            if (it.cost < bestSolution.cost) it else bestSolution,
            it,
            startTime,
            iterations + 1,
            evaluations + neighbourhood.indexOf(it) + 1,
            temperature * coolingRate,
            coolingRate
        )
    }
    return when {
        temperature < 0.01 -> BestSolution(bestSolution, iterations, evaluations)
        else -> simulatedAnnealing(
            bestSolution,
            solution,
            startTime,
            iterations + 1,
            evaluations + neighbourhood.count() + 1,
            temperature * coolingRate,
            coolingRate
        )
    }
}

fun getProbability(
    deltaCost: Long,
    temperature: Double
): Double = if (deltaCost < 0) 1.0 else temperature
