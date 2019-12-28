package net.mgsx.dl10.engine.inputs.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap.Entry;

import net.mgsx.dl10.assets.GameAssets;
import net.mgsx.dl10.engine.inputs.ControllerBase;
import net.mgsx.dl10.engine.inputs.InputManager;
import net.mgsx.dl10.engine.inputs.InputManager.Command;
import net.mgsx.dl10.engine.inputs.TriggerBase;

// TODO abstract for multi-players (P1 to P4), different tabs...
public class InputsUI extends Table
{
	private Table cmdTable;
	private InputManager inputs;
	
	private Command learningCommand;
	private SelectBox<ControllerBase> controllerSelector;
	
	private ButtonGroup<Button> buttons = new ButtonGroup<Button>();
	private Label learnLabel;

	public InputsUI(InputManager inputs, Skin skin) {
		super(skin);
		this.inputs = inputs;
		
		setBackground("default-rect");
		
		buttons.setMinCheckCount(0);
		buttons.setMaxCheckCount(1);
		
		add("Controller settings").row();
		
		controllerSelector = new SelectBox<ControllerBase>(skin);
		controllerSelector.setItems(inputs.controllers);
		controllerSelector.setSelected(inputs.controller);
		
		add(controllerSelector).row();
		
		add(cmdTable = new Table(skin)).row();
		
		
		TextButton btClose = new TextButton("OK", skin);
		add(btClose);
		
		displayController(controllerSelector.getSelected());

		btClose.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				InputsUI.this.inputs.save();
				GameAssets.i.playUIAny();
				remove();
			}
		});
		controllerSelector.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				InputsUI.this.inputs.controller = controllerSelector.getSelected();
				displayController(controllerSelector.getSelected());
			}
		});
		
	}
	
	private void displayController(final ControllerBase ce){
		cmdTable.clearChildren();
		
		buttons.clear();
		
		for(final Command cmd : inputs.commands){
			cmdTable.add(cmd.label);
			
			final Label keysLabel = cmdTable.add(triggersToText(ce, cmd)).getActor();
			
			final TextButton btLearn = new TextButton("change", getSkin(), "toggle");
			cmdTable.add(btLearn).row();
			
			buttons.add(btLearn);
			
			btLearn.addListener(new ChangeListener() {

				@Override
				public void changed(ChangeEvent event, Actor actor) {
					if(btLearn.isChecked()){
						keysLabel.setText("learning...");
						learnLabel = keysLabel;
						learningCommand = cmd;
						ce.clear(cmd);
						ce.learnStart(cmd);
					}else{
						keysLabel.setText(triggersToText(ce, cmd));
						if(learningCommand == cmd){
							learningCommand = null;
							learnLabel = null;
							ce.learnStop();
						}
					}
					GameAssets.i.playUIAny();
				}
			});
		}
	}
	
	protected CharSequence triggersToText(ControllerBase ce, Command cmd) {
		Array<TriggerBase> triggers = new Array<TriggerBase>();
		
		for(Entry<TriggerBase, Command> entry : ce.triggers){
			if(entry.value == cmd){
				entry.key.toString();
				triggers.add(entry.key);
			}
		}
		
		String s = "";
		for(int i=0 ; i<triggers.size ; i++){
			if(i > 0) s += ", ";
			s += triggers.get(i).toString();
		}
		return s;
	}

	@Override
	public void act(float delta) {
		if(learningCommand != null){
			ControllerBase ce = controllerSelector.getSelected();
			TriggerBase trigger = ce.learn();
			if(trigger != null){
				ce.triggers.put(trigger, learningCommand);
				learnLabel.setText(triggersToText(ce, learningCommand));
				GameAssets.i.playUIAny();
			}
		}
		super.act(delta);
	}

}
