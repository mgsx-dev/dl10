package net.mgsx.dl10.engine.inputs.gamepad;

public class GamepadSliderTrigger extends GamepadTrigger
{
	public int slider;
	public boolean vertical;
	
	public GamepadSliderTrigger(int slider, boolean vertical) {
		super();
		this.slider = slider;
		this.vertical = vertical;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof GamepadSliderTrigger){
			GamepadSliderTrigger o = (GamepadSliderTrigger)obj;
			return o.slider == slider && o.vertical == vertical;
		}
		return super.equals(obj);
	}
	
	@Override
	public int hashCode() {
		return slider | (vertical ? 1 << 16 : 0);
	}
	
	@Override
	public String toString() {
		return "slider " + slider + " " + (vertical ? "Y" : "X");
	}
	
	@Override
	public boolean isOn(GamepadController controller) {
		if(vertical){
			return controller.controller.getSliderY(slider);
		}else{
			return controller.controller.getSliderX(slider);
		}
	}

	@Override
	public String format() {
		return "SLIDER|" + slider + "|" + (vertical ? "y" : "x");
	}
}
