package org.example.solvers

import org.example.BestSolution
import org.example.Solution
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.abs
import kotlin.random.Random

fun simulatedAnnealing(
    initial: Solution,
    alpha: Double = 0.9,
    p: Int = 10,
    acceptanceThreshold: Double = 0.01
): BestSolution {
    val neighbourhoodSample = initial.getNeighbourhood().take(1000).toList()
    if (neighbourhoodSample.isEmpty()) return BestSolution(initial, 0L, 0L)

    val avgDelta = neighbourhoodSample
        .map { abs(it.cost - initial.cost).toDouble() }
        .average()
        .coerceAtLeast(1.0)

    var temperature = -avgDelta / ln(0.95)
    val L = neighbourhoodSample.size
    val maxNoImprovement = p * L

    var best = initial
    var current = initial
    var noImprovement = 0
    var iterations = 0L
    var evaluations = 0L

    val minTemperature = -avgDelta / ln(acceptanceThreshold)

    while (temperature > minTemperature || noImprovement < maxNoImprovement) {
        val neighbourhood = current.getNeighbourhood().toList()
        if (neighbourhood.isEmpty()) break

        repeat(L) {
            val candidate = neighbourhood.random()
            evaluations++

            val delta = candidate.cost - current.cost
            val prob = getProbability(delta, temperature)

            if (delta < 0 || Random.nextDouble() < prob) {
                current = candidate
                if (candidate.cost < best.cost) {
                    best = candidate
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

fun getProbability(deltaCost: Long, temperature: Double): Double =
    if (deltaCost < 0) 1.0 else exp(-deltaCost.toDouble() / temperature)
