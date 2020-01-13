package com.scottlogic.swaterman.blog.sprintsat;

import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.asciitable.CWC_LongestWord;
import de.vandermeer.asciithemes.a7.A7_Grids;
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
	public static void main(String[] args) {
		Long start = System.currentTimeMillis();

		Task t1 = new Task.Tech("Auth Framework", 2);
		Task t2 = new Task("User Login", 1, 3, t1);
		Task t3 = new Task("User Signup", 3, 1, t1);
		Task t4 = new Task.Tech("Role-Level Auth", 2, t1);
		Task t5 = new Task.Tech("Store Author", 1, t1);
		Task t6 = new Task("Register Authors", 2, 1, t4);
		Task t7 = new Task("Author Names", 1, 2, t4, t5);
		Task t8 = new Task("Author Delete", 2, 2, t4, t5);

		Backlog backlog = new Backlog(t1, t2, t3, t4, t5, t6, t7, t8);
		int totalEstimate = Arrays.stream(backlog.getTasks()).mapToInt(Task::getEstimate).sum();

		List<Sprint> sprints = IntStream.rangeClosed(1, totalEstimate)
		                                .mapToObj(budget -> Solver.getOptimalSprint(backlog, budget, null))
		                                .collect(Collectors.toList());

		AsciiTable table = generateTable(backlog, sprints);
		System.out.println(table.render());

		Long end = System.currentTimeMillis();
		System.out.println(end - start);
	}

	private static AsciiTable generateTable(Backlog backlog, List<Sprint> sprints){
		AsciiTable asciiTable = new AsciiTable();
		asciiTable.getContext().setGrid(A7_Grids.minusBarPlusEquals());
		asciiTable.getRenderer().setCWC(new CWC_LongestWord());
		asciiTable.addRule();

		final List<Task> stories = backlog.getStories();

		List<String> header = new ArrayList<>();
		header.add("Budget");
		for (final Task task : stories) {
			header.add(task.getName());
		}
		header.add("Estimate");
		header.add("Value");
		asciiTable.addRow(header.toArray());
		asciiTable.addRule();

		for (int i = 0; i < sprints.size(); i++) {
			List<String> sprintRow = new ArrayList<>();
			sprintRow.add(String.valueOf(i+1));

			final Sprint sprint = sprints.get(i);
			final List<Task> selectedTasks = sprint.getTasks();
			for (final Task story : stories) {
				sprintRow.add(selectedTasks.contains(story) ? "X" : "");
			}
			sprintRow.add(String.valueOf(sprint.getEstimate()));
			sprintRow.add(String.valueOf(sprint.getValue()));

			asciiTable.addRow(sprintRow.toArray());
		}
		asciiTable.addRule();
		return asciiTable.setTextAlignment(TextAlignment.CENTER);
	}
}
