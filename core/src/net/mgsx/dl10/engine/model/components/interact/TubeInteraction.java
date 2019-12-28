package net.mgsx.dl10.engine.model.components.interact;

import com.badlogic.gdx.math.Vector2;

import net.mgsx.dl10.assets.GameAssets;
import net.mgsx.dl10.engine.model.CUpdatable;
import net.mgsx.dl10.engine.model.EBase;
import net.mgsx.dl10.engine.model.components.CBlock;
import net.mgsx.dl10.engine.model.components.CInteraction;
import net.mgsx.dl10.engine.model.engines.PlatformerEngine;
import net.mgsx.dl10.engine.model.engines.PlatformerLevel;
import net.mgsx.dl10.engine.model.entities.Player;
import net.mgsx.dl10.engine.model.entities.Player.State;
import net.mgsx.dl10.engine.model.transitions.DefaultTransition;

public class TubeInteraction implements CInteraction
{
	public String targetMap;
	public String targetTube;
	
	/**
	 * 
	 * @param a outside vector to set
	 * @param b inside vector to set
	 * @param player
	 * @param e
	 */
	private void setVectors(Vector2 a, Vector2 b, Player player, EBase e){
		
		float extraV = 2f;
		float extraH = 1f;
		
		if(e.direction == CBlock.TOP){
			a.set(e.position.x + e.size.x/2 - player.size.x/2, e.position.y + e.size.y);
			b.set(a).add(0, -player.size.y * extraV);
		}else if(e.direction == CBlock.BOTTOM){
			b.set(e.position.x + e.size.x/2 - player.size.x/2, e.position.y);
			a.set(b).add(0, -player.size.y);
			b.y += player.size.y * extraV;
		}else if(e.direction == CBlock.RIGHT){
			a.set(e.position.x + e.size.x , e.position.y);
			b.set(a).add(- player.size.x - extraH, 0);
		}else if(e.direction == CBlock.LEFT){
			b.set(e.position.x , e.position.y);
			a.set(b).add(-player.size.x, 0);
			b.x += extraH;
		}
		// TODO other
	}
	
	@Override
	public void onInteraction(final PlatformerEngine engine, final Player player, final EBase e, final int direction) {
		if(direction == e.direction){
			
			if(direction == CBlock.TOP && player.down || direction != CBlock.TOP){ // XXX work with all directions (not sure for left/right
				
				final PlatformerLevel targetLevel = targetMap == null ? engine.level : engine.levels.get(targetMap);
				
				final EBase e2 = targetLevel.findEntity(targetTube);
				
				final Vector2 srcA = new Vector2();
				final Vector2 srcB = new Vector2();
				setVectors(srcA, srcB, player, e);
				
				final Vector2 dstA = new Vector2();
				final Vector2 dstB = new Vector2();
				setVectors(dstA, dstB, player, e2);
				
				final Player targetPlayer = targetLevel.players.first();
				targetPlayer.leftToRight = srcB.x > srcA.x; // XXX fake
				
				player.state = direction == CBlock.TOP ? State.ENTER_V : State.ENTER_H;
				
				GameAssets.i.playSantaEnterIn();
				
				player.sequence.add(new CUpdatable() {
					private float t;
					
					@Override
					public boolean update(EBase e, float delta) {
						t += delta;
						player.position.set(srcA).lerp(srcB, t);
						return t <= 1;
					}
				});
				player.sequence.add(new CUpdatable() {
					@Override
					public boolean update(EBase e, float delta) {
						if(targetLevel != engine.level){
							targetPlayer.position.set(dstB);
						}
						targetLevel.worldPosition.set(e2.position.x + e2.size.x/2, e2.position.y + e2.size.y - 2);
						if(engine.level != targetLevel) engine.transitions.add(new DefaultTransition(targetLevel));
						
						targetPlayer.state = Math.abs(dstB.y - dstA.y) > 0.1f ? State.ENTER_V : State.ENTER_H;
						
						targetPlayer.leftToRight = dstB.x < dstA.x; // XXX fake
						
						return false;
					}
				});
				targetPlayer.sequence.add(new CUpdatable() {
					private float t;
					private boolean first = true;
					@Override
					public boolean update(EBase e, float delta) {
						
						if(first){
							GameAssets.i.playSantaEnterOut();
							first = false;
						}
						
						t += delta;
						targetPlayer.position.set(dstB).lerp(dstA, t);
						// targetPlayer.dyn.velocity.y = 1;
						targetPlayer.dyn.velocity.setZero();
						targetPlayer.velocityTarget.setZero();
						targetPlayer.dyn.lastPosition.set(targetPlayer.position);
						targetPlayer.gravity = 0;
						if(t >= 1){
							targetPlayer.state = null;
							targetPlayer.leftToRight = null;
						}
						return t <= 1;
					}
				});
				
				// TODO disable player common logic
				// make it facing cam and enter the tube
				// when animation is over :
				// change map if needed
				// make it appear from target tube
			}
		}
	}

}
