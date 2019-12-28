package net.mgsx.dl10.engine.model.entities;

import com.badlogic.gdx.math.Interpolation;

import net.mgsx.dl10.GameSettings;
import net.mgsx.dl10.engine.model.CUpdatable;
import net.mgsx.dl10.engine.model.EBase;
import net.mgsx.dl10.engine.model.engines.PlatformerEngine;
import net.mgsx.dl10.engine.model.entities.Player.State;
import net.mgsx.dl10.engine.model.transitions.DefaultTransition;

public class PlayerSequences {

	public static void createDeathSequence(final PlatformerEngine engine, final Player player){
		
		final String currentLevelID = engine.level.prefix;
		
		final float playerY = player.position.y;
		
		final float maxY = 5;
		
		player.sequence.add(new CUpdatable() {
			float time;
			@Override
			public boolean update(EBase e, float delta) {

				player.state = State.DEATH;
				
				time += delta * 2f;
				
				player.position.y = playerY + Interpolation.pow2Out.apply(time) * maxY;
				
				return time < 1;
			}
		});
		player.sequence.add(new CUpdatable() {
			float time;
			@Override
			public boolean update(EBase e, float delta) {

				// TODO animate
				time += delta * 1f;
				
				player.position.y = playerY + maxY - Interpolation.pow2In.apply(time) * 30;
				
				return time < 1;
			}
		});
		player.sequence.add(new CUpdatable() {
			@Override
			public boolean update(EBase e, float delta) {
				engine.transitions.add(new DefaultTransition(null){
					@Override
					protected void swap(PlatformerEngine engine) {
						engine.playerContinues--;
						if(engine.playerContinues > 0){
							engine.reset(currentLevelID);
						}else{
							engine.start();
						}
					}
				});
				
				return false;
			}
		});
	}
	
	public static void createGameEndSequence(final PlatformerEngine engine, final Player player){
		// TODO a lot of stuff to do...
		player.sequence.add(new CUpdatable() {
			private float time;
			private int count;
			@Override
			public boolean update(EBase e, float delta) {
				time -= delta;
				
				player.state = State.NONE;
				
				if(time < 0){
					time = 1;
					
					// TODO skip not containing
					
					if(engine.bigBonus.contains(count)){ // || true debug XXX
						
						for(EBase b : engine.level.blocks){
							if(b.bonus != null && b.bonus.fake && b.bonus.varIndex == count){
								b.bonus.fake = false;
							}
						}
						
					}
					
					count++;
				}
				
				
				return count < GameSettings.bigBonusMax;
			}
		});
		
		
		player.sequence.add(new CUpdatable() {
			private float time;
			@Override
			public boolean update(EBase e, float delta) {
				// play santa happy or unhappy few seconds
				time += delta / 5;
				if(engine.bigBonus.size < 6){
					player.state = State.UNHAPPY;
				}else{
					player.state = State.HAPPY;
				}
				return time < 1;
			}
		});
		player.sequence.add(new CUpdatable() {
			@Override
			public boolean update(EBase e, float delta) {
				engine.transitions.add(new DefaultTransition(null){
					@Override
					protected void swap(PlatformerEngine engine) {
						engine.start();
					}
				});
				return false;
			}
		});
	}
}
