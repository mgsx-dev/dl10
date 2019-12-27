package net.mgsx.dl10.engine.model.factories;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import net.mgsx.dl10.GameSettings;
import net.mgsx.dl10.assets.GameAssets;
import net.mgsx.dl10.engine.model.EBase;
import net.mgsx.dl10.engine.model.components.CBlock;
import net.mgsx.dl10.engine.model.components.CBonus;
import net.mgsx.dl10.engine.model.components.CDynamic;
import net.mgsx.dl10.engine.model.components.CLife;
import net.mgsx.dl10.engine.model.components.CPath;
import net.mgsx.dl10.engine.model.components.interact.TubeInteraction;
import net.mgsx.dl10.engine.model.components.logics.MobLogicBase;
import net.mgsx.dl10.engine.model.components.logics.MobLogicCrusher;
import net.mgsx.dl10.engine.model.components.logics.MobLogicJumping;
import net.mgsx.dl10.engine.model.components.logics.MobLogicRolling;
import net.mgsx.dl10.engine.model.engines.PlatformerEngine;
import net.mgsx.dl10.engine.model.engines.PlatformerLevel;
import net.mgsx.dl10.engine.model.entities.Player;

public class PlatformerTilemapFactory {
	
	private static final String TAG = "PlatformerTilemapFactory";
	
	private static interface TileFactory{
		public void create(PlatformerLevel engine, float x, float y, float w, float h, TiledMapTile tile, MapObject object);
	}
	
	private TiledMap map;
	
	private final ObjectMap<String, TileFactory> tileFactories = new ObjectMap<String, TileFactory>();
	

	private PlatformerEngine engine;
	
	
	public PlatformerTilemapFactory(PlatformerEngine engine) {
		super();
		this.engine = engine;
		tileFactories.put("screen", new TileFactory() {
			
			@Override
			public void create(PlatformerLevel engine, float x, float y, float w, float h, TiledMapTile tile,
					MapObject object) {
				engine.screenBounds.set(0, 0, w, h);
			}
		});
		tileFactories.put("floor-half", new TileFactory() {
			@Override
			public void create(PlatformerLevel engine, float x, float y, float w, float h, TiledMapTile tile, MapObject object) {
				EBase b = new EBase();
				b.name = (object != null ? object.getName() : null);
				b.block = new CBlock();
				b.block.normals = CBlock.TOP;
				b.position.set(x, y);
				b.size.set(w, h);
				
				if(object != null){
					Float moveup = object.getProperties().get("moveup", null, Float.class);
					if(moveup != null){
						b.dyn = new CDynamic();
						b.path = new CPath();
						b.path.points.add(b.position.cpy());
						b.path.points.add(b.position.cpy().add(0, moveup));
						b.path.speed = 3;
						b.path.delay = 1;
						
						// XXX prevent bad collisions
						b.block.normals = CBlock.ALL;
					}
				}
				
				engine.blocks.add(b);
			}
		});
		tileFactories.put("floor", new TileFactory() {
			@Override
			public void create(PlatformerLevel engine, float x, float y, float w, float h, TiledMapTile tile, MapObject object) {
				EBase b = new EBase();
				b.name = (object != null ? object.getName() : null);
				b.block = new CBlock();
				b.block.normals = CBlock.TOP | CBlock.BOTTOM;
				b.position.set(x, y);
				b.size.set(w, h);
				engine.blocks.add(b);
			}
		});
		tileFactories.put("block", new TileFactory() {
			@Override
			public void create(PlatformerLevel engine, float x, float y, float w, float h, TiledMapTile tile, MapObject object) {
				EBase b = new EBase();
				b.name = (object != null ? object.getName() : null);
				b.block = new CBlock();
				b.block.normals = CBlock.ALL;
				b.position.set(x, y);
				b.size.set(w, h);
				engine.blocks.add(b);
			}
		});
		tileFactories.put("brick", new TileFactory() {
			@Override
			public void create(PlatformerLevel engine, float x, float y, float w, float h, TiledMapTile tile, MapObject object) {
				EBase b = new EBase();
				b.name = (object != null ? object.getName() : null);
				b.block = new CBlock();
				b.block.normals = CBlock.ALL;
				b.position.set(x, y);
				b.size.set(w, h);
				
				b.life = new CLife(1);
				
				engine.blocks.add(b);
			}
		});
		tileFactories.put("tube", new TileFactory() {
			@Override
			public void create(PlatformerLevel engine, float x, float y, float w, float h, TiledMapTile tile, MapObject object) {
				
				if(object.getProperties().get("debug", null, String.class) != null && !GameSettings.debug){
					return;
				}
				
				EBase b = new EBase();
				b.name = (object != null ? object.getName() : null);
				b.block = new CBlock();
				b.block.normals = CBlock.ALL;
				b.position.set(x, y);
				b.size.set(w, h);
				
				if(object instanceof TiledMapTileMapObject){
					TiledMapTileMapObject rmo = (TiledMapTileMapObject)object;
					int angle = MathUtils.round(rmo.getRotation() / 90);
					angle = (angle%4 + 4)%4;
					if(angle == 0){
						b.direction = CBlock.TOP;
					}else if(angle == 2){
						b.direction = CBlock.BOTTOM;
					}else if(angle == 1){
						b.direction = CBlock.RIGHT;
					}else if(angle == 3){
						b.direction = CBlock.LEFT;
					}
				}
				
				if(object != null){
					String wrap = object.getProperties().get("goto", null, String.class);
					if(wrap != null){
						TubeInteraction ti = new TubeInteraction();
						String[] mapTube = wrap.split(":");
						ti.targetMap = mapTube.length > 1 ? mapTube[0] : null;
						ti.targetTube = mapTube.length > 1 ? mapTube[1] : mapTube[0];
						b.interaction = ti; 
					}
				}
				
				engine.blocks.add(b);
			}
		});
		tileFactories.put("entry", new TileFactory() {
			@Override
			public void create(PlatformerLevel engine, float x, float y, float w, float h, TiledMapTile tile, MapObject object) {
				engine.entry.set(x, y);
			}
		});
		tileFactories.put("gift", new TileFactory() {
			@Override
			public void create(PlatformerLevel engine, float x, float y, float w, float h, TiledMapTile tile, MapObject object) {
				final EBase b = new EBase();
				b.name = (object != null ? object.getName() : null);
				b.block = new CBlock();
				b.block.normals = CBlock.ALL;
				b.block.sensor = true;
				b.position.set(x, y);
				b.size.set(w, h);
				
				
				b.bonus = new CBonus();
				b.bonus.varIndex = Integer.parseInt(tile.getProperties().get("var", null, String.class)) - 1;
				
				b.bonus.superBonus = false;
				
				b.bonus.fake = object.getProperties().get("fake", null, String.class) != null;
				
				if(!b.bonus.fake) b.life = new CLife(1);
				
				engine.blocks.add(b);
			}
		});
		tileFactories.put("life", new TileFactory() {
			@Override
			public void create(PlatformerLevel engine, float x, float y, float w, float h, TiledMapTile tile, MapObject object) {
				final EBase b = new EBase();
				b.name = (object != null ? object.getName() : null);
				b.block = new CBlock();
				b.block.normals = CBlock.ALL;
				b.block.sensor = true;
				b.position.set(x, y);
				b.size.set(w, h);
				
				
				b.bonus = new CBonus();
				
				b.bonus.superBonus = false;
				
				b.bonus.life = true;
				
				b.bonus.fake = object.getProperties().get("fake", null, String.class) != null;
				
				if(!b.bonus.fake) b.life = new CLife(1);
				
				engine.blocks.add(b);
			}
		});
		tileFactories.put("super-gift", new TileFactory() {
			@Override
			public void create(PlatformerLevel engine, float x, float y, float w, float h, TiledMapTile tile, MapObject object) {
				final EBase b = new EBase();
				b.name = (object != null ? object.getName() : null);
				b.block = new CBlock();
				b.block.normals = CBlock.ALL;
				b.block.sensor = true;
				b.position.set(x, y);
				b.size.set(w, h);
				
				b.life = new CLife(1);
				
				b.bonus = new CBonus();
				b.bonus.varIndex = Integer.parseInt(tile.getProperties().get("var", null, String.class)) - 1;
				
				b.bonus.superBonus = true;
				
				engine.blocks.add(b);
			}
		});
		tileFactories.put("exit", new TileFactory() {
			@Override
			public void create(PlatformerLevel engine, float x, float y, float w, float h, TiledMapTile tile, MapObject object) {
				final EBase b = new EBase();
				b.name = (object != null ? object.getName() : null);
				b.type = "end-game";
				b.block = new CBlock();
				b.block.normals = CBlock.ALL;
				b.block.sensor = true;
				b.position.set(x, y);
				b.size.set(w, h);
				
				engine.blocks.add(b);
			}
		});
		tileFactories.put("mob1", new TileFactory() {
			@Override
			public void create(PlatformerLevel engine, float x, float y, float w, float h, TiledMapTile tile, MapObject object) {
				final EBase b = new EBase();
				b.name = (object != null ? object.getName() : null);
				b.block = new CBlock();
				b.block.normals = CBlock.ALL;
				b.position.set(x, y);
				b.size.set(w, h);
				
				b.life = new CLife(1);
				b.logic = new MobLogicBase();
				
				b.type = "SnowmanSmall";
				
				String pathID = object.getProperties().get("path", null, String.class);
				if(pathID != null){
					Array<Vector2> path = paths.get(pathID);
					b.path = new CPath();
					b.path.points.addAll(path);
					b.path.setOffset(b.position);
					b.path.speed = 2;
				}
				
				engine.chars.add(b);
			}
		});
		tileFactories.put("mob2", new TileFactory() {
			@Override
			public void create(PlatformerLevel engine, float x, float y, float w, float h, TiledMapTile tile, MapObject object) {
				final EBase b = new EBase();
				b.name = (object != null ? object.getName() : null);
				b.block = new CBlock();
				b.block.normals = CBlock.ALL;
				b.position.set(x, y);
				b.size.set(w, h);
				
				b.type = "SnowmanBig";
						
				b.life = new CLife(4);
				
				b.logic = new MobLogicBase();
				
				String pathID = object.getProperties().get("path", null, String.class);
				if(pathID != null){
					Array<Vector2> path = paths.get(pathID);
					b.path = new CPath();
					b.path.points.addAll(path);
					b.path.setOffset(b.position);
					b.path.speed = 1;
				}
				
				engine.chars.add(b);
			}
		});
		tileFactories.put("mob3", new TileFactory() {
			@Override
			public void create(PlatformerLevel engine, float x, float y, float w, float h, TiledMapTile tile, MapObject object) {
				final EBase b = new EBase();
				b.name = (object != null ? object.getName() : null);
				b.block = new CBlock();
				b.block.normals = CBlock.ALL;
				b.position.set(x, y);
				b.size.set(w, h);
				
				b.type = "mob3";
						
				b.logic = new MobLogicCrusher(b);
				
				engine.chars.add(b);
			}
		});
		tileFactories.put("mob4", new TileFactory() {
			@Override
			public void create(PlatformerLevel engine, float x, float y, float w, float h, TiledMapTile tile, MapObject object) {
				final EBase b = new EBase();
				b.name = (object != null ? object.getName() : null);
				b.block = new CBlock();
				b.block.normals = CBlock.ALL;
				b.position.set(x, y);
				b.size.set(w, h);
				
				b.type = "mob4";
						
				b.life = new CLife(2);
				
				b.logic = new MobLogicBase();
				
				b.align = Align.center;
				
				String pathID = object.getProperties().get("path", null, String.class);
				if(pathID != null){
					Array<Vector2> path = paths.get(pathID);
					CPath cpath = new CPath();
					cpath.points.addAll(path);
					cpath.setOffset(b.position);
					cpath.speed = 1;
					b.logic = new MobLogicRolling(cpath);
				}
				
				engine.chars.add(b);
			}
		});
		tileFactories.put("mob5", new TileFactory() {
			@Override
			public void create(PlatformerLevel engine, float x, float y, float w, float h, TiledMapTile tile, MapObject object) {
				final EBase b = new EBase();
				b.name = (object != null ? object.getName() : null);
				b.block = new CBlock();
				b.block.normals = CBlock.ALL;
				b.position.set(x, y);
				b.size.set(w, h);
				
				b.type = "mob5";
						
				b.life = new CLife(1);
				
				
				String pathID = object.getProperties().get("path", null, String.class);
				if(pathID != null){
					Array<Vector2> path = paths.get(pathID);
					CPath cpath = new CPath();
					cpath.points.addAll(path);
					cpath.setOffset(b.position);
					cpath.speed = 1;
					b.logic = new MobLogicJumping(cpath);
				}
				
				engine.chars.add(b);
			}
		});
	}

	private final ObjectMap<String, Array<Vector2>> paths = new ObjectMap<String, Array<Vector2>>();
	
	public PlatformerLevel createScene(String id) {
		
		PlatformerLevel level = new PlatformerLevel(this.engine);
		level.prefix = id;
		
		this.map = GameAssets.i.maps.get(id);
		
		int mapW = map.getProperties().get("width", Integer.class);
		int mapH = map.getProperties().get("height", Integer.class);
		
		float tileW = map.getProperties().get("tilewidth", Integer.class);
		float tileH = map.getProperties().get("tileheight", Integer.class);
		
		level.worldBounds.set(0, 0, mapW, mapH);
		
		// first pass, get paths
		for(MapLayer layer : map.getLayers()){
			for(MapObject mo : layer.getObjects()){
				if(mo instanceof PolylineMapObject){
					PolylineMapObject pmo = (PolylineMapObject)mo;
					float[] points = pmo.getPolyline().getTransformedVertices();
					Array<Vector2> path = new Array<Vector2>();
					for(int i=0 ; i<points.length ; i+=2){
						path.add(new Vector2(points[i], points[i+1]).scl(1f / tileW, 1f / tileH));
					}
					paths.put(mo.getName(), path);
				}
			}
		}
		
		for(MapLayer layer : map.getLayers()){
			if(layer instanceof TiledMapTileLayer){
				TiledMapTileLayer l = (TiledMapTileLayer)layer;
				for(int y=0 ; y<l.getHeight() ; y++){
					for(int x=0 ; x<l.getWidth() ; x++){
						Cell cell = l.getCell(x, y);
						if(cell != null){
							TiledMapTile tile = cell.getTile();
							String type = tile.getProperties().get("type", null, String.class);
							if(type != null){
								TileFactory tileFactory = tileFactories.get(type);
								if(tileFactory != null){
									tileFactory.create(level, x, y, 1, 1, tile, null);
								}else{
									Gdx.app.log(TAG, "not handled tile type: " + type);
								}
							}else{
								Gdx.app.log(TAG, "not handled tile with no type id=" + tile.getId());
							}
						}
					}
				}
			}else{
				for(MapObject mo : layer.getObjects()){
					if(mo instanceof TiledMapTileMapObject){
						TiledMapTileMapObject tmo = (TiledMapTileMapObject)mo;
						String type = tmo.getProperties().get("type", null, String.class);
						TiledMapTile tile = tmo.getTile();
						if(type == null) type = tile.getProperties().get("type", null, String.class);
						if(type != null){
							TileFactory tileFactory = tileFactories.get(type);
							if(tileFactory != null){
								// rotations
								Vector2 v = new Vector2(tmo.getX(), tmo.getY());
								// v.sub(tmo.getOriginX(), tmo.getOriginY()).rotateRad(tmo.getRotation()).add(tmo.getOriginX(), tmo.getOriginY());
								int angle = MathUtils.round(tmo.getRotation() / 90);
								angle = (angle%4+4)%4;
								float w = tmo.getScaleX();
								float h = tmo.getScaleY();
								if(angle == 0){
									v.set(tmo.getX(), tmo.getY());
								}else if(angle == 2){
									v.set(tmo.getX() - tmo.getScaleX() * tileW, tmo.getY() - tmo.getScaleY() * tileH);
								}else if(angle == 1){
									// to right
									w = tmo.getScaleY();
									h = tmo.getScaleX();
									v.set(tmo.getX(), tmo.getY() - w * tileW);
								}else if(angle == 3){
									w = tmo.getScaleY();
									h = tmo.getScaleX();
									v.set(tmo.getX() - w * tileW, tmo.getY());
								}
								
								tileFactory.create(level, v.x / tileW, v.y / tileH, w, h, tile, mo);
							}else{
								Gdx.app.log(TAG, "not handled tile type: " + type);
							}
						}else{
							Gdx.app.log(TAG, "not handled tile with no type id=" + tile.getId());
						}
					}else if(mo instanceof RectangleMapObject){
						RectangleMapObject rmo = (RectangleMapObject)mo;
						String name = mo.getName();
						if(!name.isEmpty()){
							TileFactory tileFactory = tileFactories.get(name);
							if(tileFactory != null){
								tileFactory.create(level, rmo.getRectangle().x / tileW, rmo.getRectangle().y / tileH, rmo.getRectangle().width / tileW, rmo.getRectangle().height / tileH, null, mo);
							}else{
								Gdx.app.log(TAG, "not handled tile name: " + name);
							}
						}
					}
				}
			}
		}
		
		Player player = new Player();
		player.position.set(level.entry);
		// XXX player.size.set(.3f, 1.5f);
		player.size.set(1, 2); // TODO global config (in json file)
		player.dyn = new CDynamic();
		// player.walkSpeed = playerSpeed;
		player.jumpMaxHeight = 5; // mario like : a bit more than 5 blocks
		player.dynScale = 2;
		
		level.players.add(player);
		
		level.worldPosition.set(player.position);
		
		/*
		// XXX some user defined logic : 
		final EBase b12 = level.findEntity("b12");
		final EBase pf12 = level.findEntity("pf12");
		if(b12 != null){
			b12.life.onDead = new Runnable(){
				@Override
				public void run() {
					pf12.dyn = new CDynamic();
					pf12.path = new CPath();
					pf12.path.points.add(pf12.position.cpy());
					pf12.path.points.add(pf12.position.cpy().add(0, 10));
					pf12.path.speed = 1;
					pf12.path.delay = 1;
				}
			};
		}
		*/
		
		return level;
	}
	
}
