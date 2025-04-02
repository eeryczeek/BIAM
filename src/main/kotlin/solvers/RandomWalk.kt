package org.example.solvers

import org.example.BestSolution
import org.example.Solution

tailrec fun randomWalk(
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
    val newSolution = solution.getNeighbourhood().first()
    val newBestSolution = if (bestSolution.cost < newSolution.cost) bestSolution else BestSolution(
        newSolution.cost,
        System.currentTimeMillis() - startTime,
        iterations + 1,
        evaluations + 1
    )
    return randomWalk(
        newSolution,
        newBestSolution,
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
        return bestSolutions
    }
    val newSolution = solution.getNeighbourhood().first()
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
    return randomWalkHistory(
        newSolution,
        bestSolutions,
        startTime,
        maxTime,
        iterations + 1,
        evaluations + 1
    )
}
//
//fun randomWalk(maxTime: Long): List<BestSolution> {
//    val startTime = System.currentTimeMillis()
//
//    fun walk(solution: Solution, iterations: Long, bestSolutions: List<BestSolution>): List<BestSolution> {
//        if (System.currentTimeMillis() - startTime >= maxTime) {
//            return bestSolutions + BestSolution(solution.cost, iterations, System.currentTimeMillis() - startTime)
//        }
//        val newSolution = solution.getNeighbourhood().first()
//        val newBestSolutions = when {
//            newSolution.cost < bestSolutions.last().cost ->
//                bestSolutions + BestSolution(newSolution.cost, iterations, System.currentTimeMillis() - startTime)
//
//            else -> bestSolutions
//        }
//        return walk(newSolution, iterations + 1, newBestSolutions)
//    }
//
//    val initialSolution = Solution()
//    val initialBestSolutions = listOf(BestSolution(initialSolution.cost, 0, System.currentTimeMillis() - startTime))
//    return walk(initialSolution, 0, initialBestSolutions)
//}