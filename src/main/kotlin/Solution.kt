package org.example

object Problem {
    var n: Int = 0
    lateinit var distanceMatrix: Array<Array<Int>>
    lateinit var flowMatrix: Array<Array<Int>>

    fun initialize(n: Int, distanceMatrix: Array<Array<Int>>, flowMatrix: Array<Array<Int>>) {
        this.n = n
        this.distanceMatrix = distanceMatrix
        this.flowMatrix = flowMatrix
        assert(distanceMatrix.size == n)
        assert(flowMatrix.size == n)
        assert(distanceMatrix.all { it.size == n })
        assert(flowMatrix.all { it.size == n })
    }
}


data class Solution(
    val permutation: IntArray = IntArray(Problem.n) { it },
    var cost: Int = 0
) {
    init {
        this.cost = this.evaluate()
    }

    override fun toString(): String = "cost: $cost, permutation: ${permutation.joinToString()}"
}

fun Solution.evaluate(): Int {
    return Problem.distanceMatrix.indices.sumOf { i ->
        Problem.distanceMatrix.indices.sumOf { j ->
            Problem.flowMatrix[permutation[i]][permutation[j]] * Problem.distanceMatrix[i][j]
        }
    }
}
