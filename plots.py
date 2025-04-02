import json
import matplotlib.pyplot as plt
import numpy as np
from collections import defaultdict
from scipy.stats import pearsonr


def load_results(file_path):
    with open(file_path) as f:
        lines = f.readlines()
    return [json.loads(line) for line in lines]


def group_data(benchmark_results):
    """Groups benchmark results by instance size and function name."""
    data_by_instance = defaultdict(lambda: defaultdict(list))
    optimal_solutions = {}

    for result in benchmark_results:
        instance_size = result["instanceSize"]
        function_name = result["functionName"]
        runs = result["bestSolutions"]
        data_by_instance[instance_size][function_name].append(runs)

        if result["optimalSolution"] is not None:
            optimal_solutions[instance_size] = result["optimalSolution"]["cost"]

    return data_by_instance, optimal_solutions


def plot_cost_over_time(
    data_by_instance, optimal_solutions, output_file="burnout_charts.png"
):
    """Plots burnout charts for all instance sizes and saves them to a single file in a 2x4 layout."""
    # Determine the number of instance sizes
    instance_sizes = list(data_by_instance.keys())
    num_instance_sizes = len(instance_sizes)

    # Create a 2x4 layout (adjust rows/columns as needed)
    rows = 2
    cols = 4
    fig, axes = plt.subplots(rows, cols, figsize=(20, 10))
    axes = axes.flatten()

    for idx, instance_size in enumerate(instance_sizes):
        if idx >= len(axes):
            break  # Stop if there are more instance sizes than subplots

        ax = axes[idx]
        functions_data = data_by_instance[instance_size]

        for function_name, runs_list in functions_data.items():
            max_time = 0
            all_costs = []

            for runs in runs_list:
                for run in runs:
                    time_cost_pairs = [(entry["time"], entry["cost"]) for entry in run]
                    max_time = max(max_time, max(t[0] for t in time_cost_pairs))
                    all_costs.append(time_cost_pairs)

            interpolated_costs = []
            for costs in all_costs:
                times, values = zip(*costs)
                interpolated = []
                last_value = values[0]
                for t in range(max_time + 1):
                    if t in times:
                        last_value = values[times.index(t)]
                    interpolated.append(last_value)
                interpolated_costs.append(interpolated)

            avg_costs = np.mean(interpolated_costs, axis=0)
            min_costs = np.min(interpolated_costs, axis=0)
            max_costs = np.max(interpolated_costs, axis=0)

            ax.plot(range(max_time + 1), avg_costs, label=function_name)

            ax.fill_between(
                range(max_time + 1),
                min_costs,
                max_costs,
                alpha=0.2,
                label=f"{function_name} (min-max)",
            )

        if instance_size in optimal_solutions:
            optimal_cost = optimal_solutions[instance_size]
            ax.axhline(
                y=optimal_cost,
                color="red",
                linestyle="--",
                label=f"Optimal Solution (Cost: {optimal_cost})",
            )

        ax.set_title(f"Instance Size: {instance_size}")
        ax.set_xlabel("Time (ms)")
        ax.set_ylabel("Cost")
        ax.legend()
        ax.grid()

    for idx in range(len(instance_sizes), len(axes)):
        fig.delaxes(axes[idx])

    plt.tight_layout()
    plt.savefig(output_file)
    plt.close()
    print(f"Burnout charts saved to {output_file}")


def plot_all_algorithms_average_performance(data_by_instance, optimal_solutions):
    """Plots all algorithms' average fitness across all instance sizes on a single plot, including standard deviation as error bars."""
    plt.figure(figsize=(10, 6))
    plt.title("Comparison of Algorithms' Average Fitness Across All Instance Sizes")

    aggregated_fitness = defaultdict(list)
    aggregated_std = defaultdict(list)

    for instance_size, functions_data in data_by_instance.items():
        if instance_size not in optimal_solutions:
            print(
                f"Skipping instance size {instance_size} as no optimal solution is available."
            )
            continue

        optimal_value = optimal_solutions[instance_size]

        for function_name, runs_list in functions_data.items():
            final_costs = [run["cost"] for runs in runs_list for run in runs]

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

    # Customize the plot
    plt.xlabel("Instance Size")
    plt.ylabel("Average Fitness (with Std Dev)")
    plt.legend()
    plt.grid()
    plt.savefig("average_performance.png")


def plot_all_algorithms_average_running_times(data_by_instance, optimal_solutions):
    """Plots all algorithms' average running times across all instance sizes using a bar plot with a logarithmic scale."""
    plt.figure(figsize=(12, 8))
    plt.title(
        "Comparison of Algorithms' Average Running Times Across All Instance Sizes"
    )

    aggregated_times = defaultdict(list)
    aggregated_std = defaultdict(list)

    for instance_size, functions_data in data_by_instance.items():
        if instance_size not in optimal_solutions:
            print(
                f"Skipping instance size {instance_size} as no optimal solution is available."
            )
            continue

        for function_name, runs_list in functions_data.items():
            # Extract running times from the data
            running_times = [entry["time"] for runs in runs_list for entry in runs]

            aggregated_times[function_name].append(np.mean(running_times))
            aggregated_std[function_name].append(np.std(running_times))

    instance_sizes = list(optimal_solutions.keys())
    bar_width = 0.2  # Width of each bar
    x = np.arange(len(instance_sizes))  # X positions for instance sizes

    for i, (function_name, avg_times) in enumerate(aggregated_times.items()):
        std_times = aggregated_std[function_name]
        plt.bar(
            x + i * bar_width,
            avg_times,
            yerr=std_times,
            width=bar_width,
            label=function_name,
            capsize=5,
        )

    # Customize x-axis ticks to align with grouped bars
    plt.xticks(x + (len(aggregated_times) - 1) * bar_width / 2, instance_sizes)
    plt.xlabel("Instance Size")
    plt.ylabel("Average Running Time (ms)")
    plt.yscale("log")  # Set y-axis to logarithmic scale
    plt.legend()
    plt.grid(
        axis="y", which="both", linestyle="--", linewidth=0.5
    )  # Grid for log scale
    plt.tight_layout()
    plt.savefig("average_running_times.png")


if __name__ == "__main__":
    cost_time_results = load_results("cost-time-results.txt")
    # burnout_results = load_results("burnout-results.txt")
    cost_time_by_instance, cost_time_optimas = group_data(cost_time_results)
    # burnout_by_instance, burnout_optimas = group_data(burnout_results)
    plot_all_algorithms_average_performance(cost_time_by_instance, cost_time_optimas)
    plot_all_algorithms_average_running_times(cost_time_by_instance, cost_time_optimas)
    # plot_cost_over_time(burnout_by_instance, burnout_optimas)
    # plot_initial_vs_final_quality(data_by_instance)
    # print_average_best_scores(data_by_instance)
