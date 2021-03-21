package TP1.PP;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

public class Sector {
	ConcurrentLinkedDeque<Body> corpos = new ConcurrentLinkedDeque<>();

	public Sector() {
		super();
	}

	public Deque<Body> getCorpos() {
		return corpos;
	}

	public void insertOnSector(final Body corpo) {
		corpos.offer(corpo);
	}

}