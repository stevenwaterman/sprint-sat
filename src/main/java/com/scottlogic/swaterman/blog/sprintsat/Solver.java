package com.scottlogic.swaterman.blog.sprintsat;

import org.sat4j.core.VecInt;
import org.sat4j.maxsat.MinCostDecorator;
import org.sat4j.maxsat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.TimeoutException;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Solver {
	public static Sprint getOptimalSprint(Backlog input, int budget) {
		Task[] tasks = input.getTasks();
		MinCostDecorator solver = parse(tasks, budget);
		int[] indices = getOptimalAssignment(solver);
		return getSprintFromAssignment(indices, tasks);
	}

	private static Sprint getSprintFromAssignment(final int[] indices, final Task[] tasks) {
		final List<Task> selected = Arrays.stream(indices)
		                                  .filter(idx -> idx > 0) // -ve indicates not included
		                                  .mapToObj(oneIndexed -> tasks[oneIndexed - 1])
		                                  .sorted(Comparator.comparing(Task::getIndex))
		                                  .collect(Collectors.toList());
		return new Sprint(selected);
	}

	private static MinCostDecorator parse(Task[] tasks, int budget) {
		MinCostDecorator solver = new MinCostDecorator(SolverFactory.newDefault());
		solver.newVar(tasks.length);

		// The 'cost' of doing a task is -priority
		// We want to minimise the 'cost' (i.e. maximise total priority)
		for (int i = 0; i < tasks.length; i++) {
			Task task = tasks[i];
			if (task.getValue() != 0) {
				solver.setCost(i + 1, -task.getValue());
			}
		}

		// We add a pseudo-boolean constraint which says the total estimate
		// Must be at most equal to the sprint budget
		// We ignore any with estimate 0 as they break the solver
		final List<Task> nonTrivialTasks = Arrays.stream(tasks)
		                                         .filter(task -> task.getEstimate() != 0)
		                                         .collect(Collectors.toList());
		final int[] nonTrivialIds = nonTrivialTasks.stream().mapToInt(Task::getIndex).toArray();
		final int[] nonTrivialEstimates = nonTrivialTasks.stream().mapToInt(Task::getEstimate).toArray();
		try {
			solver.addAtMost(new VecInt(nonTrivialIds), new VecInt(nonTrivialEstimates), budget);
		} catch (ContradictionException e) {
			throw new IllegalArgumentException("Tasks caused Constraint Exception", e);
		}

		// We add the dependency graph as restrictions
		// A => B can be translated to (A or NOT(B))
		for (final Task task : tasks) {
			for (final Task dep : task.getDependsOn()) {
				int[] clause = {dep.getIndex(), -task.getIndex()};
				try {
					solver.addClause(new VecInt(clause));
				} catch (ContradictionException e) {
					throw new IllegalArgumentException("Tasks caused Constraint Exception", e);
				}
			}
		}

		return solver;
	}

	private static int[] getOptimalAssignment(MinCostDecorator solver) {
		try {
			while (solver.admitABetterSolution()) {
				solver.discardCurrentSolution();
			}
		} catch (ContradictionException | TimeoutException ignored) {
			// An exception is thrown when we're finished
		}
		return solver.model();
	}
}
