package net.mgsx.dl10.engine.inputs.gamepad;

public class GamepadButtonTrigger extends GamepadTrigger
{
	private int button;

	public GamepadButtonTrigger(int button) {
		super();
		this.button = button;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof GamepadButtonTrigger){
			return ((GamepadButtonTrigger) obj).button == button;
		}
		return super.equals(obj);
	}
	
	@Override
	public int hashCode() {
		return button;
	}
	
	@Override
	public String toString() {
		return "button " + button;
	}

	@Override
	public boolean isOn(GamepadController controller) {
		return controller.controller.getButton(button);
	}

	@Override
	public String format() {
		return "BUTTON|" + button;
	}
	
	
}
