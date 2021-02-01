package TP1.PP;

import java.util.concurrent.Callable;

public class TaskMultiply implements Callable<Integer> {

	final int a;
	final int b;

	TaskMultiply(final int a, final int b) {
		this.a = a;
		this.b = b;
	}

	TaskMultiply(final int a) {
		this.a = a;
		this.b = 1;
	}

	@Override
	public Integer call() throws Exception {
		System.out.println(Thread.currentThread().getName() + " multiplicando " + this.a + " com " + this.b);

		return this.a * this.b;
	}

}
