package org.example.solvers

import org.example.BestSolution
import org.example.Solution

tailrec fun localSearchSteepest(
    solution: Solution,
    startTime: Long = System.currentTimeMillis(),
    iterations: Long = 1L,
    evaluations: Long = 1L
): BestSolution {
    val neighbourhood = solution.getNeighbourhood()
    neighbourhood.minByOrNull { it.cost }?.takeIf { it.cost < solution.cost }?.let { bestNeighbour ->
        return localSearchSteepest(
            solution = bestNeighbour,
            startTime = startTime,
            iterations = iterations + 1,
            evaluations = evaluations + neighbourhood.count()
        )
    }
    return BestSolution(
        solution,
        System.currentTimeMillis() - startTime,
        iterations,
        evaluations + neighbourhood.count()
    )
}

fun localSearchSteepestHistory(
    solution: Solution,
    bestSolutions: MutableList<BestSolution>,
    startTime: Long = System.currentTimeMillis(),
    iterations: Long = 1L,
    evaluations: Long = 1L
): List<BestSolution> {
    val neighbourhood = solution.getNeighbourhood()
    val newEvaluations = evaluations + neighbourhood.count()
    val bestSolution = neighbourhood.minBy { it.cost }.takeIf { it.cost < solution.cost }
    return when {
        bestSolution == null -> bestSolutions
        else -> {
            localSearchSteepestHistory(
                bestSolution,
                bestSolutions.apply {
                    add(
                        BestSolution(
                            bestSolution,
                            System.currentTimeMillis() - startTime,
                            iterations + 1,
                            newEvaluations
                        )
                    )
                },
                startTime,
                iterations + 1,
                newEvaluations
            )
        }
    }
}
