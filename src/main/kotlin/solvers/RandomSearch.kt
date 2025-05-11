package org.example.solvers

import org.example.BestSolution
import org.example.Solution

tailrec fun randomSearch(
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
    val newSolution = Solution()
    return randomSearch(
        newSolution,
        if (bestSolution.cost < newSolution.cost) bestSolution else newSolution,
        startTime,
        maxTime,
        iterations + 1,
        evaluations + 1
    )
}
