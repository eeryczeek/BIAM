import json
import matplotlib.pyplot as plt
import numpy as np
from collections import defaultdict

FUNCTION_COLORS = {
    "heuristic": "black",
    "randomWalk": "blue",
    "randomSearch": "green",
    "localSearchGreedy": "orange",
    "localSearchSteepest": "purple",
    "simulatedAnnealing": "pink",
    "tabuSearch": "turquoise",
}
plt.rcParams.update(
    {
        "font.size": 14,
        "axes.titlesize": 18,
        "axes.labelsize": 16,
        "ytick.labelsize": 14,
        "xtick.labelsize": 14,
        "legend.fontsize": 14,
        "figure.titlesize": 20,
    }
)


def load_results(file_path):
    with open(file_path) as f:
        lines = f.readlines()
    return [json.loads(line) for line in lines]


def group_data(benchmark_results):
    data_by_instance = defaultdict(lambda: defaultdict(list))
    optimal_solutions = {}

    for result in benchmark_results:
        instance_size = result["instanceSize"]
        function_name = result["functionName"]
        runs = result["bestSolutions"]
        data_by_instance[instance_size][function_name].append(runs)

        if result["optimalSolution"] is not None:
            optimal_solutions[instance_size] = result["optimalSolution"]

    return data_by_instance, optimal_solutions


def group_initial_final_data(benchmark_results):
    data_by_instance = defaultdict(lambda: defaultdict(list))
    for result in benchmark_results:
        instance_size = result["instanceSize"]
        function_name = result["functionName"]
        runs = result["initialVsFinals"]
        data_by_instance[instance_size][function_name].append(runs)

    return data_by_instance


def scaled_distance(cost, optimal_cost):
    """Calculates the fitness as (cost - optimal) / optimal."""
    return (cost - optimal_cost) / optimal_cost


def initial_final(data_by_instance, equal_axis=False, output_file="initial-final.png"):
    instance_sizes = list(data_by_instance.keys())
    fig, axes = plt.subplots(2, 4, figsize=(24, 12))
    axes = axes.flatten()

    for idx, instance_size in enumerate(instance_sizes):
        if idx >= len(axes):
            break

        ax = axes[idx]
        functions_data = data_by_instance[instance_size]

        all_initial_costs = []
        all_final_costs = []

        for function_name, runs_list in functions_data.items():
            initial_costs = []
            final_costs = []

            for runs in runs_list:
                for run in runs:
                    initial_costs.append(run["initialSolution"]["cost"])
                    final_costs.append(run["finalSolution"]["cost"])

            ax.scatter(
                initial_costs,
                final_costs,
                label=function_name,
                alpha=0.7,
                color=FUNCTION_COLORS.get(function_name, "black"),
            )
            all_initial_costs.extend(initial_costs)
            all_final_costs.extend(final_costs)

        if equal_axis:
            max_cost = max(max(all_initial_costs), max(all_final_costs))
            min_cost = min(min(all_initial_costs), min(all_final_costs))
            ax.set_xlim(min_cost, max_cost)
            ax.set_ylim(min_cost, max_cost)
            ax.set_aspect("equal", adjustable="box")
            ax.plot(
                [min_cost, max_cost],
                [min_cost, max_cost],
                linestyle="--",
                color="black",
                label="y = x",
            )

        ax.set_title(f"Instance Size: {instance_size}")
        ax.set_xlabel("Initial Solution Cost")
        ax.set_ylabel("Final Solution Cost")
        ax.legend()
        ax.grid()

    for idx in range(len(instance_sizes), len(axes)):
        fig.delaxes(axes[idx])

    plt.tight_layout()
    plt.savefig(
        f"plots/{output_file}_equal_axis.png"
        if equal_axis
        else f"plots/{output_file}.png"
    )
    plt.close()


def distance_similarity(data_by_instance, optimal_solutions, output_file="similarity"):
    """Plots a scatter plot of solution fitness vs. similarity to the optimal solution."""
    instance_sizes = list(data_by_instance.keys())
    fig, axes = plt.subplots(2, 4, figsize=(24, 12))
    axes = axes.flatten()

    for idx, instance_size in enumerate(instance_sizes):
        ax = axes[idx]
        functions_data = data_by_instance[instance_size]
        optimal_cost = optimal_solutions[instance_size]["cost"]
        optimal_solution = optimal_solutions[instance_size]["permutation"]

        for function_name, runs_list in functions_data.items():
            distances = []
            similarities = []

            for runs in runs_list:
                for run in runs:
                    solution_cost = run["finalSolution"]["cost"]
                    solution_permutation = run["finalSolution"]["permutation"]
                    distance = scaled_distance(solution_cost, optimal_cost)
                    similarity = calculate_similarity(
                        optimal_solution, solution_permutation
                    )
                    distances.append(distance)
                    similarities.append(similarity)
            ax.scatter(
                similarities + np.random.uniform(-0.003, 0.003, size=len(similarities)),
                distances + np.random.uniform(-0.003, 0.003, size=len(distances)),
                label=function_name,
                alpha=0.7,
                color=FUNCTION_COLORS.get(function_name, "black"),
            )

        ax.set_title(f"Instance Size: {instance_size}")
        ax.set_xlabel("Similarity to Optimal Solution")
        ax.set_ylabel("Scaled Distance to Optimal Solution")
        ax.legend()
        ax.grid()

    plt.tight_layout()
    plt.savefig(f"plots/{output_file}.png")
    plt.close()


def distance_average_similarity(
    data_by_instance, optimal_solutions, output_file="similarity-average"
):
    """
    Plots a scatter plot of solution average similarity vs. fitness.
    """
    instance_sizes = list(data_by_instance.keys())
    fig, axes = plt.subplots(2, 4, figsize=(24, 12))
    axes = axes.flatten()

    for idx, instance_size in enumerate(instance_sizes):
        if idx >= len(axes):
            break

        ax = axes[idx]
        functions_data = data_by_instance[instance_size]

        if instance_size not in optimal_solutions:
            continue

        optimal_cost = optimal_solutions[instance_size]["cost"]

        for function_name, runs_list in functions_data.items():
            average_similarities = []
            distances = []

            all_solutions = [
                run["finalSolution"]["permutation"]
                for runs in runs_list
                for run in runs
            ]

            for runs in runs_list:
                for run in runs:
                    solution_cost = run["finalSolution"]["cost"]
                    solution_permutation = run["finalSolution"]["permutation"]

                    distance = scaled_distance(solution_cost, optimal_cost)
                    similarities = [
                        calculate_similarity(solution_permutation, other_permutation)
                        for other_permutation in all_solutions
                        if other_permutation != solution_permutation
                    ]
                    average_similarity = sum(similarities) / len(similarities)

                    average_similarities.append(average_similarity)
                    distances.append(distance)

            ax.scatter(
                average_similarities,
                distances,
                label=function_name,
                alpha=0.7,
                color=FUNCTION_COLORS.get(function_name, "black"),
            )

        ax.set_title(f"Instance Size: {instance_size}")
        ax.set_xlabel("Average Similarity to Other Solutions")
        ax.set_ylabel("Scaled Distance to Optimal Solution")
        ax.legend()
        ax.grid()

    plt.tight_layout()
    plt.savefig(f"plots/{output_file}.png")
    plt.close()


def multi_start_local_search(
    data_by_instance, optimal_solutions, output_file="multi-start-local-search"
):
    """
    Treats final solutions as multi-start local search results and plots the best (minimum)
    and average scaled distances for each algorithm.
    """
    instance_sizes = list(data_by_instance.keys())
    fig, axes = plt.subplots(2, 4, figsize=(24, 12))
    axes = axes.flatten()

    for idx, instance_size in enumerate(instance_sizes):
        if idx >= len(axes):
            break

        ax = axes[idx]
        functions_data = data_by_instance[instance_size]

        if instance_size not in optimal_solutions:
            continue

        optimal_cost = optimal_solutions[instance_size]["cost"]

        for function_name, runs_list in functions_data.items():
            avg_scaled_distances = []
            min_scaled_distances = []

            for runs in runs_list:
                scaled_distances = [
                    scaled_distance(run["finalSolution"]["cost"], optimal_cost)
                    for run in runs
                ]

                if scaled_distances:
                    for i in range(len(scaled_distances)):
                        avg_scaled_distances.append(np.mean(scaled_distances[: i + 1]))
                        min_scaled_distances.append(min(scaled_distances[: i + 1]))

            ax.plot(
                range(len(avg_scaled_distances)),
                avg_scaled_distances,
                linestyle="--",
                color=FUNCTION_COLORS.get(function_name, "black"),
            )
            ax.plot(
                range(len(min_scaled_distances)),
                min_scaled_distances,
                label=f"{function_name}",
                linestyle="-",
                color=FUNCTION_COLORS.get(function_name, "black"),
            )

        ax.set_title(f"Instance Size: {instance_size}")
        ax.set_xlabel("Run Index")
        ax.set_ylabel("Scaled Distance to Optimal Solution")
        ax.legend()
        ax.grid()

    plt.subplots_adjust(hspace=0.4, wspace=0.3)
    plt.tight_layout()
    plt.savefig(f"plots/{output_file}.png")
    plt.close()


def calculate_similarity(optimal_permutation, solution_permutation):
    """
    Calculates the similarity between two permutations as the number of elements
    in the same position in both permutations.
    """
    return sum(
        1 for o, s in zip(optimal_permutation, solution_permutation) if o == s
    ) / len(optimal_permutation)


if __name__ == "__main__":
    cost_time_results = load_results("results/cost-time-results.txt")
    initial_final_results = load_results("results/initial-final.txt")
    cost_time_by_instance, cost_time_optimas = group_data(cost_time_results)
    initial_final_by_instance = group_initial_final_data(initial_final_results)
    multi_start_local_search(initial_final_by_instance, cost_time_optimas)

    initial_final(initial_final_by_instance, equal_axis=True)
    initial_final(initial_final_by_instance, equal_axis=False)
    distance_similarity(initial_final_by_instance, cost_time_optimas)
    distance_average_similarity(initial_final_by_instance, cost_time_optimas)
