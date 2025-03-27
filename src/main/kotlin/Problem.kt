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

object OptimalSolution {
    var solution: Solution? = null

    fun initialize(solution: Solution) {
        this.solution = solution
    }
}
