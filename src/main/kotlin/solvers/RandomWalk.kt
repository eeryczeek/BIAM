package org.example.solvers

import org.example.BestSolution
import org.example.Solution

tailrec fun randomWalk(
    solution: Solution,
    bestSolution: Solution,
    startTime: Long,
    maxTime: Long,
    iterations: Long = 1L,
    evaluations: Long = 1L
): BestSolution {
    if (System.currentTimeMillis() - startTime >= maxTime) {
        return BestSolution(
            bestSolution,
            System.currentTimeMillis() - startTime,
            iterations,
            evaluations
        )
    }
    val newSolution = solution.getNeighbourhood().first()
    return randomWalk(
        newSolution,
        if (newSolution.cost < bestSolution.cost) newSolution else bestSolution,
        startTime,
        maxTime,
        iterations + 1,
        evaluations + 1
    )
}
