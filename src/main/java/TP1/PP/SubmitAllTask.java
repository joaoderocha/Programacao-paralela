package TP1.PP;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class SubmitAllTask {

	private SubmitAllTask() {

	}

	static public List<Future<Integer>> run(final ExecutorService threadPool, final List<Callable<Integer>> tasks) {
		return tasks.parallelStream().map(task -> threadPool.submit(task)).collect(Collectors.toList());
	}

}
