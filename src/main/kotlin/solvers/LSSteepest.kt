package org.example.solvers

import org.example.BestSolution
import org.example.Solution

tailrec fun localSearchSteepest(
    solution: Solution,
    startTime: Long = System.currentTimeMillis(),
    iterations: Long = 0L,
    evaluations: Long = 0L
): BestSolution {
    val neighbourhood = solution.getNeighbourhood()
    val newEvaluations = evaluations + neighbourhood.count()
    val bestSolution = neighbourhood.minBy { it.cost }.takeIf { it.cost < solution.cost }
    return when {
        bestSolution == null -> BestSolution(
            solution.cost,
            System.currentTimeMillis() - startTime,
            iterations,
            newEvaluations
        )

        else -> {
            localSearchSteepest(
                bestSolution,
                startTime,
                iterations + 1,
                newEvaluations
            )
        }
    }
}

fun localSearchSteepestHistory(
    solution: Solution,
    bestSolutions: MutableList<BestSolution>,
    startTime: Long = System.currentTimeMillis(),
    iterations: Long = 0L,
    evaluations: Long = 0L
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
                            bestSolution.cost,
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
