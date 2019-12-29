package net.mgsx.dl10.engine.model.inputs;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;

import net.mgsx.dl10.engine.inputs.InputManager;
import net.mgsx.dl10.engine.model.entities.Player;

public class PlatformerInputs extends InputManager
{
	public static enum PlayerCommand {
		LEFT, RIGHT, JUMP, DOWN
	}
	
	public PlatformerInputs(Preferences prefs) {
		super(prefs);
	}
	
	@Override
	protected void setDefault() {
		super.setDefault();
		
		addCommand(PlayerCommand.LEFT, "left", "Move left");
		addCommand(PlayerCommand.RIGHT, "right", "Move right");
		addCommand(PlayerCommand.JUMP, "jump", "Jump");
		addCommand(PlayerCommand.DOWN, "down", "Down");
		
		
		// default keys
		addKeys(PlayerCommand.LEFT, Input.Keys.LEFT, Input.Keys.Q, Input.Keys.A);
		addKeys(PlayerCommand.RIGHT, Input.Keys.RIGHT, Input.Keys.D);
		addKeys(PlayerCommand.JUMP, Input.Keys.UP, Input.Keys.Z, Input.Keys.W);
		addKeys(PlayerCommand.DOWN, Input.Keys.DOWN, Input.Keys.S);
	}

	public void update(Player player) 
	{
		if(controller.isOn(PlayerCommand.LEFT)){
			player.velocityTarget.x = -1;
		}else if(controller.isOn(PlayerCommand.RIGHT)){
			player.velocityTarget.x = 1;
		}else{
			player.velocityTarget.x = 0;
		}
		
		if(controller.isOn(PlayerCommand.JUMP)){
			player.jumpOn();
		}else{
			player.jumpOff();
		}
		if(controller.isOn(PlayerCommand.DOWN)){
			player.down = true;
		}else{
			player.down = false;
		}
	}

}
