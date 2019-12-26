package net.mgsx.dl10.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import net.mgsx.dl10.assets.GameAssets;
import net.mgsx.dl10.engine.model.engines.PlatformerEngine;
import net.mgsx.dl10.engine.model.transitions.DefaultTransition;

public class MenuHUD extends Table {
	
	public MenuHUD(final PlatformerEngine engine) {
		super(GameAssets.i.skin);
		setFillParent(true);

		add(new Label("Santa and the giant cake", getSkin(), "title")).row();
		
		TextButton btPlay = new TextButton("Play", getSkin());
		add(btPlay).row();
		
		TextButton btSettings = new TextButton("Controller settings", getSkin());
		add(btSettings).row();
		
		btPlay.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				engine.transitions.add(new DefaultTransition(engine.levels.get("roof1")));
			}
		});
		
		btSettings.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				engine.inputManager.openSettings(getStage(), getSkin());
			}
		});
		
	}

}
