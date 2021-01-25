package TP1.PP;

import java.util.concurrent.Callable;

public class Task implements Callable<Integer> {

	final int a;
	final int b;
	int resultado;

	Task(final int a, final int b) {
		this.a = a;
		this.b = b;
		this.resultado = a + b;
	}

	Task(final int a) {
		this.a = a;
		this.b = 0;
		this.resultado = this.a;
	}

	@Override
	public Integer call() throws Exception {
		System.out.println(Thread.currentThread().getName() + "somando: " + this.a + " com " + this.b);

		return this.resultado;
	}
}
