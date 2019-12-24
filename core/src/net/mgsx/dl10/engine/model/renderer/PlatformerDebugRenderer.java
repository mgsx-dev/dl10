package net.mgsx.dl10.engine.model.renderer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.viewport.Viewport;

import net.mgsx.dl10.engine.model.EBase;
import net.mgsx.dl10.engine.model.engines.PlatformerEngine;
import net.mgsx.dl10.engine.model.entities.Player;

public class PlatformerDebugRenderer {
	
	private final PlatformerEngine engine;
	
	private ShapeRenderer shapeRenderer;
	
	public PlatformerDebugRenderer(PlatformerEngine engine) {
		super();
		this.engine = engine;
		shapeRenderer = new ShapeRenderer();
	}

	public void render(Viewport viewport) {
		
		shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
		shapeRenderer.begin(ShapeType.Line);
		
		
		for(EBase b : engine.level.blocks){
			if(b.interaction != null){
				shapeRenderer.setColor(Color.RED);
			}else{
				shapeRenderer.setColor(Color.DARK_GRAY);
			}
			renderRect(shapeRenderer, b);
		}
	
		shapeRenderer.setColor(Color.ORANGE);
		for(EBase c : engine.level.chars){
			renderRect(shapeRenderer, c);
		}
		
		shapeRenderer.setColor(Color.WHITE);
		for(Player player : engine.level.players){
			renderRect(shapeRenderer, player);
		}
		
		shapeRenderer.end();
	}


	private void renderRect(ShapeRenderer shapeRenderer, EBase cb) {
		shapeRenderer.rect(cb.position.x, cb.position.y, cb.size.x, cb.size.y);
	}
}
