package com.scottlogic.swaterman.blog.sprintsat;

import java.util.List;
import java.util.StringJoiner;

public class Sprint {
	private final List<Task> tasks;

	public Sprint(final List<Task> tasks) {
		this.tasks = tasks;
	}

	public List<Task> getTasks() {
		return tasks;
	}

	@Override
	public String toString() {
		StringJoiner sj = new StringJoiner(System.lineSeparator());
		sj.add("Sprint:");
		tasks.forEach(task -> sj.add("    " + task.toString()));
		sj.add("Totals:");
		sj.add("    Estimate: " + getEstimate());
		sj.add("    Value: " + getValue());
		return sj.toString();
	}

	public Integer getEstimate() {
		return tasks.stream().mapToInt(Task::getEstimate).sum();
	}

	public Integer getValue() {
		return tasks.stream().mapToInt(Task::getValue).sum();
	}
}
