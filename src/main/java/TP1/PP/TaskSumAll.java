package TP1.PP;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

public class TaskSumAll implements Callable<Integer> {

	final ConcurrentLinkedQueue<Integer> auxiliar = new ConcurrentLinkedQueue<>();
	final ExecutorService threadPool;

	TaskSumAll(final ExecutorService threadPool, final List<Integer> lista) {
		auxiliar.addAll(lista);
		this.threadPool = threadPool;
	}

	@Override
	public Integer call() throws Exception {

		do {

			final List<Callable<Integer>> tasks = buildTasks(auxiliar);

			submitTasks(this.threadPool, tasks);

		} while (auxiliar.size() != 1);

		return auxiliar.poll();
	}

	private void submitTasks(final ExecutorService threadPool, final List<Callable<Integer>> tasks) {
		tasks.parallelStream().map(task -> threadPool.submit(task)).collect(Collectors.toList()).forEach(fut -> {
			try {
				this.auxiliar.add(fut.get());
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		});
	}

	private List<Callable<Integer>> buildTasks(final ConcurrentLinkedQueue<Integer> a) {
		final List<Callable<Integer>> runables = new ArrayList<>();

		do {
			final Integer aux = a.poll();
			final Integer aux2 = a.poll();

			if (aux2 != null) {
				runables.add(new TaskSum(aux, aux2));
			} else {
				runables.add(new TaskSum(aux));
			}

		} while (!a.isEmpty());

		return runables;
	}

}
