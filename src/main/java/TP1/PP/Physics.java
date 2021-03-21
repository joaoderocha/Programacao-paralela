package TP1.PP;

import java.util.Random;

import javafx.util.Pair;

public class Physics {

	public static final double GConstant = 6.673e-11;
	public static final Pair<Double, Double> IndentityNegX = new Pair<Double, Double>(-1.0, 0.0);
	public static final Pair<Double, Double> IndentityPosX = new Pair<Double, Double>(1.0, 0.0);
	public static final Pair<Double, Double> IndentityNegY = new Pair<Double, Double>(0.0, -1.0);
	public static final Pair<Double, Double> IndentityPosY = new Pair<Double, Double>(0.0, 1.0);

	/**
	 * Pega o vetor s do body e calcula a velocidade resultante (derivada)
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

		return new Pair<Double, Double>(x, y);
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
		return GConstant * a.getMassa() * b.getMassa() / (Math.pow(Physics.calculateDistance(a, b), 2));
	}

	/**
	 * Converte o vetor atual no formato global para o formato local
	 *
	 * @param a Corpo alvo
	 * @return Vetor local
	 */
	public static Pair<Double, Double> getLocalVector(final Body a) {
		return new Pair<Double, Double>(a.getVx() - a.getX(), a.getVy() - a.getY());
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
		return new Pair<Double, Double>(position.getKey() + vec.getKey(), position.getValue() + vec.getValue());
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
		return new Pair<Double, Double>(vector1.getKey() + vector2.getKey(), vector1.getValue() + vector2.getValue());
	}

	/**
	 * Calcula o ângulo entre dois vetores em graus
	 *
	 * @param vector1
	 * @param vector2
	 * @return Ângulo entre dois vetores em graus
	 */
	public static Double getAngle(final Pair<Double, Double> vector1, final Pair<Double, Double> vector2) {

		final Double cosT = Physics.multiplyVectors(vector1, vector2) / Physics.getVectorMagnitude(vector1)
				* Physics.getVectorMagnitude(vector2);

		return Math.acos(cosT);
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

		final Double tamanho = Physics.calculateGForce(a, b);
		final Pair<Double, Double> vector_to_b = Physics.globalToLocalVector(a.getPosition(), b.getPosition());

		Double angle = 0.0;
		angle = Physics.getAngle(vector_to_b, IndentityPosX);

		final Double senT = Math.sin(angle);
		final Double cosT = Math.cos(angle);

		final Double yG = senT * tamanho;
		final Double xG = cosT * tamanho;
		final Pair<Double, Double> vectorG = new Pair<Double, Double>(xG, yG);

		return Physics.sumVectors(vectorG, a.getVector());
	}
}
