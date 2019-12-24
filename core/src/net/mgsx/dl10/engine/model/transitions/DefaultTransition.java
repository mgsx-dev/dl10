package net.mgsx.dl10.engine.model.transitions;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.Viewport;

import net.mgsx.dl10.engine.model.engines.PlatformerEngine;
import net.mgsx.dl10.engine.model.engines.PlatformerLevel;
import net.mgsx.dl10.engine.model.engines.Transition;

public class DefaultTransition implements Transition {

	private PlatformerLevel nextLevel;
	private float time = 0;
	private boolean swap = false;
	
	public static Texture texture;
	public boolean reverse = false;
	public float delayRate = 1;
	public float speed = 1;
	public Interpolation interpolation = Interpolation.bounceOut;
	
	public DefaultTransition(PlatformerLevel nextLevel) {
		super();
		this.nextLevel = nextLevel;
		if(texture == null) texture = new Texture("textures/bg.png"); // XXX asset
	}

	@Override
	public void update(PlatformerEngine engine, float delta) {
		time += delta * speed;
		reverse = time > delayRate + 1; // XXX
		if(time > delayRate + 2){
			engine.transitions.removeIndex(0);
		}else if(!swap && time > 1){
			swap = true;
			if(nextLevel != null) engine.level = nextLevel;
		}
	}

	@Override
	public void draw(Batch batch, Viewport viewport) {
		if(true) return; // XXX
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


}
