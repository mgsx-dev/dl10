package net.mgsx.dl10.engine.model.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.model.Animation;
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.viewport.Viewport;

import net.mgsx.dl10.assets.GameAssets;
import net.mgsx.dl10.engine.model.EBase;
import net.mgsx.dl10.engine.model.components.CModel;
import net.mgsx.dl10.engine.model.engines.PlatformerEngine;
import net.mgsx.dl10.engine.model.engines.PlatformerLevel;
import net.mgsx.dl10.engine.shaders.OutlineShader;
import net.mgsx.dl10.engine.shaders.OutlineShaderProvider;
import net.mgsx.gltf.scene3d.animation.AnimationControllerHack;
import net.mgsx.gltf.scene3d.lights.DirectionalLightEx;
import net.mgsx.gltf.scene3d.model.ModelInstanceHack;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneManager;
import net.mgsx.gltf.scene3d.scene.SceneRenderableSorter;
import net.mgsx.gltf.scene3d.shaders.PBRShaderConfig;
import net.mgsx.gltf.scene3d.shaders.PBRShaderConfig.SRGB;
import net.mgsx.gltf.scene3d.shaders.PBRShaderProvider;

public class PlatformerRenderer {
	
	private PlatformerEngine engine;
	
	private SceneManager sceneManager;
	
	private PerspectiveCamera camera;

	private ModelBatch outlineExtBatch;

	private DirectionalLightEx dl;

	private float time;

	public PlatformerRenderer(PlatformerEngine engine) {
		this.engine = engine;
		// TODO 32 bones max
		int maxBones = 32;
		sceneManager = new SceneManager(createColorShader(maxBones), PBRShaderProvider.createDepthShaderProvider(maxBones), new SceneRenderableSorter());
		// TODO set toon shader
		
		OutlineShaderProvider outlineShaderProvider = new OutlineShaderProvider(maxBones);
		outlineExtBatch = new ModelBatch(outlineShaderProvider, new SceneRenderableSorter());
		
		// Scene globalScene = new Scene(GameAssets.i.sceneAsset.scene);
		
		
		// create objects and cache references
		for(Entry<String, PlatformerLevel> entry : engine.levels){
			
			final PlatformerLevel level = entry.value;
			
			final Scene levelScene = new Scene(new ModelInstanceHack(GameAssets.i.sceneAsset.scene.model, level.prefix), true);
			
			/*
			for(int i=0 ; i<levelScene.modelInstance.nodes.size ; ){
				Node node = levelScene.modelInstance.nodes.get(i);
				if(!node.id.startsWith(level.prefix)){
					levelScene.modelInstance.nodes.removeIndex(i);
				}else{
					i++;
				}
			}
			*/
			
			level.scenes.add(levelScene);
			
			CModel model = new CModel();
			model.scene = new Scene(new ModelInstanceHack(GameAssets.i.sceneAsset.scene.model, "santa"), true);
			model.node = model.scene.modelInstance.getNode("santa");
			// scene.modelInstance.nodes.add(model.node);
			model.anim = new AnimationControllerHack(model.scene.modelInstance);
			model.anim.calculateTransforms = false;
			level.scenes.add(model.scene);
			
			
			for(EBase e : level.players){
				e.model = model;
			}
			for(final EBase e : level.blocks){
				if(e.bonus != null){
					e.model = new CModel();
					e.model.node = GameAssets.i.gifts.get(e.bonus.varIndex).copy();
					e.model.node.translation.set(e.position.x + e.size.x/2, e.position.y + e.size.y/2, 0);
					levelScene.modelInstance.nodes.add(e.model.node);
					
					e.life.onDead = new Runnable() {
						@Override
						public void run() {
							// TODO add bonus, trig particles, and sounds...etc
							levelScene.modelInstance.nodes.removeValue(e.model.node, true);
						}
					};
				}
			}
			for(final EBase e : level.chars){
				e.model = new CModel();
				
				String modelName = e.type;
				
				ModelInstanceHack mi = new ModelInstanceHack(levelScene.modelInstance.model, modelName);
				
				e.model.scene = new Scene(mi, true);
				
				e.model.anim = (AnimationControllerHack)e.model.scene.animationController;
				e.model.node = mi.nodes.first();
				level.scenes.add(e.model.scene);
				
				animate(e.model, modelName + "Walk", 0);
				
				e.life.onDead = new Runnable() {
					@Override
					public void run() {
						// TODO add bonus, trig particles, and sounds...etc
						// levelScene.modelInstance.nodes.removeValue(e.model.node, true);
						level.scenes.removeValue(e.model.scene, true);
					}
				};
			}
		}
		
		
		camera = new PerspectiveCamera(60f, 1, 1);
		
		sceneManager.camera = camera;
		
		
		dl = new DirectionalLightEx();
		dl.set(Color.WHITE, new Vector3(0, -1, 0).nor());
		dl.intensity = 3.0f;
		sceneManager.environment.add(dl);
	}
	
	private static ShaderProvider createColorShader(int maxBones){
		PBRShaderConfig config = PBRShaderProvider.defaultConfig();
		config.vertexShader = Gdx.files.classpath("net/mgsx/dl10/engine/shaders/gltf-ceil-shading.vs.glsl").readString();
		config.fragmentShader = Gdx.files.classpath("net/mgsx/dl10/engine/shaders/gltf-ceil-shading.fs.glsl").readString();
		config.manualSRGB = SRGB.NONE;
		config.numBones = maxBones;
		config.numDirectionalLights = 1;
		config.numPointLights = 0;
		config.numSpotLights = 0;
		config.numVertexColors = 2;
		return PBRShaderProvider.createDefault(config);
	}
	
	public void resize(int width, int height) {
		sceneManager.updateViewport(width, height);
	}
	
	public void update(float delta){
		
		time += delta;
		
		// TODO only add visible nodes
		sceneManager.getScenes().clear();
		
		sceneManager.getScenes().addAll(engine.level.scenes);
		
		for(EBase e : engine.level.players){
			
			if(Math.abs(e.dyn.velocity.x) > .1f){
				animate(e.model, "run", .1f);
			}else{
				animate(e.model, null, 1f);
			}
			
			e.model.node.translation.set(e.position.x + e.size.x/2, e.position.y, 0);
			
			float fakeDirectionAngle = 80;
			
			e.model.node.rotation.set(Vector3.Y, e.dyn.velocity.x > 0 ? fakeDirectionAngle : -fakeDirectionAngle);
			/*
			e.model.scene.modelInstance.transform
			.idt()
			.translate(e.position.x + e.size.x/2, e.position.y, 0)
			.rotate(Vector3.Y, e.dyn.velocity.x > 0 ? 90 : -90)
			;
			*/
			
			if(e.model.anim.current != null){
				e.model.anim.current.speed = Math.abs(e.dyn.velocity.x / 5);
			}
			
			e.model.anim.update(delta);
		}
		
		for(EBase e : engine.level.blocks){
			if(e.bonus != null){
				float base = e.size.x;
				float t = MathUtils.sinDeg(time * 360 * 2) * .5f + .5f;
				float s = MathUtils.lerp(.9f, 1.1f, t) * base;
				e.model.node.scale.set(s, s, s);
				float t2 = MathUtils.sinDeg(time * 90) * .5f + .5f;
				float pitch = MathUtils.lerp(0, 45, t2);
				e.model.node.rotation.set(Vector3.X, pitch).mul(new Quaternion(Vector3.Y, time * 90));
			}
		}
		
		for(EBase e : engine.level.chars){
			e.model.node.translation.set(e.position.x + e.size.x/2, e.position.y, 0);
			// e.model.anim.update(delta);
			
			float fakeDirectionAngle = 45;
			
			e.model.node.rotation.set(Vector3.Y, e.path.isReverse() ? -fakeDirectionAngle : fakeDirectionAngle);
		}
		
		sceneManager.update(delta);
		
		for(Scene scene : sceneManager.getScenes()){
			scene.modelInstance.calculateTransforms(); // XXX because no master anims
		}
	}
	
	private boolean animate(CModel model, String id, float transition){
		// TODO store last anim on CModel (object for fast comparison)
		if(model.anim.current == null && id == null) return false;
		if(model.anim.current != null && model.anim.current.animation.id.equals(id)) return false;
		if(model.scene != null){
			Animation animation = model.scene.modelInstance.getAnimation(id);
			if(animation == null) return false;
		}
		model.anim.animate(id, -1, null, transition); // TODO setAnimation instead ? available on controller hack
		return true;
	}

	public void render(Viewport viewport) {
		
		sceneManager.camera.far = 200;
		sceneManager.camera.update();
		
		// dependency angle / distance
		camera.fieldOfView = 40;
		float distance = (viewport.getCamera().viewportHeight / 2) / (float)Math.tan(camera.fieldOfView * MathUtils.degreesToRadians / 2);
		camera.position.set(viewport.getCamera().position.x , viewport.getCamera().position.y, distance);
		camera.direction.set(0, 0, -1); //.rotate(Vector3.Y, delta * 90);
		camera.up.set(Vector3.Y);
		
		camera.viewportWidth = viewport.getCamera().viewportWidth;
		camera.viewportHeight = viewport.getCamera().viewportHeight;
		camera.update();
		
		dl.intensity = 1f;
		
		sceneManager.render();
		
		if(true){
			
			OutlineShader.extrusionRate = .05f * .1f;
			
			OutlineShader.extrusionColor.set(Color.BLACK);
			
			ModelBatch oldBatch = sceneManager.getBatch();
			sceneManager.setBatch(outlineExtBatch);

			sceneManager.renderColors();
			
			sceneManager.setBatch(oldBatch);
		}
	}

}
