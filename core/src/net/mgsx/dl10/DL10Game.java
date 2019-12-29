package net.mgsx.dl10;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.utils.Collections;

import net.mgsx.dl10.assets.GameAssets;
import net.mgsx.dl10.engine.PlatformerTemplate;
import net.mgsx.dl10.utils.Stats;

public class DL10Game extends Game {
	
	public static final int WIDTH = 1024;
	public static final int HEIGHT = 614;
	
	public Stats stats;
	
	@Override
	public void create () {
		Collections.allocateIterators = true;
		
		if(GameSettings.stats){ 
			stats = new Stats();
			stats.enable(true);
		}
		
		GameAssets.i = new GameAssets();
		
		setScreen(new PlatformerTemplate());
	}
	
	@Override
	public void render() {
		if(stats != null) stats.update();
		super.render();
	}
	
}
