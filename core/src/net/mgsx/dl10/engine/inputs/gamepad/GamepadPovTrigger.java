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
		PovDirection cdir = controller.controller.getPov(pov);
		if(direction == PovDirection.east){
			if(cdir == PovDirection.northEast) return true;
			if(cdir == PovDirection.east) return true;
			if(cdir == PovDirection.southEast) return true;
		}
		else if(direction == PovDirection.west){
			if(cdir == PovDirection.northWest) return true;
			if(cdir == PovDirection.west) return true;
			if(cdir == PovDirection.southWest) return true;
		}
		else if(direction == PovDirection.north){
			if(cdir == PovDirection.northWest) return true;
			if(cdir == PovDirection.north) return true;
			if(cdir == PovDirection.northEast) return true;
		}
		else if(direction == PovDirection.south){
			if(cdir == PovDirection.southWest) return true;
			if(cdir == PovDirection.south) return true;
			if(cdir == PovDirection.southEast) return true;
		}
		
		return controller.controller.getPov(pov) == direction; 
	}

	@Override
	public String format() {
		return "POV|" + pov + "|" + direction.toString();
	}
}
