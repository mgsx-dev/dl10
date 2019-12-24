package net.mgsx.dl10.engine.model.engines;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.viewport.Viewport;

public interface Transition {
	public void update(PlatformerEngine engine, float delta);
	public void draw(Batch batch, Viewport viewport);
}
