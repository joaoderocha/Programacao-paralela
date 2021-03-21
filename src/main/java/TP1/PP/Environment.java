package TP1.PP;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javafx.util.Pair;

public final class Environment {
	private static Environment environment;
	private static ConcurrentHashMap<Pair<Double, Double>, Body[]> environmentMesh = new ConcurrentHashMap<>();
	private static HashMap<String, Sector> currentIterationBodies = new HashMap<>();
	private static Integer bodySetSize = 0;

	private Environment(final Set<Body> bodys) {
		Environment.bodySetSize = bodys.size();
		createSectors();

		bodys.forEach((body) -> {
			final Body[] arrayBody = new Body[bodySetSize];

			final Pair<Double, Double> destino = new Pair<>(body.getX(), body.getY());
			final String sector = defineBodySector(body);
			currentIterationBodies.get(sector).insertOnSector(body);
			environmentMesh.put(destino, arrayBody);
			environmentMesh.get(destino)[body.getIndex()] = body;
		});
	}

	public static Environment getEnvironment(final Set<Body> bodys) {
		if (environment != null) {
			return environment;
		}

		return new Environment(bodys);
	}

	public static ConcurrentMap<Pair<Double, Double>, Body[]> getEnvironmentMesh() {
		return Environment.environmentMesh;
	}

	public static List<Body> getCurrentBodyStates() {
		final List<Body> result = new LinkedList<>();

		currentIterationBodies.values().parallelStream().map((sector) -> sector.getCorpos())
				.forEach((deques) -> result.addAll(deques));

		return result;
	}

	// colocar pra limpar dinamicamente
	public static void moveBody(final Body body) {
		final Pair<Double, Double> oldPosition = body.getPosition();
		body.move();
		final Pair<Double, Double> destino = body.getPosition();
		final Integer index = body.getIndex();

		if (environmentMesh.containsKey(destino)) {
			environmentMesh.get(oldPosition)[index] = null;
			environmentMesh.get(destino)[index] = body;

			return;
		}

		final Body[] arrayBody = new Body[bodySetSize];

		environmentMesh.putIfAbsent(destino, arrayBody);

		if (environmentMesh.containsKey(destino)) {
			environmentMesh.get(oldPosition)[index] = null;
			environmentMesh.get(destino)[index] = body;

		}
	}

	@Override
	public String toString() {
		final StringBuilder resp = new StringBuilder("");
		environmentMesh.forEach((posicao, arrayCorpos) -> {
			resp.append("Posicao x: " + posicao.getKey() + "\tPosicao y: " + posicao.getValue() + "\nCorpos:[\n");
			for (int i = 0; i < arrayCorpos.length; i++) {
				resp.append("\t" + arrayCorpos[i] + ",\n");
			}

			resp.append("\n]\n");
		});

		return resp.toString();
	}

	public static String defineBodySector(final Body body) {
		if (body.getX() >= 0) {
			return body.getY() >= 0 ? "Q1" : "Q4";
		}

		return body.getY() >= 0 ? "Q2" : "Q3";
	}

	public static void createSectors() {
		currentIterationBodies.put("Q1", new Sector());
		currentIterationBodies.put("Q2", new Sector());
		currentIterationBodies.put("Q3", new Sector());
		currentIterationBodies.put("Q4", new Sector());
	}
}
