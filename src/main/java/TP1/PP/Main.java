package TP1.PP;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Main {
	private static Integer bodySize = 10;
	private static Integer environmentXSize = 100;

	public static void main(final String[] args) {
		final Set<Body> bodys = new HashSet<>(bodySize);

		for (int i = 0; i < bodySize; i++) {
			final Double x = (Double) randomNumber(environmentXSize);

			bodys.add(new Body(null, null, null, null, i, null, i))
		}


		final Environment environment = Environment.getEnvironment(bodys);
	}

	public void runSystem(final Integer T, final Environment environment) {

	}

	public Integer randomNumber(final Integer max) {
		final Random rand = new Random();

		return rand.nextInt(max - (-1 * max)) - (-1 * max);
	}

}
