package org.example

import kotlin.random.Random

fun main() {
    val fileParser = FileParser()
    val optimalSolutionParser = OptimalSolutionParser()
    val files = listOf("input/tai12a.dat")
    val optimalFiles = listOf("input/tai12a.sln")

    files.forEachIndexed { index, filePath ->
        fileParser.initializeProblem(filePath)
        val generator = SolutionGenerator()
        val modifier = SolutionModifier()
        val solution = Solution()
        val greedyInitial = generator.greedyInitialSolution()
        val greedySolution = modifier.localSearchGreedy(solution)
        val steepestSolution = modifier.localSearchSteepest(solution)
        val optimalSolution = optimalSolutionParser.applyOptimalPermutation(solution, optimalFiles[index])

        println("Initial solution: $solution")
        println("Greedy initial solution: $greedyInitial")
        println("LS greedy solution: $greedySolution")
        println("LS steepest solution: $steepestSolution")
        println("Optimal solution: $optimalSolution")
    }
}

fun shuffle(array: IntArray): IntArray {
    for (i in array.size - 1 downTo 1) {
        val randomIndex = Random.nextInt(i + 1)
        array[i] = array[randomIndex].also { array[randomIndex] = array[i] }
    }
    return array
}

fun randomsWithoutRepetition(range: Int): Pair<Int, Int> {
    val random1 = Random.nextInt(range)
    val random2 = (random1 + Random.nextInt(1, range)) % range
    return Pair(random1, random2)
}
