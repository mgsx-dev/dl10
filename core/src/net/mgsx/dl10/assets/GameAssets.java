package net.mgsx.dl10.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import net.mgsx.gltf.loaders.gltf.GLTFLoader;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

public class GameAssets {
	public static GameAssets i;
	
	public SceneAsset sceneAsset;
	
	public final Array<Node> gifts = new Array<Node>();
	
	public final ObjectMap<String, TiledMap> maps = new ObjectMap<String, TiledMap>();

	public Texture bgRoof;

	public Skin skin;

	public Texture transitionTexture;

	public GameAssets() {
		
		sceneAsset = new GLTFLoader().load(Gdx.files.internal("models/main.gltf"), true);
		
		for(int i=1 ; i<=6 ; i++){
			Node node = sceneAsset.scene.model.getNode("gift" + i);
			gifts.add(node);
			// no longer necessary
			// sceneAsset.scene.model.nodes.removeValue(node, true);
		}
		
		loadMap("menu");
		loadMap("roof1");
		loadMap("roof2");
		loadMap("house");
		loadMap("cake1");
		loadMap("cake2");
		loadMap("cake3");
		
		bgRoof = new Texture("textures/bg-roof.png");
		
		transitionTexture = new Texture("textures/bg.png");
		
		skin = new Skin(Gdx.files.internal("skins/game-skin.json"));
	}
	
	private void loadMap(String id){
		maps.put(id, new TmxMapLoader().load("maps/" + id + ".tmx"));
	}
	
}
