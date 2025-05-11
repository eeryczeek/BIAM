package benchmarking

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.example.BestSolution
import org.example.OptimalSolution
import org.example.Problem
import org.example.Solution

data class Result(
    val functionName: String,
    val instanceSize: Long,
    val optimalSolution: Solution? = OptimalSolution.solution,
)

@Serializable
data class InitialVsFinalResult(
    val functionName: String,
    val initialVsFinals: Set<InitialVsFinal>,
    val instanceSize: Long = Problem.n.toLong(),
) {
    fun toJson(): String {
        val json = Json { encodeDefaults = true }
        return json.encodeToString(this)
    }
}

@Serializable
data class InitialVsFinal(
    val initialSolution: Solution,
    val finalSolution: Solution,
)

@Serializable
data class CostOverTimeBenchmarkResult(
    val functionName: String,
    val totalRuns: Long,
    val totalTimeMilliseconds: Long,
    val bestSolutions: Set<List<BestSolution>>,
    val instanceSize: Long = Problem.n.toLong(),
    val optimalSolution: Solution? = OptimalSolution.solution
) {
    fun toJson(): String {
        val json = Json { encodeDefaults = true }
        return json.encodeToString(this)
    }
}

@Serializable
data class BenchmarkResult(
    val functionName: String,
    val bestSolutions: Set<BestSolution>,
    val instanceSize: Long = Problem.n.toLong(),
    val optimalSolution: Solution? = OptimalSolution.solution
) {
    fun toJson(): String {
        val json = Json { encodeDefaults = true }
        return json.encodeToString(this)
    }
}

@Serializable
data class BestCost(
    val cost: Long,
    val time: Long = System.currentTimeMillis(),
    val iterations: Long = 0,
    val evaluations: Long = 0
) {
    fun toJson(): String {
        val json = Json { encodeDefaults = true }
        return json.encodeToString(this)
    }
}

@Serializable
data class GeneralResult(
    val functionName: String,
    val instanceSize: Long = Problem.n.toLong(),
    val optimalSolution: Solution? = OptimalSolution.solution,
    val bestSolutions: Set<BestCost>,
) {
    fun toJson(): String {
        val json = Json { encodeDefaults = true }
        return json.encodeToString(this)
    }
}
