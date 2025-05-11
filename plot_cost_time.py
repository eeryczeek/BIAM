import json
import matplotlib.pyplot as plt
import numpy as np
from collections import defaultdict

FUNCTION_COLORS = {
    "heuristic": "#000000",  # black
    "randomWalk": "#ff0000",  # red
    "randomSearch": "#0000ff",  # blue
    "localSearchGreedy": "#ffa500",  # orange
    "simulatedAnnealing": "#008000",  # green
    "localSearchSteepest": "#800080",  # purple
    "tabuSearch": "#00ced1",  # dark turquoise
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


def scaled_distance(cost, optimal_cost):
    """Calculates the fitness as (cost - optimal) / optimal."""
    return (cost - optimal_cost) / optimal_cost


def distance_by_instance(
    data_by_instance,
    optimal_solutions,
    output_file="average-fitness",
    only_search=False,
):
    """Plots the average fitness for each algorithm across all instance sizes."""
    plt.figure(figsize=(12, 8))
    aggregated_fitness = defaultdict(list)
    aggregated_std = defaultdict(list)

    for instance_size, functions_data in data_by_instance.items():
        if instance_size not in optimal_solutions:
            continue

        optimal_value = optimal_solutions[instance_size]["cost"]

        for function_name, runs_list in functions_data.items():
            if only_search and function_name in [
                "randomWalk",
                "randomSearch",
                "heuristic",
            ]:
                continue
            final_costs = [run["cost"] for runs in runs_list for run in runs]
            distance_values = [
                (cost - optimal_value) / optimal_value for cost in final_costs
            ]
            aggregated_fitness[function_name].append(np.mean(distance_values))
            aggregated_std[function_name].append(np.std(distance_values))

    for function_name in aggregated_fitness.keys():
        avg_fitness = aggregated_fitness[function_name]
        std_fitness = aggregated_std[function_name]
        plt.errorbar(
            list(optimal_solutions.keys()),
            avg_fitness,
            yerr=std_fitness,
            label=function_name,
            capsize=5,
            color=FUNCTION_COLORS.get(function_name, "black"),
        )

    plt.xlabel("Instance Size")
    plt.ylabel("Average Scaled Distance (with Std Dev)")
    plt.legend()
    plt.grid()
    plt.tight_layout()
    plt.savefig(f"plots/{output_file}.png")


def plot_all_average_measure(
    data_by_instance, measure, log_scale=False, output_file="average"
):
    plt.figure(figsize=(12, 8))

    aggregated_times = defaultdict(list)
    aggregated_std = defaultdict(list)

    for instance_size, functions_data in data_by_instance.items():
        for function_name, runs_list in functions_data.items():
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
            color=FUNCTION_COLORS.get(function_name, "black"),
        )

    plt.xticks(x, data_by_instance.keys())
    plt.xlabel("Instance Size")
    if log_scale:
        plt.yscale("log")
        plt.ylabel(f"Average {measure} (ms, log scale)")
    else:
        plt.ylabel(f"Average {measure}")
    plt.legend()
    plt.grid(axis="y", which="both", linestyle="--", linewidth=0.5)
    plt.tight_layout()
    plt.savefig(f"plots/{output_file}-{measure}{'-log' if log_scale else ''}.png")
    plt.close()


def scaled_distance_vs(
    data_by_instance, optimal_solutions, measure="time", output_file="fitness-vs"
):
    """Plots a scatter plot of solution fitness vs. a given measure (e.g., time)."""
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
            measures = []
            distances = []

            for runs in runs_list:
                for run in runs:
                    measure_value = run[measure]
                    solution_cost = run["cost"]

                    distance = scaled_distance(solution_cost, optimal_cost)

                    measures.append(measure_value)
                    distances.append(distance)

            ax.scatter(
                np.array(measures) + np.random.uniform(-0.1, 0.1, size=len(measures)),
                np.array(distances)
                + np.random.uniform(-0.01, 0.01, size=len(distances)),
                label=function_name,
                alpha=0.7,
                color=FUNCTION_COLORS.get(function_name, "black"),
            )

        ax.set_title(f"Instance Size: {instance_size}")
        ax.set_xlabel(measure.capitalize())
        ax.set_ylabel("Scaled Distance to Optimal Solution")
        ax.legend()
        ax.grid()

    for idx in range(len(instance_sizes), len(axes)):
        fig.delaxes(axes[idx])

    plt.tight_layout()
    plt.savefig(f"plots/{output_file}-{measure}.png")
    plt.close()


def distance_vs_only_search(
    data_by_instance, optimal_solutions, measure="time", output_file="fitness-vs"
):
    """Plots a scatter plot of solution fitness vs. a given measure (e.g., time)."""
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
            if function_name in ["randomWalk", "randomSearch", "heuristic"]:
                continue
            measures = []
            distances = []

            for runs in runs_list:
                for run in runs:
                    measure_value = run[measure]
                    solution_cost = run["cost"]

                    distance = scaled_distance(solution_cost, optimal_cost)

                    measures.append(measure_value)
                    distances.append(distance)

            ax.scatter(
                np.array(measures) + np.random.uniform(-0.1, 0.1, size=len(measures)),
                np.array(distances)
                + np.random.uniform(-0.01, 0.01, size=len(distances)),
                label=function_name,
                alpha=0.7,
                color=FUNCTION_COLORS.get(function_name, "black"),
            )

        ax.set_title(f"Instance Size: {instance_size}")
        ax.set_xlabel(measure.capitalize())
        ax.set_ylabel("Scaled Distance to Optimal Solution")
        ax.legend()
        ax.grid()

    for idx in range(len(instance_sizes), len(axes)):
        fig.delaxes(axes[idx])

    plt.tight_layout()
    plt.savefig(f"plots/{output_file}-{measure}-search.png")
    plt.close()


if __name__ == "__main__":
    cost_time_results = load_results("results/cost-time-results.txt")
    cost_time_by_instance, cost_time_optimas = group_data(cost_time_results)

    distance_by_instance(cost_time_by_instance, cost_time_optimas)
    distance_by_instance(
        cost_time_by_instance,
        cost_time_optimas,
        output_file="average-fitness-only-search",
        only_search=True,
    )
    plot_all_average_measure(cost_time_by_instance, "time")
    plot_all_average_measure(cost_time_by_instance, "iterations")
    plot_all_average_measure(cost_time_by_instance, "evaluations")
    plot_all_average_measure(cost_time_by_instance, "time", log_scale=True)
    plot_all_average_measure(cost_time_by_instance, "iterations", log_scale=True)
    plot_all_average_measure(cost_time_by_instance, "evaluations", log_scale=True)
    scaled_distance_vs(cost_time_by_instance, cost_time_optimas, "time")
    distance_vs_only_search(cost_time_by_instance, cost_time_optimas, "time")
    scaled_distance_vs(cost_time_by_instance, cost_time_optimas, "iterations")
    scaled_distance_vs(cost_time_by_instance, cost_time_optimas, "evaluations")
