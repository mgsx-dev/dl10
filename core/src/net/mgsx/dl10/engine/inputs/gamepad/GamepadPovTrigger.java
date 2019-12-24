package net.mgsx.dl10.engine.inputs.gamepad;

import com.badlogic.gdx.controllers.PovDirection;

public class GamepadPovTrigger extends GamepadTrigger {
	private int pov;
	private PovDirection direction;
	public GamepadPovTrigger(int pov, PovDirection direction) {
		super();
		this.pov = pov;
		this.direction = direction;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof GamepadPovTrigger){
			GamepadPovTrigger o = (GamepadPovTrigger)obj;
			return o.pov == pov && o.direction == direction;
		}
		return super.equals(obj);
	}
	
	@Override
	public String toString() {
		return "pov " + pov + " " + direction.toString();
	}
	
	@Override
	public int hashCode() {
		return pov | (direction.ordinal() << 16);
	}
	@Override
	public boolean isOn(GamepadController controller) {
		// TODO not true
		return controller.controller.getPov(pov) == direction; 
	}

	@Override
	public String format() {
		return "POV|" + pov + "|" + direction.toString();
	}
}
