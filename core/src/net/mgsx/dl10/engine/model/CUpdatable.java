package net.mgsx.dl10.engine.model;

public interface CUpdatable {
	/**
	 * 
	 * @param e
	 * @param delta
	 * @return false when sequence is over
	 */
	public boolean update(EBase e, float delta);
}
