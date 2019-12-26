package net.mgsx.dl10;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.utils.Collections;

import net.mgsx.dl10.assets.GameAssets;
import net.mgsx.dl10.engine.PlatformerTemplate;

public class DL10Game extends Game {
	
	public static final int WIDTH = 1024;
	public static final int HEIGHT = 614;
	
	@Override
	public void create () {
		
		Collections.allocateIterators = true;
		
		GameAssets.i = new GameAssets();
		
		setScreen(new PlatformerTemplate());
	}
	
}
