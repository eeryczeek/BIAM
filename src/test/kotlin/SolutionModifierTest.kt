import org.example.Benchmarking
import org.example.FileParser
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
        println(benchmarking.generalBenchmark("localSearchGreedy", 1) { modifier.localSearchGreedy() })
    }

    @Test
    fun localSearchSteepest() {
        println(benchmarking.generalBenchmark("localSearchSteepest", 1) { modifier.localSearchSteepest() })
    }
}
