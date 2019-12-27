package net.mgsx.dl10.engine.model.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;

import net.mgsx.dl10.GameSettings;
import net.mgsx.dl10.engine.model.engines.PlatformerEngine;
import net.mgsx.dl10.engine.model.renderer.PlatformerRenderer.CameraAnim;

public class MenuCameraAnim implements CameraAnim
{
	private float time;
	
	@Override
	public void updateCamera(PlatformerEngine engine, PerspectiveCamera camera, Viewport viewport) {
		
		time += Gdx.graphics.getDeltaTime();
		
		camera.fieldOfView = 40;
		float distance = (viewport.getCamera().viewportHeight / 2) / (float)Math.tan(camera.fieldOfView * MathUtils.degreesToRadians / 2);
		camera.position.set(viewport.getCamera().position.x , viewport.getCamera().position.y, distance);
		
		
		float angleTarY = (engine.level.players.first().position.y - engine.level.worldPosition.y) * .5f ;
		float angleTarX = (engine.level.players.first().position.x - engine.level.worldPosition.x) * .5f ;
		
		// MathUtils.sin(time * .2f)
		if(GameSettings.steadyCamDebug){
			camera.direction.set(0, 0, -1);
		}else{
			camera.direction.set(angleTarX * 0.01f, angleTarY  * .03f, -1);
		}
		
		camera.up.set(Vector3.Y);
		
		camera.viewportWidth = viewport.getCamera().viewportWidth;
		camera.viewportHeight = viewport.getCamera().viewportHeight;
	}
}
