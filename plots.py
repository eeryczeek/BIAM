import json
import matplotlib.pyplot as plt
import numpy as np
from collections import defaultdict

FUNCTION_COLORS = {
    "randomWalk": "blue",
    "randomSearch": "green",
    "localSearchGreedy": "orange",
    "localSearchSteepest": "purple",
}


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


def plot_cost_over_measure(
    data_by_instance, optimal_solutions, measure, output_file="burnout_chart"
):
    instance_sizes = list(data_by_instance.keys())
    fig, axes = plt.subplots(2, 4, figsize=(24, 12))
    axes = axes.flatten()

    for idx, instance_size in enumerate(instance_sizes):
        if idx >= len(axes):
            break

        ax = axes[idx]
        functions_data = data_by_instance[instance_size]

        for function_name, runs_list in functions_data.items():
            label = True
            for runs in runs_list:
                for run in runs:
                    times = [int(entry[measure]) for entry in run]
                    costs = [int(entry["solution"]["cost"]) for entry in run]
                    ax.plot(
                        times,
                        costs,
                        label=function_name if label else None,
                        alpha=0.7,
                        color=FUNCTION_COLORS.get(function_name, "black"),
                    )
                    label = False

        if instance_size in optimal_solutions:
            optimal_cost = optimal_solutions[instance_size]["cost"]
            ax.axhline(
                y=optimal_cost,
                color="red",
                linestyle="--",
                label=f"Optimal Solution (Cost: {optimal_cost})",
            )

        ax.set_title(f"Instance Size: {instance_size}")
        ax.set_xlabel("Time (ms)")
        ax.set_ylabel("Cost")
        ax.legend(loc="upper right", fontsize="small")
        ax.grid()

    plt.subplots_adjust(hspace=0.4, wspace=0.3)
    plt.tight_layout()
    plt.savefig(f"plots/{output_file}_{measure}.png")
    plt.close()


def plot_average_cost_over_measure(
    data_by_instance,
    optimal_solutions,
    measure,
    output_file="average_cost_over_measure",
):
    """
    Plots the average and minimum of the last costs for each measurement value.
    """
    instance_sizes = list(data_by_instance.keys())
    fig, axes = plt.subplots(2, 4, figsize=(24, 12))
    axes = axes.flatten()

    for idx, instance_size in enumerate(instance_sizes):
        if idx >= len(axes):
            break

        ax = axes[idx]
        functions_data = data_by_instance[instance_size]

        for function_name, runs_list in functions_data.items():
            all_measurements = []
            all_costs = []

            # Collect all measurements and costs
            for runs in runs_list:
                for run in runs:
                    all_measurements.extend([int(entry[measure]) for entry in run])
                    all_costs.extend([int(entry["solution"]["cost"]) for entry in run])

            # Sort measurements
            sorted_measurements = sorted(set(all_measurements))

            # Compute the average and minimum of the last costs for each measurement
            avg_last_costs = []
            min_last_costs = []

            for m in sorted_measurements:
                # Filter runs up to the current measurement
                last_costs = []
                for runs in runs_list:
                    for run in runs:
                        filtered_costs = [
                            int(entry["solution"]["cost"])
                            for entry in run
                            if int(entry[measure]) <= m
                        ]
                        if filtered_costs:
                            last_costs.append(filtered_costs[-1])  # Take the last cost

                # Compute the average and minimum of the last costs
                if last_costs:
                    avg_last_costs.append(sum(last_costs) / len(last_costs))
                    min_last_costs.append(min(last_costs))

            # Plot the results
            ax.plot(
                sorted_measurements,
                avg_last_costs,
                label=f"{function_name} (avg last)",
                linestyle="-",
                alpha=0.7,
                color=FUNCTION_COLORS.get(function_name, "black"),
            )
            ax.plot(
                sorted_measurements,
                min_last_costs,
                label=f"{function_name} (min last)",
                linestyle="--",
                alpha=0.7,
                color=FUNCTION_COLORS.get(function_name, "black"),
            )

        if instance_size in optimal_solutions:
            optimal_cost = optimal_solutions[instance_size]["cost"]
            ax.axhline(
                y=optimal_cost,
                color="red",
                linestyle="--",
                label=f"Optimal Solution (Cost: {optimal_cost})",
            )

        ax.set_title(f"Instance Size: {instance_size}")
        ax.set_xlabel(f"{measure.capitalize()} (ms)")
        ax.set_ylabel("Cost")
        ax.legend(loc="upper right", fontsize="small")
        ax.grid()

    plt.subplots_adjust(hspace=0.4, wspace=0.3)
    plt.tight_layout()
    plt.savefig(f"plots/{output_file}_{measure}.png")
    plt.close()


def plot_all_average_performance(data_by_instance, optimal_solutions):
    plt.figure(figsize=(10, 6))
    plt.title("Comparison of Algorithms' Average Fitness Across All Instance Sizes")

    aggregated_fitness = defaultdict(list)
    aggregated_std = defaultdict(list)

    for instance_size, functions_data in data_by_instance.items():
        if instance_size not in optimal_solutions:
            continue

        optimal_value = optimal_solutions[instance_size]["cost"]

        for function_name, runs_list in functions_data.items():
            final_costs = [
                run["solution"]["cost"] for runs in runs_list for run in runs
            ]
            fitness_values = [
                (cost - optimal_value) / optimal_value for cost in final_costs
            ]
            aggregated_fitness[function_name].append(np.mean(fitness_values))
            aggregated_std[function_name].append(np.std(fitness_values))

    for function_name in aggregated_fitness.keys():
        avg_fitness = aggregated_fitness[function_name]
        std_fitness = aggregated_std[function_name]
        plt.errorbar(
            list(optimal_solutions.keys()),
            avg_fitness,
            yerr=std_fitness,
            label=function_name,
            capsize=5,
        )

    plt.xlabel("Instance Size")
    plt.ylabel("Average Fitness (with Std Dev)")
    plt.legend()
    plt.grid()
    plt.savefig("plots/average_performance.png")


def plot_all_average_measure(data_by_instance, measure, log_scale=False):
    plt.figure(figsize=(12, 8))
    plt.title(f"Algorithms' average {measure} across all instance sizes")

    aggregated_times = defaultdict(list)
    aggregated_std = defaultdict(list)

    for instance_size, functions_data in data_by_instance.items():
        for function_name, runs_list in functions_data.items():
            if function_name in ["randomWalk", "randomSearch"] and measure == "time":
                continue
            running_times = [entry[measure] for runs in runs_list for entry in runs]
            aggregated_times[function_name].append(np.mean(running_times))
            aggregated_std[function_name].append(np.std(running_times))

    x = np.arange(len(data_by_instance.keys()))

    for function_name, avg_times in aggregated_times.items():
        std_times = aggregated_std[function_name]
        plt.errorbar(
            x,
            avg_times,
            yerr=std_times,
            label=function_name,
            capsize=5,
            marker="o",
            linestyle="-",
        )

    plt.xticks(x, data_by_instance.keys())
    plt.xlabel("Instance Size")
    if log_scale:
        plt.yscale("log")
        plt.ylabel(f"Average {measure} (ms, log scale)")
    else:
        plt.ylabel(f"Average {measure} (ms)")
    plt.legend()
    plt.grid(axis="y", which="both", linestyle="--", linewidth=0.5)
    plt.tight_layout()
    plt.savefig(f"plots/average_{measure}{'_log' if log_scale else ''}.png")


def plot_initial_vs_final_quality(
    data_by_instance, equal_axis=False, output_file="initial_vs_final.png"
):
    instance_sizes = list(data_by_instance.keys())
    fig, axes = plt.subplots(2, 4, figsize=(20, 10))
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

            ax.scatter(initial_costs, final_costs, label=function_name, alpha=0.7)
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
    plt.savefig(f"plots/{output_file}")
    plt.close()


def plot_cost_vs(data_by_instance, measure="time", output_file="cost_vs_time.png"):
    instance_sizes = list(data_by_instance.keys())
    fig, axes = plt.subplots(2, 4, figsize=(20, 10))
    axes = axes.flatten()

    for idx, instance_size in enumerate(instance_sizes):
        if idx >= len(axes):
            break

        ax = axes[idx]
        functions_data = data_by_instance[instance_size]

        for function_name, runs_list in functions_data.items():
            if function_name in ["randomWalk", "randomSearch"]:
                continue
            times = []
            final_costs = []

            for runs in runs_list:
                for run in runs:
                    times.append(run[measure])
                    final_costs.append(run["solution"]["cost"])

            ax.scatter(times, final_costs, label=function_name, alpha=0.7)

        ax.set_title(f"Instance Size: {instance_size}")
        ax.set_xlabel(measure)
        ax.set_ylabel("Final Solution Cost")
        ax.legend()
        ax.grid()

    for idx in range(len(instance_sizes), len(axes)):
        fig.delaxes(axes[idx])

    plt.tight_layout()
    plt.savefig(f"plots/{output_file}")
    plt.close()


def plot_cost_vs_similarity(
    data_by_instance, optimal_solutions, output_file="cost_vs_similarity.png"
):
    """
    Plots a scatter plot of solution cost vs. similarity to the optimal solution.
    """
    instance_sizes = list(data_by_instance.keys())
    fig, axes = plt.subplots(2, 4, figsize=(20, 10))
    axes = axes.flatten()

    for idx, instance_size in enumerate(instance_sizes):
        ax = axes[idx]
        functions_data = data_by_instance[instance_size]
        optimal_solution = optimal_solutions[instance_size]["permutation"]

        for function_name, runs_list in functions_data.items():
            costs = []
            similarities = []

            for runs in runs_list:
                for run in runs:
                    solution_cost = run["finalSolution"]["cost"]
                    solution_permutation = run["finalSolution"]["permutation"]
                    similarity = calculate_similarity(
                        optimal_solution, solution_permutation
                    )
                    costs.append(solution_cost)
                    similarities.append(similarity)

            ax.scatter(costs, similarities, label=function_name, alpha=0.7)

        ax.set_title(f"Instance Size: {instance_size}")
        ax.set_xlabel("Solution Cost")
        ax.set_ylabel("Similarity to Optimal Solution")
        ax.legend()
        ax.grid()

    for idx in range(len(instance_sizes), len(axes)):
        fig.delaxes(axes[idx])

    plt.tight_layout()
    plt.savefig(f"plots/{output_file}")
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
    burnout_results = load_results("results/burnout-results.txt")
    cost_time_by_instance, cost_time_optimas = group_data(cost_time_results)
    initial_final_by_instance = group_initial_final_data(initial_final_results)
    burnout_by_instance, burnout_optimas = group_data(burnout_results)
    plot_all_average_performance(cost_time_by_instance, cost_time_optimas)
    plot_all_average_measure(cost_time_by_instance, "time")
    plot_all_average_measure(cost_time_by_instance, "iterations")
    plot_all_average_measure(cost_time_by_instance, "evaluations")
    plot_all_average_measure(cost_time_by_instance, "time", log_scale=True)
    plot_all_average_measure(cost_time_by_instance, "iterations", log_scale=True)
    plot_all_average_measure(cost_time_by_instance, "evaluations", log_scale=True)
    plot_cost_vs(cost_time_by_instance, "time", "cost-time.png")
    plot_cost_vs(cost_time_by_instance, "iterations", "cost-iterations.png")
    plot_cost_vs(cost_time_by_instance, "evaluations", "cost-evaluations.png")
    plot_initial_vs_final_quality(
        initial_final_by_instance,
        equal_axis=True,
        output_file="initial_vs_final_equal_axis.png",
    )
    plot_initial_vs_final_quality(
        initial_final_by_instance, equal_axis=False, output_file="initial_vs_final.png"
    )
    plot_cost_vs_similarity(
        initial_final_by_instance, cost_time_optimas, "cost-similarity.png"
    )
    plot_cost_over_measure(burnout_by_instance, burnout_optimas, "time")
    plot_cost_over_measure(burnout_by_instance, burnout_optimas, "iterations")
    plot_cost_over_measure(burnout_by_instance, burnout_optimas, "evaluations")
    plot_average_cost_over_measure(
        burnout_by_instance, burnout_optimas, "time", "average_cost_over_time"
    )
    plot_average_cost_over_measure(
        burnout_by_instance,
        burnout_optimas,
        "iterations",
        "average_cost_over_iterations",
    )
    plot_average_cost_over_measure(
        burnout_by_instance,
        burnout_optimas,
        "evaluations",
        "average_cost_over_evaluations",
    )
