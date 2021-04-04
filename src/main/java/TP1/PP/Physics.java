package TP1.PP;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import javafx.util.Pair;

public class Physics {

	public static final double GConstant = 6.673e-11;
	public static final Pair<Double, Double> IndentityNegX = new Pair<>(-1.0, 0.0);
	public static final Pair<Double, Double> IndentityPosX = new Pair<>(1.0, 0.0);
	public static final Pair<Double, Double> IndentityNegY = new Pair<>(0.0, -1.0);
	public static final Pair<Double, Double> IndentityPosY = new Pair<>(0.0, 1.0);

	public static Pair<Double, Double> nullVector() {
		return new Pair<>(0.0, 0.0);
	}

	/**
	 * Pega o vetor direcional do body e calcula a velocidade resultante (derivada)
	 *
	 * @param b Body alvo
	 * @return Velocidade de movimento
	 */
	public static Double calculateSpeed(final Body b) {
		final Double vx = b.getVx();
		final Double vy = b.getVy();

		return Math.sqrt(vx * vx + vy * vy);
	}

	/**
	 * Gera um vetor aleatório com coordenadas entre -20 e 20
	 *
	 * @return Vetor aleatório
	 */
	public static Pair<Double, Double> generateRandomVector() {
		final Random r = new Random();
		final Double x = (double) r.nextInt() % 20;
		final Double y = (double) r.nextInt() % 20;

		return new Pair<>(x, y);
	}

	/**
	 * Calcula a distância entre dois corpos
	 *
	 * @param a Corpo A
	 * @param b Corpo B
	 * @return
	 */
	public static Double calculateDistance(final Body a, final Body b) {
		return Math.sqrt(Math.pow(a.getX() + b.getX(), 2) + Math.pow(a.getY() + b.getY(), 2));
	}

	/**
	 * Calcula a força G que o Body B exerce sobre o Body A
	 *
	 * @param a Corpo A
	 * @param b Corpo B
	 * @return Força G que B exerce em A
	 */
	public static Double calculateGForce(final Body a, final Body b) {
		final Double distance = Physics.calculateDistance(a, b);
		return distance > 0 ? GConstant * a.getMassa() * b.getMassa() / (Math.pow(distance, 2)) : 0;
	}

	/**
	 * Converte o vetor atual no formato global para o formato local
	 *
	 * @param a Corpo alvo
	 * @return Vetor local
	 */
	public static Pair<Double, Double> getLocalVector(final Body a) {
		return new Pair<>(a.getVx() - a.getX(), a.getVy() - a.getY());
	}

	/**
	 * Converte coordenada local para global do vetor
	 *
	 * @param position Posição do corpo
	 * @param vec      Vetor local
	 * @return Vetor global resultante
	 */
	public static Pair<Double, Double> localToGlobalVector(final Pair<Double, Double> position,
			final Pair<Double, Double> vec) {
		return new Pair<>(position.getKey() + vec.getKey(), position.getValue() + vec.getValue());
	}

	/**
	 * Converte coordenada global para local do vetor
	 *
	 * @param position Posição do corpo
	 * @param vec      Vetor global
	 * @return Vetor local resultante
	 */
	public static Pair<Double, Double> globalToLocalVector(final Pair<Double, Double> position,
			final Pair<Double, Double> vec) {
		return new Pair<Double, Double>(-position.getKey() + vec.getKey(), -position.getValue() + vec.getValue());
	}

	/**
	 * Calcula o módulo do vetor indicado
	 *
	 * @param vector
	 * @return
	 */
	public static Double getVectorMagnitude(final Pair<Double, Double> vector) {
		return Math.sqrt(Math.pow(vector.getKey(), 2) + Math.pow(vector.getValue(), 2));
	}

	/**
	 * Multiplica dois vetores e retorna a escalar
	 *
	 * @param vector1
	 * @param vector2
	 * @return Double: Produto
	 */
	public static Double multiplyVectors(final Pair<Double, Double> vector1, final Pair<Double, Double> vector2) {
		return vector1.getKey() * vector2.getKey() + vector1.getValue() * vector2.getValue();
	}

	/**
	 * Soma dois vetores e retorna o vetor resultante
	 *
	 * @param vector1
	 * @param vector2
	 * @return
	 */
	public static Pair<Double, Double> sumVectors(final Pair<Double, Double> vector1,
			final Pair<Double, Double> vector2) {
		return new Pair<>(vector1.getKey() + vector2.getKey(), vector1.getValue() + vector2.getValue());
	}

	/**
	 * Calcula o ângulo entre dois vetores em graus
	 *
	 * @param vector1
	 * @param vector2
	 * @return Ângulo entre dois vetores em graus
	 */
	public static Double getAngle(Pair<Double,Double> vector1, Pair<Double,Double> vector2) {
        Double mag1 = Physics.getVectorMagnitude(vector1);
        Double mag2 = Physics.getVectorMagnitude(vector2);
        
        if(mag1 == 0 || mag2 == 0)
            return 0.0;
        
        Double cosT = Physics.multiplyVectors(vector1, vector2) / (mag1 * mag2);
        
        return Math.acos(cosT);
    }

	/**
	 * Calcula o vetor de força resultante da atração gravitacional de B em A
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	public static Pair<Double, Double> calculateForceVector(final Body a, final Body b) {

		final Double tamanho = Physics.calculateGForce(a, b);
		final Pair<Double, Double> vector_to_b = Physics.globalToLocalVector(a.getPosition(), b.getPosition());

		Double angle = 0.0;
		if (vector_to_b.getKey() < 0)
			angle = Physics.getAngle(vector_to_b, IndentityPosX);
		else
			angle = Physics.getAngle(vector_to_b, IndentityNegX);

		final Double senT = Math.sin(angle);
		final Double cosT = Math.cos(angle);

		final Double yG = senT * tamanho;
		final Double xG = cosT * tamanho;
		return new Pair<>(xG, yG);
	}

	/**
	 * Calcula a nova direção que A terá no Environment. A massa de B e a sua
	 * distância em relação a A afetam na força de gravidade que atua em A.
	 *
	 * Não altera o valor dos Bodys em si, apenas retorna o novo vetor
	 *
	 * @param a Corpo A, corpo no qual a mudança deve ser aplicada
	 * @param b Corpo B, corpo que afeta A
	 * @return Novo vetor de direção
	 */
	public static Pair<Double, Double> calculateNewDirection(final Body a, final Body b) {

		final Pair<Double, Double> vectorG = Physics.calculateForceVector(a, b);
		return Physics.sumVectors(vectorG, a.getVector());
	}

	/**
	 * Calcula a nova direção que A terá no Environment.
	 *
	 * Não altera o valor dos Bodys em si, apenas retorna o novo vetor
	 *
	 * @param a         Corpo A, corpo no qual a mudança deve ser aplicada
	 * @param body_list Lista de corpos que vão afetar A
	 * @return Novo vetor de direção de A
	 * @throws Exception
	 */
	public static Pair<Double, Double> calculateNewDirection(final Body a, final List<Body> body_list)
			throws Exception {

		final Pair<Double, Double> vectorG = sumGravitationalVectors(a, body_list);
		return Physics.sumVectors(vectorG, a.getVector());
	}

	/**
	 * Aplica um reduce paralelo para somar os vetores resultantes da influência
	 * gravitacional de todos os outros corpos da lista para com A
	 *
	 * @param a         Corpo a ser calculado o vetor de força
	 * @param body_list Lista de corpos que interferem em A
	 * @return Vetor resultante da soma de todas as forças que todos os corpos da
	 *         lista body_list exercem em A
	 * @throws Exception
	 */
	public static Pair<Double, Double> sumGravitationalVectors(final Body a, final List<Body> body_list)
			throws Exception {

		final ExecutorService calculationsThreadPool = Executors.newCachedThreadPool(); // Threadpool para fazer os
																						// calculos
		final ExecutorService reduceThreadPool = Executors.newCachedThreadPool(); // Threadpool para fazer o reduce do
																					// resultado dos calculos
		final List<Future<Pair<Double, Double>>> tasks = new ArrayList<>();
		final ArrayList<Pair<Double, Double>> forceVectors = new ArrayList<>();

		body_list.forEach(body -> {
			if (a != body) {
				tasks.add(calculationsThreadPool.submit(new CalculationsAuxiliar(a, body)));
			}
		});

		final Iterator<Future<Pair<Double, Double>>> it = tasks.iterator();
		while (it.hasNext()) {
			forceVectors.add(it.next().get());
		}

		calculationsThreadPool.shutdown();

		final TaskSumAll tsa = new TaskSumAll(reduceThreadPool, forceVectors); // reduce
		final Pair<Double, Double> result = tsa.sumAll();
		reduceThreadPool.shutdown();

		return result;
	}

	/**
	 * Calcula o centro de massa de n objetos
	 *
	 * @param system Lista de corpos a serem considerados
	 * @return ponto do centro de massa
	 */
	public static Pair<Double, Double> centerOfMass(final List<Body> system) {

		Double totalMass = 0.;
		Double xSum = 0.;
		Double ySum = 0.;

		final Iterator<Body> it = system.iterator();
		while (it.hasNext()) {
			final Body body = it.next();
			totalMass += body.getMassa();
			xSum += body.getX() * body.getMassa();
			ySum += body.getY() * body.getMassa();
		}

		final Double xc = xSum / totalMass;
		final Double yc = ySum / totalMass;

		return new Pair<>(xc, yc);
	}

	// -------------------------------------------------------------------------------------
	// CLASSES ANINHADAS AUXILIARES:

	/**
	 * Classe auxiliar para implementação de threads no calculo da gravidade
	 */
	private static class CalculationsAuxiliar implements Callable<Pair<Double, Double>> {

		Body a;
		Body b;

		public CalculationsAuxiliar(final Body a, final Body b) {
			this.a = a;
			this.b = b;
		}

		@Override
		public Pair<Double, Double> call() throws Exception {
			return Physics.calculateForceVector(a, b);
		}

	}

	/*
	 * Classe auxiliar para a implementação do reduce
	 */
	private static class TaskSum implements Callable<Pair<Double, Double>> {

		final Pair<Double, Double> a;
		final Pair<Double, Double> b;

		TaskSum(final Pair<Double, Double> a, final Pair<Double, Double> b) {
			this.a = a;
			this.b = b;
		}

		TaskSum(final Pair<Double, Double> a) {
			this.a = a;
			this.b = Physics.nullVector();

		}

		@Override
		public Pair<Double, Double> call() throws Exception {

			return Physics.sumVectors(a, b);
		}
	}

	/*
	 * Classe auxiliar para o reduce
	 */
	private static class TaskSumAll {

		final ConcurrentLinkedQueue<Pair<Double, Double>> auxiliar = new ConcurrentLinkedQueue<>();
		final ExecutorService threadPool;

		public TaskSumAll(final ExecutorService threadPool, final List<Pair<Double, Double>> lista) {
			auxiliar.addAll(lista);
			this.threadPool = threadPool;
		}

		public Pair<Double, Double> sumAll() throws Exception {

			do {

				final List<Callable<Pair<Double, Double>>> tasks = buildTasks(auxiliar);

				submitTasks(this.threadPool, tasks);

			} while (auxiliar.size() != 1);

			return auxiliar.poll();
		}

		private void submitTasks(final ExecutorService threadPool, final List<Callable<Pair<Double, Double>>> tasks) {
			tasks.parallelStream().map(task -> threadPool.submit(task)).collect(Collectors.toList()).forEach(fut -> {
				try {
					this.auxiliar.add(fut.get());
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			});
		}

		private List<Callable<Pair<Double, Double>>> buildTasks(final ConcurrentLinkedQueue<Pair<Double, Double>> a) {
			final List<Callable<Pair<Double, Double>>> runables = new ArrayList<>();

			do {
				final Pair<Double, Double> aux = a.poll();
				final Pair<Double, Double> aux2 = a.poll();

				if (aux2 != null) {
					runables.add(new TaskSum(aux, aux2));
				} else {
					runables.add(new TaskSum(aux));
				}

			} while (!a.isEmpty());

			return runables;
		}

	}

}
