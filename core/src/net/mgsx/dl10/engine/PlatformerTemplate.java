package net.mgsx.dl10.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import net.mgsx.dl10.DL10Game;
import net.mgsx.dl10.GameSettings;
import net.mgsx.dl10.assets.GameAssets;
import net.mgsx.dl10.engine.model.engines.PlatformerEngine;
import net.mgsx.dl10.engine.model.entities.Player;
import net.mgsx.dl10.engine.model.inputs.PlatformerInputs;
import net.mgsx.dl10.engine.model.renderer.PlatformerDebugRenderer;
import net.mgsx.dl10.engine.model.renderer.PlatformerRenderer;

public class PlatformerTemplate extends ScreenAdapter
{
	public static final float cameraFollowSpeed = 3;
	
	private Viewport viewport;
	private PlatformerDebugRenderer debugRenderer;
	private PlatformerEngine engine;
	private Stage stage;
	private PlatformerInputs inputs;
	private Preferences prefs;
	private final Vector2 worldPositionClamped = new Vector2();
	private PlatformerRenderer renderer;
	
	public PlatformerTemplate() {
		
		prefs = Gdx.app.getPreferences("DL10");
		inputs = new PlatformerInputs(prefs);
		
		engine = new PlatformerEngine(inputs);
		engine.renderer = renderer = new PlatformerRenderer(engine);
		
		
		engine.start();
		
		// Game viewport will be updated later
		viewport = new FitViewport(DL10Game.WIDTH, DL10Game.HEIGHT);
		
		// HUD
		stage = new Stage(new FitViewport(DL10Game.WIDTH, DL10Game.HEIGHT));
		Gdx.input.setInputProcessor(stage);
		
		// rendering
		debugRenderer = new PlatformerDebugRenderer(engine);
		
		// TODO intro screen engine.transitions.add(new DefaultTransition(null));
	}
	
	public boolean isOver(){
		return engine.playerContinues <= 0;
	}
	
	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, false);
		stage.getViewport().update(width, height);
		renderer.resize(width, height);
	}
	
	@Override
	public void render(float delta) {
		
		if(engine.level.hud != null && !stage.getRoot().hasChildren() || engine.level.hud != stage.getRoot().getChild(0)){
			stage.getRoot().clearChildren();
			if(engine.level.hud != null){
				stage.addActor(engine.level.hud);
			}
		}
		
		stage.act();
		
		if(GameSettings.debug && Gdx.input.isKeyJustPressed(Input.Keys.TAB)){
			inputs.openSettings(stage, GameAssets.i.skin);
		}
		
		// input
		Player player = engine.level.players.first();
		
		inputs.update(player);
		
		// update
		engine.update(delta);
		
		// update camera
		engine.level.worldPosition.lerp(player.position, delta * cameraFollowSpeed);
		
		worldPositionClamped.set(engine.level.worldPosition);
		if(engine.level.screenClamp){
			
			// update view (clip to world bounds)
			float halfW = viewport.getWorldWidth()/2;
			float halfH = viewport.getWorldHeight()/2;
			if(worldPositionClamped.x - halfW < engine.level.worldBounds.x){
				worldPositionClamped.x = engine.level.worldBounds.x + halfW;
			}else if(worldPositionClamped.x + halfW > engine.level.worldBounds.x + engine.level.worldBounds.width){
				worldPositionClamped.x = engine.level.worldBounds.x + engine.level.worldBounds.width - halfW;
			}
			if(worldPositionClamped.y - halfH < engine.level.worldBounds.y){
				worldPositionClamped.y = engine.level.worldBounds.y + halfH;
			}else if(worldPositionClamped.y + halfH > engine.level.worldBounds.y + engine.level.worldBounds.height){
				worldPositionClamped.y = engine.level.worldBounds.y + engine.level.worldBounds.height - halfH;
			}
			engine.level.worldPosition.set(worldPositionClamped);
		}
		
		// XXX should be an animated thing... (in transitions / interactions)
		engine.level.screenZoom = MathUtils.lerp(engine.level.screenZoom, 1, delta * 1f);
		
		viewport.setWorldSize(engine.level.screenBounds.width * engine.level.screenZoom, engine.level.screenBounds.height * engine.level.screenZoom);
		viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		viewport.getCamera().position.set(worldPositionClamped, 0).add(0, engine.level.viewOffset, 0);
		viewport.getCamera().update();
		// rendering
		
		renderer.update(delta);
		
		float l = 0f;
		Gdx.gl.glClearColor(l, l, l, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		if(engine.level.bgTexture != null){
			Batch batch = stage.getBatch();
			batch.begin();
			batch.draw(engine.level.bgTexture, 0, 0);
			batch.end();
		}
		
		viewport.apply();
		
		renderer.render(viewport);
		
		if(GameSettings.debug){
			debugRenderer.render(viewport);
		}
		
		
		stage.getViewport().apply();
		stage.draw();
		stage.getBatch().setColor(Color.WHITE);
		
		engine.render(stage.getViewport());
	}

}
