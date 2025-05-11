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
