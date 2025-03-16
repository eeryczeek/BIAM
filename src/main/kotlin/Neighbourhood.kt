package org.example

object Neighbourhood {
    fun getNeighbour(solution: Solution): Sequence<Solution> {
        val n = solution.n
        val A = solution.A
        return (0 until n).asSequence().flatMap { i ->
            (0 until n).asSequence().filter { j -> i != j }.map { j ->
                val newA = swapRowsAndColumns(A.copyOf(), i, j)
                Solution(n, newA, solution.B)
            }
        }
    }

    fun swapRowsAndColumns(A: Array<Array<Int>>, p1: Int, p2: Int): Array<Array<Int>> {
        val n = A.size
        val newA = A.map { it.clone() }.toTypedArray()

        for (i in 0 until n) {
            newA[p1][i] = A[i][p2]
            newA[i][p2] = A[p1][i]
        }

        for (i in 0 until n) {
            newA[p2][i] = A[i][p1]
            newA[i][p1] = A[p2][i]
        }

        return newA
    }
}