// package org.example.solvers
//
// import org.example.BestSolution
// import org.example.Solution
// import java.util.LinkedList
//
// tailrec fun tabuSearch(
//    current: Solution,
//    best: Solution = current,
//    tabuList: LinkedList<Int> = LinkedList(),
//    maxIterations: Int = 1000,
//    tabuTenure: Int = 10,
//    iteration: Int = 0,
//    evaluations: Long = 0L,
//    startTime: Long = System.currentTimeMillis()
// ): BestSolution {
//    if (iteration >= maxIterations) {
//        return BestSolution(best, System.currentTimeMillis() - startTime, iteration.toLong(), evaluations)
//    }
//
//    val neighborhood = current.getNeighbourhood()
//    val next = neighborhood
//        .filter { it.hashCode() !in tabuList || it.cost < best.cost }
//        .minByOrNull { it.cost }
//
//    if (next == null) {
//        return BestSolution(
//            best,
//            System.currentTimeMillis() - startTime,
//            iteration.toLong(),
//            evaluations + neighborhood.size
//        )
//    }
//
//    val updatedTabuList = LinkedList(tabuList).apply {
//        add(next.hashCode())
//        if (size > tabuTenure) removeFirst()
//    }
//
//    val newBest = if (next.cost < best.cost) next else best
//
//    return tabuSearch(
//        next,
//        newBest,
//        updatedTabuList,
//        maxIterations,
//        tabuTenure,
//        iteration + 1,
//        evaluations + neighborhood.size,
//        startTime
//    )
// }
