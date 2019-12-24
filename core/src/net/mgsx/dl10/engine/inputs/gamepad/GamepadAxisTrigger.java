package net.mgsx.dl10.engine.inputs.gamepad;

public class GamepadAxisTrigger extends GamepadTrigger {
	private int axis;
	private boolean positive;
	public GamepadAxisTrigger(int axis, boolean positive) {
		super();
		this.axis = axis;
		this.positive = positive;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof GamepadAxisTrigger){
			GamepadAxisTrigger o = (GamepadAxisTrigger)obj;
			return o.axis == axis && o.positive == positive;
		}
		return super.equals(obj);
	}
	
	@Override
	public String toString() {
		return "axis " + axis + " " + (positive ? "+" : "-");
	}
	
	@Override
	public int hashCode() {
		return axis | (positive ? 1 << 16 : 0);
	}

	@Override
	public boolean isOn(GamepadController controller) {
		if(positive){
			return controller.controller.getAxis(axis) > 0.5f;
		}else{
			return controller.controller.getAxis(axis) < -0.5f;
		}
	}

	@Override
	public String format() {
		return "AXIS|" + axis + "|" + (positive ? "+" : "-");
	}
	
}
