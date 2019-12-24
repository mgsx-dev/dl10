package net.mgsx.dl10.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.utils.Array;

import net.mgsx.gltf.loaders.gltf.GLTFLoader;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

public class GameAssets {
	public static GameAssets i;
	
	public SceneAsset sceneAsset;
	
	public final Array<Node> gifts = new Array<Node>();

	public GameAssets() {
		
		sceneAsset = new GLTFLoader().load(Gdx.files.internal("models/main.gltf"), true);
		
		for(int i=1 ; i<=6 ; i++){
			Node node = sceneAsset.scene.model.getNode("gift" + i);
			gifts.add(node);
			// no longer necessary
			// sceneAsset.scene.model.nodes.removeValue(node, true);
		}
		
	}
	
}
