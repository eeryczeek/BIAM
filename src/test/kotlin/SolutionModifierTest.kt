import org.example.Benchmarking
import org.example.FileParser
import org.example.Solution
import org.example.SolutionModifier
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SolutionModifierTest {
    private lateinit var modifier: SolutionModifier
    private lateinit var benchmarking: Benchmarking

    @BeforeEach
    fun setUp() {
        val filePath = "input/tai40a.dat"
        FileParser().initializeProblem(filePath)
        modifier = SolutionModifier()
        benchmarking = Benchmarking()
    }

    @Test
    fun localSearchGreedy() {
        println(benchmarking.benchmarkModifier("localSearchGreedy") { modifier.localSearchGreedy(Solution()) })
    }

    @Test
    fun localSearchSteepest() {
        println(benchmarking.benchmarkModifier("localSearchSteepest") { modifier.localSearchSteepest(Solution()) })
    }
}
