import json
import matplotlib.pyplot as plt
import numpy as np
from collections import defaultdict

# Load benchmark results
with open("benchmark-results.txt") as f:
    lines = f.readlines()
    benchmark_results = [json.loads(line) for line in lines]

# Group data by instanceSize and functionName
data_by_instance = defaultdict(lambda: defaultdict(list))
optimal_solutions = {}

for result in benchmark_results:
    instance_size = result["instanceSize"]
    function_name = result["functionName"]
    runs = result["bestSolutions"]
    data_by_instance[instance_size][function_name].append(runs)

    # Store the optimal solution if it exists
    if result["optimalSolution"] is not None:
        optimal_solutions[instance_size] = result["optimalSolution"]["cost"]

# Generate burnout charts for each instanceSize
for instance_size, functions_data in data_by_instance.items():
    plt.figure(figsize=(10, 6))
    plt.title(f"Cost Over Time by Function (Instance Size: {instance_size})")

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
        plt.plot(range(max_time + 1), avg_costs, label=function_name)

        # Add shading for min and max costs
        plt.fill_between(
            range(max_time + 1),
            min_costs,
            max_costs,
            alpha=0.2,
            label=f"{function_name} (min-max)",
        )

    # Plot the optimal solution as a red horizontal line if it exists
    if instance_size in optimal_solutions:
        optimal_cost = optimal_solutions[instance_size]
        plt.axhline(
            y=optimal_cost,
            color="red",
            linestyle="--",
            label=f"Optimal Solution (Cost: {optimal_cost})",
        )

    # Customize plot
    plt.xlabel("Time (ms)")
    plt.ylabel("Cost")
    plt.legend()
    plt.grid()
    plt.show()

# Print average best scores for each instanceSize and functionName
for instance_size, functions_data in data_by_instance.items():
    print(f"Instance Size: {instance_size}")
    for function_name, runs_list in functions_data.items():
        last_costs = [run[-1]["solution"]["cost"] for runs in runs_list for run in runs]
        average_best_scores = np.mean(last_costs)
        print(f"  {function_name}: {average_best_scores}")
