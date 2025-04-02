package org.example.solvers

import org.example.BestSolution
import org.example.Solution

tailrec fun randomSearch(
    solution: Solution,
    bestSolution: BestSolution,
    startTime: Long,
    maxTime: Long,
    iterations: Long = 0,
    evaluations: Long = 0
): BestSolution {
    if (System.currentTimeMillis() - startTime >= maxTime) {
        return BestSolution(
            solution.cost,
            System.currentTimeMillis() - startTime,
            iterations,
            evaluations
        )
    }
    val newSolution = Solution()
    val newBestSolution = if (bestSolution.cost < newSolution.cost) bestSolution else BestSolution(
        newSolution.cost,
        System.currentTimeMillis() - startTime,
        iterations + 1,
        evaluations + 1
    )
    return randomSearch(
        newSolution,
        newBestSolution,
        startTime,
        maxTime,
        iterations + 1,
        evaluations + 1
    )
}

tailrec fun randomSearchHistory(
    solution: Solution,
    bestSolutions: MutableList<BestSolution>,
    startTime: Long,
    maxTime: Long,
    iterations: Long = 0,
    evaluations: Long = 0
): List<BestSolution> {
    if (System.currentTimeMillis() - startTime >= maxTime) {
        return bestSolutions
    }
    val newSolution = Solution()
    if (newSolution.cost < bestSolutions.last().cost) {
        bestSolutions.add(
            BestSolution(
                newSolution.cost,
                System.currentTimeMillis() - startTime,
                iterations + 1,
                evaluations + 1
            )
        )
    }
    return randomSearchHistory(
        newSolution,
        bestSolutions,
        startTime,
        maxTime,
        iterations + 1,
        evaluations + 1
    )
}