package TP1.PP;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Main {
	static final int MAX_T = 4;
	static final int V_SIZE = 100;
	static ConcurrentLinkedQueue<Integer> parcial = new ConcurrentLinkedQueue<>();

	public static void main(final String[] args) throws InterruptedException {
		final ExecutorService threadPool = Executors.newCachedThreadPool();

		for (int i = 0; i < V_SIZE; i++) {
			parcial.add(i);
		}

		do {

			final List<Callable<Integer>> tasks = buildParams(parcial);

			submitAllTasks(threadPool, tasks);

		} while (parcial.size() != 1);

		System.out.println(parcial.toString());
	}

	private static void submitAllTasks(final ExecutorService threadPool, final List<Callable<Integer>> tasks) {
		tasks.parallelStream().map(task -> threadPool.submit(task)).collect(Collectors.toList()).forEach(fut -> {
			try {
				parcial.add(fut.get());
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		});
	}

	static List<Callable<Integer>> buildParams(final ConcurrentLinkedQueue<Integer> a) {
		final List<Callable<Integer>> runables = new ArrayList<>();

		do {
			final Integer aux = a.poll();
			final Integer aux2 = a.poll();

			if (aux2 != null) {
				runables.add((new Task(aux, aux2)));
			} else {
				runables.add(new Task(aux));
			}

		} while (!a.isEmpty());

		return runables;
	}
}
