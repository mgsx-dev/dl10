package net.mgsx.dl10.engine.inputs;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap.Entry;

import net.mgsx.dl10.engine.inputs.gamepad.GamepadController;
import net.mgsx.dl10.engine.inputs.keyboard.KeyboardController;
import net.mgsx.dl10.engine.inputs.keyboard.KeyboardTrigger;
import net.mgsx.dl10.engine.inputs.store.ControllerData;
import net.mgsx.dl10.engine.inputs.store.InputsData;
import net.mgsx.dl10.engine.inputs.ui.InputsUI;

public class InputManager {

	public static class Command{
		public Object cmd;
		public String label;
		public String id;
		public Command(Object cmd, String id, String label) {
			super();
			this.cmd = cmd;
			this.id = id;
			this.label = label;
		}
		
	}
	
	private InputsUI lastUI;

	private final Preferences prefs;
	
	
	public InputManager(Preferences prefs) {
		this.prefs = prefs;
		controllers.add(new KeyboardController());
		for(Controller controller : Controllers.getControllers()){
			controllers.add(new GamepadController(controller));
		}
		controller = controllers.first();
	}
	
	public void reload() {
		controllers.clear();
		
		controllers.add(new KeyboardController());
		for(Controller controller : Controllers.getControllers()){
			controllers.add(new GamepadController(controller));
		}
		controller = controllers.first();
	}
	
	public void openSettings(Stage stage, Skin skin) {
		if(lastUI == null || !lastUI.hasParent()){
			InputsUI ui = new InputsUI(this, skin);
			ui.setFillParent(true);
			stage.addActor(ui);
			lastUI = ui;
		}
	}
	
	public final Array<ControllerBase> controllers = new Array<ControllerBase>();

	public final Array<Command> commands = new Array<Command>();

	public ControllerBase controller;
	
	public void addCommand(Object cmd, String id, String label) {
		commands.add(new Command(cmd, id, label));
	}
	
	public void addKeys(Object cmd, int...keys) {
		Command cm = null;
		for(Command c : commands){
			if(c.cmd == cmd){
				cm = c;
			}
		}
		ControllerBase keyboard = controllers.first();
		for(int key : keys){
			keyboard.triggers.put(new KeyboardTrigger(key), cm);
		}
		
	}

	public boolean load() 
	{
		String json = prefs.getString("controls", null);
		if(json != null){
			// TODO
			InputsData data = new Json().fromJson(InputsData.class, json);
			for(ControllerData dc : data.controllers){
				for(ControllerBase c : controllers){
					if(c.toString().equals(dc.name)){ // TODO could have more than one...
						c.triggers.clear(); // clear default
						for(Entry<String, String> e : dc.triggers){
							TriggerBase trigger = c.parseTrigger(e.key);
							if(trigger != null){
								for(Command cmd : commands){
									if(cmd.id.equals(e.value)){
										c.triggers.put(trigger, cmd);
									}
								}
							}
						}
					}
				}
			}
			
			int firstID = data.activations.first();
			if(firstID < controllers.size){
				controller = controllers.get(firstID); // TODO not really true : find index from data
			}else{
				controller = controllers.first();
			}
			
			return true;
		}
		return false;
	}

	public void save() 
	{
		InputsData data = new InputsData();
		for(ControllerBase c : controllers){
			ControllerData dc = new ControllerData();
			data.controllers.add(dc);
			dc.name = c.toString();
			for(Entry<TriggerBase, Command> e : c.triggers){
				dc.triggers.put(e.key.format(), e.value.id);
			}
		}
		data.activations.add(controllers.indexOf(controller, true));
		
		prefs.putString("controls", new Json().toJson(data));
		prefs.flush();
	}


}
