package org.example.solvers

import org.example.BestSolution
import org.example.Solution

tailrec fun localSearchGreedy(
    solution: Solution,
    startTime: Long = System.currentTimeMillis(),
    iterations: Long = 0L,
    evaluations: Long = 0L
): BestSolution {
    val neighbourhood = solution.getNeighbourhood()
    val index = neighbourhood.indexOfFirst { it.cost < solution.cost }
    neighbourhood.elementAtOrNull(index)?.let {
        return localSearchGreedy(
            it,
            startTime,
            iterations + 1,
            evaluations + index + 1
        )
    }
    return BestSolution(
        solution,
        System.currentTimeMillis() - startTime,
        iterations,
        evaluations + index + 1
    )
}

fun localSearchGreedyHistory(
    solution: Solution,
    bestSolutions: MutableList<BestSolution>,
    startTime: Long = System.currentTimeMillis(),
    iterations: Long = 0L,
    evaluations: Long = 0L
): List<BestSolution> {
    val neighbourhood = solution.getNeighbourhood()
    val index = neighbourhood.indexOfFirst { it.cost < solution.cost }
    val newEvaluations = evaluations + index + 1
    val bestSolution = neighbourhood.elementAtOrNull(index)
    return when {
        bestSolution == null -> bestSolutions
        else -> {
            localSearchGreedyHistory(
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
