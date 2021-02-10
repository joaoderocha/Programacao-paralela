package TP1.PP;

import java.util.concurrent.Callable;

public class TaskSum implements Callable<Integer> {

	final int a;
	final int b;

	TaskSum(final int a, final int b) {
		this.a = a;
		this.b = b;
	}

	TaskSum(final int a) {
		this.a = a;
		this.b = 0;

	}

	@Override
	public Integer call() throws Exception {
		System.out.println(" " + Thread.currentThread().getName() + "somando: " + this.a + " com " + this.b);

		return this.a + this.b;
	}
}
