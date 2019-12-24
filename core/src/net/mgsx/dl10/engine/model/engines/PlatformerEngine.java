package net.mgsx.dl10.engine.model.engines;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.viewport.Viewport;

public class PlatformerEngine {

	public PlatformerLevel level;
	
	public final ObjectMap<String, PlatformerLevel> levels = new ObjectMap<String, PlatformerLevel>();

	public final Array<Transition> transitions = new Array<Transition>();

	private Batch batch;
	
	public PlatformerEngine() {
		batch = new SpriteBatch();
	}
	
	public void update(float delta) {
		if(transitions.size > 0) transitions.peek().update(this, delta);
		level.update(delta);
	}

	public void render(Viewport viewport) {
		if(transitions.size > 0){
			batch.setProjectionMatrix(viewport.getCamera().combined);
			batch.begin();
			transitions.peek().draw(batch, viewport);
			batch.end();
		}
	}
	
}
