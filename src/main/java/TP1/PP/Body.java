package TP1.PP;

import javafx.util.Pair;

public class Body {
	private Double x;
	private Double y;
	private Double Vx;
	private Double Vy;
	private Integer index;
	private String name;
	private Integer massa;

	public Body(final Double x, final Double y, final Double vx, final Double vy, final Integer index,
			final String name, final Integer massa) {
		super();
		this.x = x;
		this.y = y;
		Vx = vx;
		Vy = vy;
		this.index = index;
		this.name = name;
		this.massa = massa;
	}

	public Double getX() {
		return x;
	}

	public void setX(final Double x) {
		this.x = x;
	}

	public Double getY() {
		return y;
	}

	public void setY(final Double y) {
		this.y = y;
	}

	public Double getVx() {
		return Vx;
	}

	public void setVx(final Double vx) {
		Vx = vx;
	}

	public Double getVy() {
		return Vy;
	}

	public void setVy(final Double vy) {
		Vy = vy;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(final Integer index) {
		this.index = index;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setPosition(final Pair<Double, Double> destino) {
		this.x = destino.getKey();
		this.y = destino.getValue();
	}

	public void setVector(final Pair<Double, Double> direcao) {
		this.Vx = direcao.getKey();
		this.Vy = direcao.getValue();
	}

	public Integer getMassa() {
		return massa;
	}

	public void setMassa(final Integer massa) {
		this.massa = massa;
	}

	@Override
	public String toString() {
		return "Body [x=" + x + ", y=" + y + ", Vx=" + Vx + ", Vy=" + Vy + ", index=" + index + ", name=" + name
				+ ", massa=" + massa + "]";
	}

}
