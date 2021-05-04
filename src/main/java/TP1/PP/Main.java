package TP1.PP;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import javafx.util.Pair;

public class Main {
	private static Integer bodySize = 30;
	private static Integer environmentMaxMassSize = 750000;
	private static Integer environmentMinMassSize = 300000;
	private static Integer numberOfIterations = 3;

	public static void main(final String[] args) throws Exception {
		final Set<Body> bodys = new HashSet<>(bodySize);

		for (int i = 0; i < bodySize; i++) {
			final Pair<Double, Double> position = Physics.generateRandomVector();
			final Pair<Double, Double> vector = Physics.generateRandomVector();
			final Double mass = generateMass(environmentMaxMassSize, environmentMinMassSize);
			final String nome = generateNome();

			bodys.add(new Body(position.getKey(), position.getValue(), vector.getKey(), vector.getValue(), i, nome,
					mass));
		}

		final Environment environment = Environment.getEnvironment(bodys);

		runSystem(numberOfIterations, environment);
	}

	public static void runSystem(final Integer T, final Environment environment) throws InterruptedException {
		final ExecutorService threadPool = Executors.newCachedThreadPool();

		for (int i = 0; i < T; i++) {
			System.out.println("Iteracao T = " + i + "\n");

			final List<Body> currentBodyStates = Environment.getCurrentBodyStates();

			final List<Future<Boolean>> futures = new LinkedList<>();

			currentBodyStates.forEach(body -> {
//				System.out.println("Calculando influencias de corpos sobre" + body + "\n");
				try {
					//
					body.setVector(Physics.calculateNewDirection(body, currentBodyStates));
				} catch (final Exception e) {
					e.printStackTrace();
				}
			});

			currentBodyStates.forEach(body -> {
				final Callable<Boolean> fut = new Callable<Boolean>() {

					@Override
					public Boolean call() {
						System.out.println("Movendo corpos no espaco...");
						Environment.moveBody(body);

						return true;
					}
				};
				futures.add(threadPool.submit(fut));
			});

			final Boolean ok = syncronize(futures);

			if (ok) {
				System.out.println("Corpos movidos");
			}

			Environment.clearEmptyPositions();

			System.out.println("Fim da iteracao T = " + i + "\n");
			System.out.println(environment);
		}

		threadPool.shutdown();
	}

	public static Boolean syncronize(final List<Future<Boolean>> futures) {
		final List<Boolean> resultados = futures.parallelStream().map(fut -> {
			try {
				return fut.get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
			return false;
		}).collect(Collectors.toList());

		return resultados.stream().reduce(true, (a, b) -> a && b);
	}

	public static void calculaDestino(final Body body) {
		final List<Body> currentBodyStates = Environment.getCurrentBodyStates();

		for (final Body corpo : currentBodyStates) {
			if (corpo != body) {
				final Pair<Double, Double> novaDirecao = Physics.calculateNewDirection(body, corpo);
				body.setVector(novaDirecao);
			}
		}

	}

	public static Double generateMass(final Integer max, final Integer min) {
		final Random rand = new Random();

		return rand.nextDouble() * (max - min) + min;
	}

	public static String generateNome() {
		final Random rnd = new Random();
		final StringBuilder sb = new StringBuilder("");
		final char a = (char) ('a' + rnd.nextInt(26));
		final char b = (char) ('a' + rnd.nextInt(26));
		sb.append(a);
		sb.append(b);

		return sb.toString();
	}

}
