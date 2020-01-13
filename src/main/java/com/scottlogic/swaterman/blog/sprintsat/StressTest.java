package com.scottlogic.swaterman.blog.sprintsat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class StressTest {
	private static Random random = new Random();

	public static void main(String[] args) {
		IntStream.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 15, 20, 25, 30, 35, 40, 45, 50, 60, 70, 80, 90, 100, 125, 150)
		         .flatMap(i -> IntStream.of(i, i, i, i, i, i, i, i, i, i)) // 10 per
		         .forEach(StressTest::run);
	}

	private static void run(int taskCount) {
		List<Task> tasks = new ArrayList<>();

		IntStream.range(0, taskCount).forEach(idx -> {
			int dependencyCount = Math.min(idx, random.nextInt(5));
			final Task[] dependencies = IntStream.range(0, dependencyCount)
			                                     .map(it -> random.nextInt(idx))
			                                     .mapToObj(tasks::get)
			                                     .toArray(Task[]::new);
			tasks.add(new Task(String.valueOf(idx), random.nextInt(10) + 1, random.nextInt(10), dependencies));
		});

		Backlog backlog = new Backlog(tasks.toArray(new Task[0]));

		final long now = System.currentTimeMillis();
		Solver.getOptimalSprint(backlog, taskCount, null);
		final long time = System.currentTimeMillis() - now;
		System.out.println(taskCount + ", " + time);
	}
}
