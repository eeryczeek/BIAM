import json
import matplotlib.pyplot as plt
import numpy as np
from collections import defaultdict
from scipy.stats import pearsonr


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

            # Collect all time-cost pairs across runs
            for runs in runs_list:
                for run in runs:
                    time_cost_pairs = [
                        (entry["time"], entry["solution"]["cost"]) for entry in run
                    ]
                    max_time = max(max_time, max(t[0] for t in time_cost_pairs))
                    all_costs.append(time_cost_pairs)

            # Interpolate missing values
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

            # Calculate averages, min, and max
            avg_costs = np.mean(interpolated_costs, axis=0)
            min_costs = np.min(interpolated_costs, axis=0)
            max_costs = np.max(interpolated_costs, axis=0)

            # Plot average costs
            ax.plot(range(max_time + 1), avg_costs, label=function_name)

            # Add shading for min and max costs
            ax.fill_between(
                range(max_time + 1),
                min_costs,
                max_costs,
                alpha=0.2,
                label=f"{function_name} (min-max)",
            )

        # Plot the optimal solution as a red horizontal line if it exists
        if instance_size in optimal_solutions:
            optimal_cost = optimal_solutions[instance_size]
            ax.axhline(
                y=optimal_cost,
                color="red",
                linestyle="--",
                label=f"Optimal Solution (Cost: {optimal_cost})",
            )

        # Customize subplot
        ax.set_title(f"Instance Size: {instance_size}")
        ax.set_xlabel("Time (ms)")
        ax.set_ylabel("Cost")
        ax.legend()
        ax.grid()

    # Remove unused subplots
    for idx in range(len(instance_sizes), len(axes)):
        fig.delaxes(axes[idx])

    # Adjust layout and save to file
    plt.tight_layout()
    plt.savefig(output_file)
    plt.close()
    print(f"Burnout charts saved to {output_file}")


def load_benchmark_results(file_path):
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
            optimal_solutions[instance_size] = result["optimalSolution"]["cost"]

    return data_by_instance, optimal_solutions


def plot_initial_vs_final_quality(data_by_instance):
    for instance_size, functions_data in data_by_instance.items():
        plt.figure(figsize=(8, 6))
        plt.title(
            f"Initial vs. Final Solution Quality (Instance Size: {instance_size})"
        )

        all_initial_qualities = []
        all_final_qualities = []

        for function_name, runs_list in functions_data.items():
            initial_qualities = []
            final_qualities = []

            for runs in runs_list:
                for run in runs:
                    initial_quality = run[0]["solution"]["cost"]  # Initial quality
                    final_quality = run[-1]["solution"]["cost"]  # Final quality
                    initial_qualities.append(initial_quality)
                    final_qualities.append(final_quality)

            plt.scatter(
                initial_qualities, final_qualities, label=function_name, alpha=0.5, s=10
            )

            # Collect data for both axes
            all_initial_qualities.extend(initial_qualities)
            all_final_qualities.extend(final_qualities)

            # Calculate and display correlation
            if len(initial_qualities) > 1:
                correlation, _ = pearsonr(initial_qualities, final_qualities)
                print(
                    f"Instance Size {instance_size}, Function {function_name}: Correlation = {correlation:.3f}"
                )

        # Find the smallest and largest values for both axes
        min_val = min(min(all_initial_qualities), min(all_final_qualities))
        max_val = max(max(all_initial_qualities), max(all_final_qualities))

        # Set the same range for both axes
        plt.xlim(min_val, max_val)
        plt.ylim(min_val, max_val)

        plt.xlabel("Initial Solution Quality")
        plt.ylabel("Final Solution Quality")
        plt.legend()
        plt.grid()
        plt.show()


def print_average_best_scores(data_by_instance):
    for instance_size, functions_data in data_by_instance.items():
        print(f"Instance Size: {instance_size}")
        for function_name, runs_list in functions_data.items():
            last_costs = [
                run[-1]["solution"]["cost"] for runs in runs_list for run in runs
            ]
            average_best_scores = np.mean(last_costs)
            print(f"  {function_name}: {average_best_scores}")


if __name__ == "__main__":
    benchmark_results = load_benchmark_results("benchmark-results.txt")
    data_by_instance, optimal_solutions = group_data(benchmark_results)
    plot_cost_over_time(data_by_instance, optimal_solutions)
    plot_initial_vs_final_quality(data_by_instance)
    print_average_best_scores(data_by_instance)
