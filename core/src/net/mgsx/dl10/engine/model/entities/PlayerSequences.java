package net.mgsx.dl10.engine.model.entities;

import net.mgsx.dl10.GameSettings;
import net.mgsx.dl10.engine.model.CUpdatable;
import net.mgsx.dl10.engine.model.EBase;
import net.mgsx.dl10.engine.model.engines.PlatformerEngine;
import net.mgsx.dl10.engine.model.transitions.DefaultTransition;

public class PlayerSequences {

	public static void createDeathSequence(final PlatformerEngine engine, final Player player){
		
		final String currentLevelID = engine.level.prefix;
		
		player.sequence.add(new CUpdatable() {
			float time;
			@Override
			public boolean update(EBase e, float delta) {

				// TODO animate
				time += delta * .5f;
				
				player.position.y += delta;
				
				return time < 1;
			}
		});
		player.sequence.add(new CUpdatable() {
			float time;
			float velocity;
			@Override
			public boolean update(EBase e, float delta) {

				// TODO animate
				time += delta * .5f;
				
				velocity -= delta;
				
				player.position.y += velocity * delta;
				
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
				
				if(time < 0){
					time = 1;
					
					if(engine.bigBonus.contains(count) || true){ /// XXX DEBUG true
						
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
				time += delta * .1f; // 10s
				// TODO play santa happy or unhappy few seconds
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
