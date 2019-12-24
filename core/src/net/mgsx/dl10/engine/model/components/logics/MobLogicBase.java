package net.mgsx.dl10.engine.model.components.logics;

import com.badlogic.gdx.math.Rectangle;

import net.mgsx.dl10.engine.model.EBase;
import net.mgsx.dl10.engine.model.components.CLogic;
import net.mgsx.dl10.engine.model.engines.PlatformerLevel;

public class MobLogicBase implements CLogic {

	private static final Rectangle r1 = new Rectangle();
	private static final Rectangle r2 = new Rectangle();
	
	@Override
	public void update(PlatformerLevel level, EBase e, float delta) {
		// TODO walk and fall
		// if collide on right or left, then flip direction
		/*
		e.getBounds(r1);
		for(EBase b : level.blocks){
			b.getBounds(r2);
			if(r1.overlaps(r2)){
				if(r1.x )
			}
		}
		*/
	}

}
