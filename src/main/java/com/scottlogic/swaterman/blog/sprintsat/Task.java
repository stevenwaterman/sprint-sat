package com.scottlogic.swaterman.blog.sprintsat;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class Task {
	private final String name;
	private final Integer estimate;
	private final Integer value;
	private final Set<Task> dependsOn;
	private int index;

	public Task(
			final String name, final Integer estimate, final Integer value, final Task... dependsOn
	           ) {
		this.name = name;
		this.estimate = estimate;
		this.value = value;
		this.dependsOn = Arrays.stream(dependsOn).collect(Collectors.toSet());
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(final int index) {
		this.index = index;
	}

	public Integer getEstimate() {
		return estimate;
	}

	public Integer getValue() {
		return value;
	}

	public String getName() {
		return name;
	}

	public Set<Task> getDependsOn() {
		return dependsOn;
	}

	@Override
	public String toString() {
		return "Task " + index + ": '" + name + "' - estimate: " + estimate + " - value: " + value;
	}

	public static class Tech extends Task {
		public Tech(String name, Integer estimate, Task... dependsOn) {
			super(name, estimate, 0, dependsOn);
		}
	}
}
