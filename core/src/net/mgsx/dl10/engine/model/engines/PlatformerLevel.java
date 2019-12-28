package net.mgsx.dl10.engine.model.engines;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

import net.mgsx.dl10.GameSettings;
import net.mgsx.dl10.engine.model.EBase;
import net.mgsx.dl10.engine.model.components.CBlock;
import net.mgsx.dl10.engine.model.entities.Player;
import net.mgsx.dl10.engine.model.entities.Player.State;
import net.mgsx.dl10.engine.model.entities.PlayerSequences;
import net.mgsx.dl10.engine.model.renderer.PlatformerRenderer.CameraAnim;
import net.mgsx.gltf.scene3d.scene.Scene;

public class PlatformerLevel {

	public final Array<EBase> blocks = new Array<EBase>();
	public final Array<EBase> chars = new Array<EBase>();
	public final Array<Player> players = new Array<Player>();
	public final Vector2 worldPosition = new Vector2();
	public final Rectangle worldBounds = new Rectangle();
	public final Rectangle screenBounds = new Rectangle();
	
	private final Array<EBase> toRemove = new Array<EBase>();
	
	private final Rectangle r1 = new Rectangle();
	private final Rectangle r2 = new Rectangle();
	
	private final Vector2 v1 = new Vector2();
	private final Vector2 v2 = new Vector2();
	
	public final Vector2 entry = new Vector2();
	
	public final PlatformerEngine engine;
	
	public final Array<Scene> scenes = new Array<Scene>();
	
	public String prefix;
	public boolean screenClamp = true;
	public float screenZoom = 1;
	public Texture bgTexture;
	public float viewOffset = 0;
	public Actor hud;
	public CameraAnim cameraAnim;
	
	public PlatformerLevel(PlatformerEngine engine) {
		super();
		this.engine = engine;
	}

	public void update(float delta) 
	{
		for(EBase b : blocks){
			if(!b.update(engine, delta)){
				toRemove.add(b);
			}
		}
		blocks.removeAll(toRemove, true);
		toRemove.clear();
		
		for(EBase c : chars){
			if(!c.update(engine, delta)){
				toRemove.add(c);
			}
		}
		chars.removeAll(toRemove, true);
		toRemove.clear();
		
		for(Player player : players){
			
			if(GameSettings.debug){
				if(Gdx.input.isKeyPressed(Input.Keys.F)){
					player.state = State.DEATH;
				}
				if(Gdx.input.isKeyPressed(Input.Keys.X)){
					player.state = State.ENTER_H;
				}
				if(Gdx.input.isKeyPressed(Input.Keys.C)){
					player.state = State.ENTER_V;
				}
				if(Gdx.input.isKeyPressed(Input.Keys.V)){
					player.state = State.HAPPY;
				}
				if(Gdx.input.isKeyPressed(Input.Keys.B)){
					player.state = State.UNHAPPY;
				}
				if(Gdx.input.isKeyPressed(Input.Keys.N)){
					player.state = State.NONE;
				}
				if(Gdx.input.isKeyPressed(Input.Keys.SPACE)){
					player.state = null;
				}
			}
			
			player.dyn.lastPosition.set(player.position);
			
			player.update(engine, delta);
			
			if(engine.level != this){
				player.reset();
				return; // level has changed
			}
			
			if(player.sequence.size > 0) continue;
			
			if(player.block != null && player.block.path != null){
				EBase bm = player.block;
				
				// player.velocity.add(dx / delta, dy / delta);
				player.position.mulAdd(bm.dyn.velocity, delta);
				player.dyn.lastPosition.add(0, bm.dyn.velocity.y * delta + .2f);
				player.dyn.velocity.y = 0;
			}
			
			float pvely = player.dyn.velocity.y;
			player.dyn.velocity.y += player.gravity * delta;
			if(pvely > 0 && player.dyn.velocity.y <= 0){
				System.out.println(player.position.y);
			}
			
			boolean onGround = false;
			
			player.position.mulAdd(player.dyn.velocity, delta);
			player.block = null;
			
			player.getBounds(r2);
			
			// One way platforms
			for(EBase e : blocks){
				CBlock b = e.block;
				e.getBounds(r1);
				if(b.sensor){
					if(r1.overlaps(r2)){
						if(e.life != null){
							e.life.decrease();
						}
						if(e.bonus != null && !e.bonus.fake){
							if(e.bonus.life){
								if(engine.playerLife < GameSettings.playerLifeMax){
									engine.playerLife++;
								}else{
									if(engine.playerContinues < GameSettings.playerContinues){
										engine.playerContinues++;
									}
								}
							}
							else if(e.bonus.superBonus){
								engine.bigBonus.add(e.bonus.varIndex);
								engine.smallBonus += GameSettings.bigBonusPoints;
							}else{
								engine.smallBonus++;
							}
							if(engine.smallBonus >= GameSettings.bonusPerLife){
								engine.smallBonus -= GameSettings.bonusPerLife;
								if(engine.playerLife < GameSettings.playerLifeMax){
									engine.playerLife++;
								}else{
									if(engine.playerContinues < GameSettings.playerContinues){
										engine.playerContinues++;
									}
								}
							}
						}
						if("end-game".equals(e.type)){
							PlayerSequences.createGameEndSequence(engine, player);
						}
					}
					continue;
				}
				
				if((b.normals & CBlock.TOP) != 0){
					if(player.dyn.velocity.y < 0 || true){
						if(player.position.x < r1.x + r1.width && player.position.x + player.size.x > r1.x){
							if(player.position.y <= r1.y + r1.height){
								if(player.dyn.lastPosition.y >= r1.y + r1.height){
									player.onGround(r1.y + r1.height);
									player.block = e;
									onGround = true;
									
									if(e.interaction != null) e.interaction.onInteraction(engine, player, e, CBlock.TOP);
									
									// TODO if player in super fall and have life then ...
								}
							}
						}
					}
				}
				
				if((b.normals & CBlock.BOTTOM) != 0){
					if(player.dyn.velocity.y > 0 || true){
						if(player.position.x < r1.x + r1.width && player.position.x + player.size.x > r1.x){
							if(player.position.y + player.size.y >= r1.y){
								if(player.dyn.lastPosition.y + player.size.y <= r1.y){
									player.position.y = r1.y - player.size.y;
									player.dyn.velocity.y = 0;
									player.onCeil(e);
									
									if(e.interaction != null) e.interaction.onInteraction(engine, player, e, CBlock.BOTTOM);
									
									if(e.life != null){
										e.life.decrease();
									}
								}
							}
						}
					}
				}
				
				if((b.normals & CBlock.LEFT) != 0){
					if(player.dyn.velocity.x > 0 || true){
						if(player.position.y < r1.y + r1.height && player.position.y + player.size.y > r1.y){
							if(player.position.x + player.size.x > r1.x){
								if(player.dyn.lastPosition.x + player.size.x <= r1.x){
									player.position.x = r1.x - player.size.x;
									player.dyn.velocity.x = 0;
									
									if(e.interaction != null) e.interaction.onInteraction(engine, player, e, CBlock.LEFT);
								}
							}
						}
					}
					
				}
				
				if((b.normals & CBlock.RIGHT) != 0){
					if(player.dyn.velocity.x < 0 || true){
						if(player.position.y < r1.y + r1.height && player.position.y + player.size.y > r1.y){
							if(player.position.x < r1.x + r1.width){
								if(player.dyn.lastPosition.x >= r1.x + r1.width){
									player.position.x = r1.x + r1.width;
									player.dyn.velocity.x = 0;
									
									if(e.interaction != null) e.interaction.onInteraction(engine, player, e, CBlock.RIGHT);
								}
							}
						}
					}
					
				}
					
			}
			if(!onGround && player.block == null){
				player.inAir();
			}
			
			boolean panic = false;
			if(onGround && player.block != null){
				// get panic state depends on orientation and block borders
				if(player.position.x + player.size.x/2 < player.block.position.x){
					panic = true;
					player.panicInv = player.dyn.velocity.x > 0;
				}
				if(player.position.x + player.size.x/2 > player.block.position.x + player.block.size.x){
					panic = true;
					player.panicInv = player.dyn.velocity.x < 0;
				}
			}
			if(!panic && player.state == State.PANIC){
				player.state = null;
			}else if(panic && player.state == null){
				player.state = State.PANIC;
			}
			
			
			player.getBounds(r1);
			if(!r1.overlaps(worldBounds)){
				if(player.position.y < 0){
					PlayerSequences.createDeathSequence(engine, player);
				}
			}
			
				
			r1.getCenter(v1);
			// v1.y = player.position.y;
			
			boolean hit = false;
			
			for(EBase c : chars){
				
				// TODO check if collide with feets, head or sides
				c.getBounds(r2);
				
				if(r1.overlaps(r2)){
					
					// find angle
					r2.getCenter(v2).sub(v1).nor();
					
					if(Math.abs(v2.x) > .5f){
						// side
						if(player.canBeHit()){
							
							player.dyn.velocity.x = v2.x > 0 ? -10 : 10;
						}
						
						hit = true;
					}else if(v2.y < 0){
						// feets
						if(player.jumpKeep){
							player.dyn.velocity.y = 12; // because of gravity
						}else{
							player.dyn.velocity.y = 15;
						}
						if(c.life != null){
							c.life.decrease();
						}
					}else{
						// head
						if(player.canBeHit()){
							player.dyn.velocity.x = v2.x > 0 ? -10 : 10;
						}
						hit = true;
					}
					
					// XXX for now just add a force to player (bouncing out)
				}
				
			}
			
			if(hit && player.canBeHit()){
				
				engine.playerLife--;
				if(engine.playerLife <= 0){
					
					// TODO set dead
					PlayerSequences.createDeathSequence(engine, player);
					
				}else{
					// TODO blinking
					player.setHit();
					
				}
				
			}
		}
			
			
		
		
	}

	public EBase findEntity(String name) {
		for(EBase b : blocks){
			if(name.equals(b.name)) return b;
		}
		for(EBase c : chars){
			if(name.equals(c.name)) return c;
		}
		return null;
	}
}
