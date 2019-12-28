package net.mgsx.dl10.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
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

	public Texture bgcake;

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
		
		bgcake = new Texture("textures/bg-cake.png");
		
		skin = new Skin(Gdx.files.internal("skins/game-skin.json"));
		
		loadAudio("cake3.mp3", false);
		loadAudio("cakes.mp3", false);
		loadAudio("cake-short.mp3", false);
		loadAudio("house.mp3", false);
		loadAudio("jazz1.mp3", false);
		loadAudio("jazz2.mp3", false);
		loadAudio("jazz3.mp3", false);
		loadAudio("roof.mp3", false);
		loadAudio("menu.mp3", false);
		
		
		loadAudio("bad.mp3", true);
		loadAudio("blurp.wav", true);
		loadAudio("bonus1.wav", true);
		loadAudio("bonus2.wav", true);
		loadAudio("bonus3.wav", true);
		loadAudio("bonus4.wav", true);
		loadAudio("good.mp3", true);
		loadAudio("mob1.wav", true);
		loadAudio("mob2.wav", true);
		loadAudio("paper1.mp3", true);
		loadAudio("paper2.mp3", true);
		loadAudio("santa-bad.wav", true);
		loadAudio("santa-mery.wav", true);
		loadAudio("santa-mery2.wav", true);
		loadAudio("santa-ohohoh.wav", true);
		loadAudio("ui-long.wav", true);
		loadAudio("ui-short.wav", true);
		
		loadAudio("santa-o1.wav", true);
		loadAudio("santa-o2.wav", true);
		loadAudio("santa-o3.wav", true);
		loadAudio("santa-o4.wav", true);
		loadAudio("santa-o5.wav", true);
		loadAudio("santa-m1.wav", true);
		
		loadAudio("dead.wav", true);
		
		loadAudio("knock.wav", true);
		loadAudio("boom.wav", true);
	}
	
	private ObjectMap<String, Sound> sounds = new ObjectMap<String, Sound>();
	private ObjectMap<String, Music> musics = new ObjectMap<String, Music>();
	
	
	private void loadAudio(String fileName, boolean asSFX) {
		FileHandle file = Gdx.files.internal("audio/" + fileName);
		String name = file.nameWithoutExtension();
		if(asSFX){
			sounds.put(name, Gdx.audio.newSound(file));
		}else{
			musics.put(name, Gdx.audio.newMusic(file));
		}
	}

	private void loadMap(String id){
		maps.put(id, new TmxMapLoader().load("maps/" + id + ".tmx"));
	}
	
	private Music currentMusic;
	
	public void playMusic(String name){
		if(currentMusic != null) currentMusic.stop();
		currentMusic = musics.get(name);
		currentMusic.setLooping(true);
		currentMusic.play();
	}
	
	public void stopMusic() {
		if(currentMusic != null) currentMusic.stop();
		currentMusic = null;
	}
	
	public void playSantaJump(){
		// playSFX("santa-o" + (MathUtils.randomBoolean() ? 1 : 3));
		playSFX("santa-o1");
	}
	
	public void playSantaFall(){
		playSFX("knock");
		// playSFX("santa-o" + MathUtils.random(4, 5)); // TODO something different
	}
	public void playSantaPanic(){
		playSFX("santa-m1");
	}
	
	public void playEnd(){
		currentMusic.setVolume(.3f); // XXX for the end
		playSFX("santa-mery2");
	}
	public void playLose(){
		currentMusic.setVolume(.1f); // XXX for the end
		playSFX("bad");
	}
	public void playWin(){
		currentMusic.setVolume(.1f); // XXX for the end
		playSFX("good");
	}
	public void playMobHurt(){
		playSFX("mob2");
	}
	public void playMobDead(){
		playSFX("blurp");
	}
	public void playSmallBonus(){
		playSFX("bonus1");
	}
	public void playBigBonus(){
		playSFX("bonus2");
	}
	public void playLifeBonus(){
		playSFX("bonus3");
	}
	public void playExtraBonus(){
		// double effect
		playSFX("bonus3");
		playSFX("good");
	}
	public void playTransitionIn(){
		playSFX("paper2");
	}
	public void playTransitionOut(){
		// playSFX("paper1");
	}
	public void playSantaEnterIn(){
		playSFX("santa-o3");
	}
	public void playSantaEnterOut(){
		playSFX("santa-ohohoh");
	}
	public void playSantaHurt(){
		playSFX("santa-m1");
	}
	public void playSantaDead(){
		playSFX("dead");
	}
	
	public void playMobFall(){
		playSFX("boom");
	}

	
	public void playUIStart(){
		playSFX("ui-long");
	}
	public void playUIAny(){
		playSFX("ui-short");
	}

	private void playSFX(String name) {
		sounds.get(name).play(.5f); // TODO SFX volume
		
	}

	
}
