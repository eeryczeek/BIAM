import org.example.Benchmarking
import org.example.FileParser
import org.example.SolutionGenerator
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SolutionGeneratorTest {
    private lateinit var generator: SolutionGenerator
    private lateinit var benchmarking: Benchmarking

    @BeforeEach
    fun setUp() {
        val filePath = "input/tai12a.dat"
        FileParser().initializeProblem(filePath)
        generator = SolutionGenerator()
        benchmarking = Benchmarking()
    }

    @Test
    fun greedyInitialSolution() {
        println(benchmarking.benchmarkGenerator("greedyInitialSolution") { generator.greedyInitialSolution() })
    }
}
