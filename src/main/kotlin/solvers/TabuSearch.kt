package org.example.solvers

import org.example.BestSolution
import org.example.Problem
import org.example.Solution

tailrec fun tabuSearch(
    solution: Solution,
    bestSolution: Solution,
    startTime: Long = System.currentTimeMillis(),
    iterations: Long = 1L,
    evaluations: Long = 1L,
    tabuMatrix: Array<Array<Int>> = Array(solution.permutation.size) { Array(solution.permutation.size) { 0 } },
    noImprovement: Int = 0,
): BestSolution {
    val neighborhood = solution.getNeighbourhoodWithMoves()
    val solutionWithMove =
        neighborhood.take(16 * Problem.n).sortedBy { it.solution.cost }.firstOrNull {
            tabuMatrix[it.move.first][it.move.second] == 0 || it.solution.cost < bestSolution.cost
        }!!
    tabuMatrix.forEachIndexed { i, matrix ->
        matrix.forEachIndexed { j, _ ->
            if (tabuMatrix[i][j] > 0) {
                tabuMatrix[i][j]--
            }
        }
    }
    tabuMatrix[solutionWithMove.move.first][solutionWithMove.move.second] = Problem.n

    return when {
        noImprovement >= 4 * Problem.n ->
            BestSolution(
                bestSolution,
                System.currentTimeMillis() - startTime,
                iterations,
                evaluations + neighborhood.count()
            )

        else -> tabuSearch(
            solution = solutionWithMove.solution,
            bestSolution = if (solutionWithMove.solution.cost < bestSolution.cost) solutionWithMove.solution else bestSolution,
            startTime = startTime,
            iterations = iterations + 1,
            evaluations = evaluations + 4 * Problem.n,
            tabuMatrix = tabuMatrix,
            noImprovement = if (solutionWithMove.solution.cost < bestSolution.cost) 0 else noImprovement + 1,
        )
    }
}
