package net.mgsx.dl10.engine.inputs.gamepad;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.controllers.PovDirection;

import net.mgsx.dl10.engine.inputs.ControllerBase;
import net.mgsx.dl10.engine.inputs.InputManager.Command;
import net.mgsx.dl10.engine.inputs.TriggerBase;

public class GamepadController extends ControllerBase
{
	Controller controller;
	
	protected GamepadTrigger lastTrigger;

	private ControllerAdapter listener;
	
	public GamepadController(Controller controller) {
		super();
		this.controller = controller;
	}
	
	@Override
	public String toString() {
		return controller.getName();
	}

	@Override
	public TriggerBase learn() {
		/*
		for(int i=0 ; i<256 ; i++){
			float axisValue = controller.getAxis(i);
			if(axisValue > 0.5f){
				return new GamepadAxisTrigger(i, true);
			}else if(axisValue < -0.5f){
				return new GamepadAxisTrigger(i, false);
			}
		}
		for(int i=0 ; i<256 ; i++){
			if(controller.getButton(i)){
				return new GamepadButtonTrigger(i);
			}
		}
		for(int i=0 ; i<256 ; i++){
			if(controller.getSliderX(i)){
				return new GamepadSliderTrigger(i, false);
			}
			if(controller.getSliderY(i)){
				return new GamepadSliderTrigger(i, true);
			}
		}
		for(int i=0 ; i<256 ; i++){
			PovDirection pd = controller.getPov(i);
			if(pd != PovDirection.center) System.out.println(pd);
		}
		*/
		GamepadTrigger trigger = lastTrigger;
		lastTrigger = null;
		return trigger;
	}

	@Override
	public void learnStart(final Command cmd) {
		
		controller.addListener(listener = new ControllerAdapter(){
			@Override
			public boolean axisMoved(Controller controller, int axisIndex, float value) {
				if(value > 0.5f){
					lastTrigger = new GamepadAxisTrigger(axisIndex, true);
				}else if(value < -0.5f){
					lastTrigger = new GamepadAxisTrigger(axisIndex, false);
				}
				return false;
			}
			@Override
			public boolean buttonDown(Controller controller, int buttonIndex) {
				lastTrigger = new GamepadButtonTrigger(buttonIndex);
				return false;
			}
			@Override
			public boolean povMoved(Controller controller, int povIndex, PovDirection value) {
				switch(value){
				case north:
				case east:
				case west:
				case south:
					lastTrigger = new GamepadPovTrigger(povIndex, value);
					break;
				default:
					break;
				}
				return false;
			}
			@Override
			public boolean xSliderMoved(Controller controller, int sliderIndex, boolean value) {
				lastTrigger = new GamepadSliderTrigger(sliderIndex, false);
				return false;
			}
			@Override
			public boolean ySliderMoved(Controller controller, int sliderIndex, boolean value) {
				lastTrigger = new GamepadSliderTrigger(sliderIndex, true);
				return false;
			}
			
		});
	}

	@Override
	public void learnStop() {
		if(listener != null){
			controller.removeListener(listener);
			listener = null;
			lastTrigger = null;
		}
	}

}
