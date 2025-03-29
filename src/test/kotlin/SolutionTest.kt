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
        val allNeighbourhoodSolutions = solution.getNeighbourhood(ordered = true)
        val allNeighbourhoodCosts = allNeighbourhoodSolutions.map { it.cost }.iterator()
        for (i in 0 until Problem.n - 1) {
            for (j in i + 1 until Problem.n) {
                val deltaCost = solution.getDeltaCost(i, j)
                val neighbourCost = allNeighbourhoodCosts.next()
                assertEquals(neighbourCost, solution.cost + deltaCost)
            }
        }
    }
}
