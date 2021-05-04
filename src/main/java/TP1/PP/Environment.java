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
	private static ConcurrentHashMap<Pair<Double, Double>, Position> environmentMesh = new ConcurrentHashMap<>();
	private static HashMap<String, Sector> currentIterationBodies = new HashMap<>();
	private static Integer bodySetSize = Integer.valueOf(0);

	private Environment(final Set<Body> bodys) {
		Environment.bodySetSize = bodys.size();
		createSectors();

		bodys.forEach((body) -> {
			final Body[] arrayBody = new Body[bodySetSize];
			final Position p = new Position(body.getX(), body.getY(), arrayBody);
			final String sector = definePositionSector(body.getPosition());
			currentIterationBodies.get(sector).insertOnSector(body);
			p.insert(body);
			environmentMesh.put(p.getPosition(), p);
		});
	}

	public static Environment getEnvironment(final Set<Body> bodys) {
		if (environment != null) {
			return environment;
		}

		return new Environment(bodys);
	}

	public static ConcurrentMap<Pair<Double, Double>, Position> getEnvironmentMesh() {
		return Environment.environmentMesh;
	}

	public static List<Body> getCurrentBodyStates() {
		final List<Body> result = new LinkedList<>();

		currentIterationBodies.values().parallelStream().map((sector) -> sector.getCorpos())
				.forEach((deques) -> result.addAll(deques));

		return result;
	}

	public static void moveBody(final Body body) {
		final Pair<Double, Double> oldPosition = body.getPosition();
		body.move();
		final Pair<Double, Double> destino = body.getPosition();
		final Integer index = body.getIndex();
		final Boolean shouldChangeSector = !sameSector(oldPosition, destino);

		if (environmentMesh.containsKey(destino)) {
			environmentMesh.get(oldPosition).remove(index);
			environmentMesh.get(destino).insert(body);

			if (Boolean.TRUE.equals(shouldChangeSector)) {
				moveSector(oldPosition, destino, body);
			}

			return;
		}

		final Body[] arrayBody = new Body[bodySetSize];
		final Position p = new Position(destino, arrayBody);

		environmentMesh.putIfAbsent(destino, p);

		if (environmentMesh.containsKey(destino)) {
			environmentMesh.get(oldPosition).remove(index);
			environmentMesh.get(destino).insert(body);

			if (Boolean.TRUE.equals(shouldChangeSector)) {
				moveSector(oldPosition, destino, body);
			}
		}
	}

	public static void clearEmptyPositions() {
		final Long numberOfElements = environmentMesh.mappingCount();
		environmentMesh.forEach(numberOfElements, (key, value) -> {
			if (value.isEmpty()) {
				environmentMesh.remove(key);
			}
		});
	}

	@Override
	public String toString() {
		final StringBuilder resp = new StringBuilder("");
		environmentMesh.forEach((posicao, p) -> {
			resp.append("Posicao x: " + posicao.getKey() + "\tPosicao y: " + posicao.getValue() + "\nCorpos:[\n");
			final Body[] b = p.getBodies();
			for (int i = 0; i < b.length; i++) {
				if (b != null) {
					resp.append("\t" + b[i] + ",\n");
				}
			}

			resp.append("\n]\n");
		});

		return resp.toString();
	}

	public static String definePositionSector(final Pair<Double, Double> position) {
		if (position.getKey() >= 0) {
			return position.getValue() >= 0 ? "Q1" : "Q4";
		}

		return position.getValue() >= 0 ? "Q2" : "Q3";
	}

	public static void createSectors() {
		currentIterationBodies.put("Q1", new Sector());
		currentIterationBodies.put("Q2", new Sector());
		currentIterationBodies.put("Q3", new Sector());
		currentIterationBodies.put("Q4", new Sector());
	}

	public static Boolean sameSector(final Pair<Double, Double> a, final Pair<Double, Double> b) {
		return Math.signum(a.getKey()) == Math.signum(b.getKey())
				&& Math.signum(a.getValue()) == Math.signum(b.getValue());

	}

	public static void moveSector(final Pair<Double, Double> oldPosition, final Pair<Double, Double> destiny,
			final Body body) {
		System.out.println("Mudei de setor: " + body);
		final String oldSector = definePositionSector(oldPosition);
		final String newSector = definePositionSector(destiny);
		currentIterationBodies.get(oldSector).removeFromSector(body);
		currentIterationBodies.get(newSector).insertOnSector(body);
	}
}
