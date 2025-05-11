package org.example.solvers

import org.example.BestSolution
import org.example.Problem
import org.example.Solution
import kotlin.math.pow
import kotlin.random.Random

tailrec fun simulatedAnnealing(
    solution: Solution,
    bestSolution: Solution,
    startTime: Long = System.currentTimeMillis(),
    iterations: Long = 1L,
    evaluations: Long = 1L,
    temperature: Double = 0.999.pow(Problem.n),
    coolingRate: Double = 0.999.pow(Problem.n),
    coolingRateCountdown: Int = 4 * Problem.n,
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
            temperature = if (coolingRateCountdown == 0) temperature * coolingRate else temperature,
            coolingRate = coolingRate,
            coolingRateCountdown = if (coolingRateCountdown == 0) Problem.n else coolingRateCountdown - 1
        )
    }
    return when {
        temperature < 0.001 -> BestSolution(
            bestSolution,
            System.currentTimeMillis() - startTime,
            iterations,
            evaluations + neighbourhood.count()
        )

        else -> simulatedAnnealing(
            solution = solution,
            bestSolution = bestSolution,
            startTime = startTime,
            iterations = iterations + 1,
            evaluations = evaluations + neighbourhood.count() + 1,
            temperature = if (coolingRateCountdown == 0) temperature * coolingRate else temperature,
            coolingRate = coolingRate,
            coolingRateCountdown = if (coolingRateCountdown == 0) Problem.n else coolingRateCountdown - 1
        )
    }
}

fun getProbability(delta: Long, temperature: Double): Double {
    return if (delta < 0) 1.0 else temperature
}
