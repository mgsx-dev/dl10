package net.mgsx.dl10.engine.model.components;

public class CBlock {

	public static final int TOP = 1;
	public static final int BOTTOM = 2;
	public static final int LEFT = 4;
	public static final int RIGHT = 8;
	public static final int ALL = 15;
	
	public int normals = 0;
	public boolean sensor;
}
