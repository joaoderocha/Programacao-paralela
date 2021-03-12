package TP1.PP;

import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import javafx.util.Pair;

public final class Environment {
	private static Environment environment;
	private static ConcurrentHashMap<Pair<Double, Double>, Vector<Body>> environmentMesh = new ConcurrentHashMap<>();
	private static Integer bodySetSize = 0;

	private Environment(final Set<Body> bodys) {
		Environment.bodySetSize = bodys.size();

		bodys.forEach((body) -> {
			final Vector<Body> arrayBody = new Vector<>(bodySetSize);

			final Pair<Double, Double> destino = new Pair<>(body.getX(), body.getY());

			environmentMesh.put(destino, arrayBody);
			environmentMesh.get(destino).set(body.getIndex(), body);
		});

	}

	public static Environment getEnvironment(final Set<Body> bodys) {
		if (environment != null) {
			return environment;
		}

		return new Environment(bodys);
	}

	public static void moveBody(final Body body, final Pair<Double, Double> destino) {
		if (environmentMesh.containsKey(destino)) {
			body.setPosition(destino);
			environmentMesh.get(destino).set(body.getIndex(), body);

			return;
		}

		final Vector<Body> arrayBody = new Vector<>(bodySetSize);

		environmentMesh.putIfAbsent(destino, arrayBody);

		if (environmentMesh.containsKey(destino)) {
			body.setPosition(destino);
			environmentMesh.get(destino).set(body.getIndex(), body);
		}
	}
}
