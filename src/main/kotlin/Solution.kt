package org.example


data class Solution(
    val n: Int,
    val A: Array<Array<Int>>,
    val B: Array<Array<Int>>,
)

fun Solution.evaluate(): Int {
    return A.indices.sumOf { i ->
        A.indices.sumOf { j ->
            A[i][j] * B[j][i]
        }
    }
}