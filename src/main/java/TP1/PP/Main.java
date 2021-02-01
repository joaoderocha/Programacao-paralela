package TP1.PP;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class Main {
	static final int M_X_SIZE = 100;
	static final int M_Y_SIZE = 100;

	public static void main(final String[] args) throws InterruptedException {
		final ExecutorService threadPool = Executors.newCachedThreadPool();

		final ConcurrentHashMap<Integer, ConcurrentLinkedQueue<Integer>> matrix = buildMatrix();
		final List<Integer> array = buildArray();
		final ConcurrentHashMap<Integer, List<Future<Integer>>> promisesDaMultiplicacao = new ConcurrentHashMap<>();
		final ConcurrentHashMap<Integer, Future<Integer>> promisesDaSoma = new ConcurrentHashMap<>();

		matrix.forEach((index, pilha) -> {
			final List<Callable<Integer>> tasks = createMultiplyTask(pilha, array.get(index));

			promisesDaMultiplicacao.put(index, SubmitAllTask.run(threadPool, tasks));
		});

		promisesDaMultiplicacao.forEach((index, futures) -> {
			final List<Integer> lista = futures.parallelStream().map(fut -> {
				try {
					return fut.get();
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
				return index;
			}).collect(Collectors.toList());

			final Future<Integer> futureDaSoma = threadPool.submit(new TaskSumAll(threadPool, lista));
			promisesDaSoma.put(index, futureDaSoma);
		});

		final List<Integer> result = promisesDaSoma.values().stream().map(fut -> {
			try {
				return fut.get();
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}).collect(Collectors.toList());

		System.out.println(result.toString());
		threadPool.shutdown();
	}

	private static ConcurrentHashMap<Integer, ConcurrentLinkedQueue<Integer>> buildMatrix() {
		final ConcurrentHashMap<Integer, ConcurrentLinkedQueue<Integer>> matrix = new ConcurrentHashMap<>();

		for (int i = 0; i < M_X_SIZE; i++) {
			final ConcurrentLinkedQueue<Integer> aux = new ConcurrentLinkedQueue<>();
			for (int j = 0; j < M_Y_SIZE; j++) {
				aux.add(1);
			}

			matrix.put(i, aux);
		}

		return matrix;
	}

	private static List<Integer> buildArray() {
		final List<Integer> array = new ArrayList<Integer>();
		for (int i = 0; i < M_X_SIZE; i++) {
			array.add(2);
		}

		return array;
	}

	static List<Callable<Integer>> createMultiplyTask(final ConcurrentLinkedQueue<Integer> pilha, final int b) {
		final List<Callable<Integer>> runables = new ArrayList<>();

		do {
			final Integer aux = pilha.poll();
			final Integer aux2 = pilha.poll();

			if (aux2 != null) {
				runables.add(new TaskMultiply(aux, b));
				runables.add(new TaskMultiply(aux2, b));
			} else {
				runables.add(new TaskMultiply(aux));
			}

		} while (!pilha.isEmpty());

		return runables;
	}
}
