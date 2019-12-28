package net.mgsx.dl10.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;

import net.mgsx.dl10.assets.GameAssets;
import net.mgsx.dl10.engine.model.engines.PlatformerEngine;
import net.mgsx.dl10.engine.model.transitions.DefaultTransition;

public class MenuHUD extends Table {
	
	public MenuHUD(final PlatformerEngine engine) {
		super(GameAssets.i.skin);
		setFillParent(true);

		add(new Label("SANTA", getSkin(), "title")).padTop(10).getActor().setAlignment(Align.center);;
		row();
		Label a = add(new Label("and\nthe giant cake", getSkin(), "title")).padTop(10).getActor();
		a.setAlignment(Align.center);
		a.setFontScale(.5f);
		row();
		
		add().padTop(10).row();
		
		TextButton btPlay = new TextButton("New Game", getSkin());
		add(btPlay).padBottom(10).row();
		
		TextButton btSettings = new TextButton("Controller settings", getSkin());
		add(btSettings).row();
		
		add().expand(); // force top
		
		btPlay.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				engine.transitions.add(new DefaultTransition(engine.levels.get("roof1")));
				GameAssets.i.playUIStart();
			}
		});
		
		btSettings.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				engine.inputManager.openSettings(getStage(), getSkin());
				GameAssets.i.playUIAny();
			}
		});
		
	}

}
