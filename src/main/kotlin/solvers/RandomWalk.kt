package org.example.solvers

import org.example.BestSolution
import org.example.Solution

tailrec fun randomWalk(
    solution: Solution,
    bestSolution: Solution,
    startTime: Long,
    maxTime: Long,
    iterations: Long = 0,
    evaluations: Long = 0
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

tailrec fun randomWalkHistory(
    solution: Solution,
    bestSolutions: MutableList<BestSolution>,
    startTime: Long,
    maxTime: Long,
    iterations: Long = 0,
    evaluations: Long = 0
): List<BestSolution> {
    if (System.currentTimeMillis() - startTime >= maxTime) {
        bestSolutions.add(
            BestSolution(
                bestSolutions.last().solution,
                System.currentTimeMillis() - startTime,
                iterations + 1,
                evaluations + 1
            )
        )
        return bestSolutions
    }
    val newSolution = solution.getNeighbourhood().first()
    if (newSolution.cost < bestSolutions.last().solution.cost) {
        bestSolutions.add(
            BestSolution(
                newSolution,
                System.currentTimeMillis() - startTime,
                iterations + 1,
                evaluations + 1
            )
        )
    }
    return randomWalkHistory(
        newSolution,
        bestSolutions,
        startTime,
        maxTime,
        iterations + 1,
        evaluations + 1
    )
}
