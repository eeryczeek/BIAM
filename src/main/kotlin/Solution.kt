package org.example


data class Solution(
    val n: Int,
    val A: Array<Array<Int>>,
    val B: Array<Array<Int>>,
)

fun Solution.validate() {
    assert(A.size == n)
    A.map { assert(it.size == n) }
    assert(B.size == n)
    B.map { assert(it.size == n) }
}

fun Solution.evaluate(): Int {
    this.validate()
    return A.indices.sumOf { i ->
        A.indices.sumOf { j ->
            A[i][j] * B[j][i]
        }
    }
}