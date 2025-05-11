package org.example.solvers

import org.example.BestSolution
import org.example.Solution

tailrec fun localSearchGreedy(
    solution: Solution,
    startTime: Long = System.currentTimeMillis(),
    iterations: Long = 1L,
    evaluations: Long = 1L
): BestSolution {
    val neighbourhood = solution.getNeighbourhood()
    val index = neighbourhood.indexOfFirst { it.cost < solution.cost }
    neighbourhood.elementAtOrNull(index)?.let {
        return localSearchGreedy(
            solution = it,
            startTime = startTime,
            iterations = iterations + 1,
            evaluations = evaluations + index + 1
        )
    }
    return BestSolution(
        solution,
        System.currentTimeMillis() - startTime,
        iterations,
        evaluations + index + 1
    )
}
