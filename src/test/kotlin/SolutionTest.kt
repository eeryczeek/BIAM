import kotlinx.coroutines.runBlocking
import org.example.FileParser
import org.example.Problem
import org.example.Solution
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SolutionTest {

    @BeforeEach
    fun setUp() {
        FileParser().initializeProblem("input/tai10a.dat")
    }

    @Test
    fun getDeltaCost() {
        val solution = Solution()
        val allNeighbourhoodSolutions = solution.getNeighbourhood(ordered = true).iterator()
        (0 until Problem.n - 1).forEach { i ->
            (i + 1 until Problem.n).forEach { j ->
                val deltaCost = runBlocking { solution.getDeltaCost(i, j) }
                val nextNeighbourSolutionCost = Solution(allNeighbourhoodSolutions.next().permutation).cost
                assertEquals(nextNeighbourSolutionCost, solution.cost + deltaCost)
            }
        }
    }
}
