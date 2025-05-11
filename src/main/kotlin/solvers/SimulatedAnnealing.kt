package org.example.solvers

import org.example.BestSolution
import org.example.Problem
import org.example.Solution
import kotlin.math.pow
import kotlin.random.Random

fun simulatedAnnealing(
    solution: Solution,
    bestSolution: Solution,
    startTime: Long = System.currentTimeMillis(),
    iterations: Long = 1L,
    evaluations: Long = 1L,
    temperature: Double = 1.0,
    coolingRate: Double = 0.9993.pow(Problem.n)
): BestSolution {
    val neighbourhood = solution.getNeighbourhood()
    val index =
        neighbourhood.indexOfFirst { Random.nextDouble() < getProbability(it.cost - solution.cost, temperature) }
    if (index != -1) {
        val newSolution = neighbourhood.elementAt(index)
        return simulatedAnnealing(
            solution = newSolution,
            bestSolution = if (newSolution.cost < bestSolution.cost) newSolution else bestSolution,
            startTime = startTime,
            iterations = iterations + 1,
            evaluations = evaluations + index + 1,
            temperature = temperature * coolingRate,
            coolingRate = coolingRate
        )
    }
    return when {
        temperature < 0.001 -> BestSolution(
            bestSolution,
            System.currentTimeMillis() - startTime,
            iterations,
            evaluations
        )

        else -> simulatedAnnealing(
            solution = solution,
            bestSolution = bestSolution,
            startTime = startTime,
            iterations = iterations + 1,
            evaluations = evaluations + neighbourhood.count() + 1,
            temperature = temperature * coolingRate,
            coolingRate = coolingRate
        )
    }
}

fun getProbability(delta: Long, temperature: Double): Double {
    return if (delta < 0) 1.0 else temperature
}
