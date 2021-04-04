package TP1.PP;

import javafx.util.Pair;

public class Body {

	private Double x;
	private Double y;
	private Double Vx;
	private Double Vy;
	private Double VxGlobal;
	private Double VyGlobal;
	private Integer index;
	private String name;
	private Double massa;

	/**
	 * Construtor da classe
	 *
	 * @param pos            Posição do corpo no espaço; default = (0,0)
	 * @param movementVector Vetor de movimento do corpo; default = (0,0)
	 * @param index          Index do corpo; default: -1
	 * @param name           Nome do corpo; default: body::noname
	 * @param massa          Massa do corpo; default: 1.0
	 */
	public Body(final Pair<Double, Double> pos, final Pair<Double, Double> movementVector, final Double massa,
			final Integer index, final String name) throws Exception {

		super();
		if (pos != null) {
			this.x = pos.getKey();
			this.y = pos.getValue();
		} else {
			this.x = 0.;
			this.y = 0.;
		}

		if (movementVector != null) {
			Vx = movementVector.getKey();
			Vy = movementVector.getValue();
		} else {
			Vx = 0.;
			Vy = 0.;
		}

		this.index = index == null ? -1 : index;
		this.name = name == null ? "body::noname" : name;
		if (massa == null || massa >= 0)
			this.massa = massa == null ? 1. : massa;
		else
			throw new Exception("Valor da massa negativo! massa = " + massa);

		updateGlobalVec();
	}

	/**
	 *
	 *
	 * @param x     Pos x
	 * @param y     Pos y
	 * @param vx    Vetor mov x
	 * @param vy    Vetor mov y
	 * @param index
	 * @param name
	 * @param massa
	 * @throws Exception
	 */
	public Body(final Double x, final Double y, final Double vx, final Double vy, final Integer index,
			final String name, final Double massa) throws Exception {

		super();
		this.x = x == null ? 0. : x;
		this.y = y == null ? 0. : y;
		Vx = vx == null ? 0. : vx;
		Vy = vy == null ? 0. : vy;

		this.index = index == null ? -1 : index;
		this.name = name == null ? "body::noname" : name;

		if (massa == null || massa >= 0)
			this.massa = massa == null ? 1. : massa;
		else
			throw new Exception("Valor da massa negativo! massa = " + massa);

		updateGlobalVec();
	}

	public Body(final Pair<Double, Double> pos, final Pair<Double, Double> movementVector, final Double massa,
			final Integer index) throws Exception {

		this(pos, movementVector, massa, index, null);
	}

	public Body(final Pair<Double, Double> pos, final Pair<Double, Double> movementVector, final Double massa)
			throws Exception {

		this(pos, movementVector, massa, null, null);
	}

	public Body(final Pair<Double, Double> pos, final Pair<Double, Double> movementVector) throws Exception {

		this(pos, movementVector, null, null, null);
	}

	public Body(final Pair<Double, Double> pos) throws Exception {

		this(pos, null, null, null, null);
	}

	public Body() throws Exception {

		this(null, null, null, null, null);
	}

	// ----------------------------------------------------------------------------------------------------------------

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

	/**
	 * Aplica um prefixo no nome
	 *
	 * @param prefix  prefixo desejado
	 * @param replace se troca o prefixo atual ou não. default = false
	 */
	public void setNamePrefix(final String prefix, final boolean replace) {
		if (replace) {
			this.name = prefix + "::" + this.name.split("::")[1];
		} else {
			this.name = prefix + "::" + name;
		}
	}

	public void setNamePrefix(final String prefix) {
		setNamePrefix(prefix, false);
	}

	public String getNamePrefix() {
		return this.name.split("::")[1];
	}

	public void setPosition(final Pair<Double, Double> destino) {
		this.x = destino.getKey();
		this.y = destino.getValue();
	}

	/**
	 * Seta o vetor de direção
	 *
	 * @param direcao  Direção a ser setada
	 * @param isGlobal Se o vetor é global ou não; Default = true
	 */
	public void setVector(final Pair<Double, Double> direcao, final boolean isGlobal) {

		if (!isGlobal) {
			this.Vx = direcao.getKey();
			this.Vy = direcao.getValue();
			updateGlobalVec();
		} else {
			this.VxGlobal = direcao.getKey();
			this.VyGlobal = direcao.getValue();
			final Pair<Double, Double> p = Body.globalToLocalVector(new Pair<Double, Double>(this.x, this.y), direcao);
			this.Vx = p.getKey();
			this.Vy = p.getValue();
		}

	}

	public void setVector(final Pair<Double, Double> direcao) {

		this.Vx = direcao.getKey();
		this.Vy = direcao.getValue();
		updateGlobalVec();
	}

	/**
	 * Move o corpo de acordo com seu vetor de movimento
	 */
	public void move() {
		this.x = this.VxGlobal;
		this.y = this.VyGlobal;
		updateGlobalVec();
	}

	public Pair<Double, Double> getPosition() {
		return new Pair<>(this.x, this.y);
	}

	/**
	 * get vetor de movimento
	 *
	 * @param global Se o vetor desejado é global ou local. Default = true (global)
	 * @return
	 */
	public Pair<Double, Double> getVector(final boolean global) {
		if (global)
			return new Pair<>(this.VxGlobal, this.VyGlobal);
		else
			return new Pair<>(this.Vx, this.Vy);
	}

	public Pair<Double, Double> getVector() {
		return new Pair<>(this.VxGlobal, this.VyGlobal);
	}

	public Pair<Double, Double> getLocalVector() {
		return new Pair<>(this.Vx, this.Vy);
	}

	public Pair<Double, Double> getGlobalVector() {
		return new Pair<>(this.VxGlobal, this.VyGlobal);
	}

	public Double getMassa() {
		return massa;
	}

	public void setMassa(final Double massa) {
		this.massa = massa;
	}

	@Override
	public String toString() {
		return "Body [x=" + x + ", y=" + y + ", Vx=" + Vx + ", Vy=" + Vy + ", index=" + index + ", name=" + name
				+ ", massa=" + massa + "]";
	}

	private void updateGlobalVec() {

		final Pair<Double, Double> p = Body.localToGlobalVector(new Pair<Double, Double>(this.x, this.y),
				new Pair<Double, Double>(this.Vx, this.Vy));
		this.VxGlobal = p.getKey();
		this.VyGlobal = p.getValue();
	}

	private static Pair<Double, Double> localToGlobalVector(final Pair<Double, Double> position,
			final Pair<Double, Double> vec) {
		return new Pair<>(position.getKey() + vec.getKey(), position.getValue() + vec.getValue());
	}

	private static Pair<Double, Double> globalToLocalVector(final Pair<Double, Double> position,
			final Pair<Double, Double> vec) {
		return new Pair<>(-position.getKey() + vec.getKey(), -position.getValue() + vec.getValue());
	}

}
