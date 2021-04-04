package TP1.PP;

import java.util.Objects;

import javafx.util.Pair;

public class Position {
	Double x;
	Double y;
	Body[] corpos;
	Integer counter = Integer.valueOf(0);

	Position(final Double x, final Double y, final Body[] bodies) {
		this.x = x;
		this.y = y;
		for (final Body body : bodies) {
			if (body != null) {
				counter++;
			}
		}
		this.corpos = bodies;
	}

	Position(final Pair<Double, Double> destino, final Body[] bodies) {
		this.x = destino.getKey();
		this.y = destino.getValue();
		for (final Body body : bodies) {
			if (body != null) {
				counter++;
			}
		}
		this.corpos = bodies;
	}

	public Pair<Double, Double> getPosition() {
		return new Pair<>(x, y);
	}

	public void insert(final Body body) {
		corpos[body.getIndex()] = body;
		counter++;
	}

	public void remove(final Integer index) {
		corpos[index] = null;
		counter--;

	}

	public Integer getCounter() {
		return this.counter;
	}

	public Body[] getBodies() {
		return this.corpos;
	}

	public Boolean isEmpty() {
		return counter == 0;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Position)) {
			return false;
		}
		final Position other = (Position) obj;
		return Objects.equals(x, other.x) && Objects.equals(y, other.y);
	}
}
