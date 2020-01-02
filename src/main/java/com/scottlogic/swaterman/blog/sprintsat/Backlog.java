package com.scottlogic.swaterman.blog.sprintsat;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Backlog {
	private final Task[] tasks;

	public Backlog(final Task... tasks) {
		this.tasks = tasks;
		for (int i = 0; i < this.tasks.length; i++) {
			tasks[i].setIndex(i + 1);
		}
	}

	public Task[] getTasks() {
		return tasks;
	}

	public List<Task> getStories(){
		return Arrays.stream(tasks).filter(task -> task.getValue() != 0).collect(Collectors.toList());
	}
}
