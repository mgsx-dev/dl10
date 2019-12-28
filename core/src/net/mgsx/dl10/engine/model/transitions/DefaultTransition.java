package net.mgsx.dl10.engine.model.transitions;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.Viewport;

import net.mgsx.dl10.GameSettings;
import net.mgsx.dl10.assets.GameAssets;
import net.mgsx.dl10.engine.model.engines.PlatformerEngine;
import net.mgsx.dl10.engine.model.engines.PlatformerLevel;
import net.mgsx.dl10.engine.model.engines.Transition;

public class DefaultTransition implements Transition {

	private PlatformerLevel nextLevel;
	private float time = 0;
	private boolean swap = false;
	
	public boolean reverse = false;
	public float delayRate = 1;
	public float speed = 1;
	public Interpolation interpolation = Interpolation.bounceOut;
	private Texture texture;
	private boolean first = true;
	
	public DefaultTransition(PlatformerLevel nextLevel) {
		super();
		this.nextLevel = nextLevel;
		texture = GameAssets.i.transitionTexture;
	}

	@Override
	public void update(PlatformerEngine engine, float delta) {
		if(first){
			first = false;
			GameAssets.i.stopMusic();
			GameAssets.i.playTransitionIn();
		}
		time += delta * speed;
		reverse = time > delayRate + 1; // XXX
		if(time > delayRate + 2){
			if(nextLevel != null) GameAssets.i.playMusic(nextLevel.music);
			engine.transitions.removeIndex(0);
		}else if(!swap && time > 1){
			swap = true;
			swap(engine);
			GameAssets.i.playTransitionOut();
		}
	}

	@Override
	public void draw(Batch batch, Viewport viewport) {
		if(!GameSettings.transitionsEnabled) return;
		if(reverse){
			float t = MathUtils.clamp(time - delayRate - 1, 0, 1);
			// out
			batch.draw(texture, 0, interpolation.apply(t) * viewport.getWorldHeight(), viewport.getWorldWidth(), viewport.getWorldWidth() / (float)texture.getWidth() * (float)texture.getHeight());
		}else{
			float t = MathUtils.clamp(time, 0, 1);
			// in
			batch.draw(texture, 0, (1 - interpolation.apply(t)) * viewport.getWorldHeight(), viewport.getWorldWidth(), viewport.getWorldWidth() / (float)texture.getWidth() * (float)texture.getHeight());
		}
	}
	
	protected void swap(PlatformerEngine engine){
		if(nextLevel != null){
			engine.level = nextLevel;
		}
	}


}
