package org.example.solvers

import org.example.BestSolution
import org.example.Solution
import kotlin.math.*
import kotlin.random.Random

fun simulatedAnnealing(
    initial: Solution,
    alpha: Double = 0.9,
    p: Int = 10,
    acceptanceThreshold: Double = 0.01
): BestSolution {
    val neighbourhoodSample = initial.getNeighbourhood().take(100).toList()
    if (neighbourhoodSample.isEmpty()) return BestSolution(initial, 0L, 0L)

    val avgDelta = neighbourhoodSample
        .map { abs(it.cost - initial.cost).toDouble() }
        .average()
        .coerceAtLeast(1.0)

    var temperature = -avgDelta / ln(0.95)
    val minTemperature = -avgDelta / ln(acceptanceThreshold)

    var current = initial
    var best = initial
    var iterations = 0L
    var evaluations = 0L
    var noImprovement = 0

    while (temperature > minTemperature || noImprovement < p * 10) {
        val neighbours = current.getNeighbourhood()
        val L = max(10, neighbours.count()) // L depends on instance size
        val neighboursList = neighbours.take(L).toList()
        if (neighboursList.isEmpty()) break

        repeat(L) {
            val index = neighboursList.indexOfFirst { it.cost < current.cost }
            val improving = neighboursList.getOrNull(index)
            val candidate = improving ?: neighboursList.random()
            evaluations++

            val delta = candidate.cost - current.cost
            val prob = if (delta < 0) 1.0 else exp(-delta.toDouble() / temperature)

            if (delta < 0 || Random.nextDouble() < prob) {
                current = candidate
                if (current.cost < best.cost) {
                    best = current
                    noImprovement = 0
                } else {
                    noImprovement++
                }
            } else {
                noImprovement++
            }
            iterations++
        }

        temperature *= alpha
    }

    return BestSolution(best, iterations, evaluations)
}