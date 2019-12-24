package net.mgsx.dl10.engine.inputs.keyboard;

import com.badlogic.gdx.Gdx;

import net.mgsx.dl10.engine.inputs.ControllerBase;
import net.mgsx.dl10.engine.inputs.InputManager;
import net.mgsx.dl10.engine.inputs.TriggerBase;
import net.mgsx.dl10.engine.inputs.InputManager.Command;

public class KeyboardController extends ControllerBase
{
	@Override
	public String toString() {
		return "Keyboard";
	}
	@Override
	public TriggerBase learn() {
		for(int i=0 ; i<256 ; i++){
			if(Gdx.input.isKeyJustPressed(i)){
				return new KeyboardTrigger(i);
			}
		}
		return null;
	}
	@Override
	public void learnStart(Command cmd) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void learnStop() {
		// TODO Auto-generated method stub
		
	}

}
