package net.mgsx.dl10.engine.model.components.logics;

import net.mgsx.dl10.engine.model.EBase;
import net.mgsx.dl10.engine.model.components.CLogic;
import net.mgsx.dl10.engine.model.components.CPath;
import net.mgsx.dl10.engine.model.engines.PlatformerLevel;

public class MobLogicRolling implements CLogic {
	
	private CPath cpath;
	
	private float t;
	private boolean inverse;
	private float len;
	
	public MobLogicRolling(CPath cpath) {
		this.cpath = cpath;
		len = cpath.points.peek().dst(cpath.points.first());
	}

	@Override
	public void update(PlatformerLevel level, EBase e, float delta) {
		
		float speed = 3;
		
		if(inverse){
			t -= delta / len * speed;
			if(t <= 0){
				inverse = !inverse;
			}
		}else{
			t += delta / len * speed;
			if(t >= 1){
				inverse = !inverse;
			}
		}
		
		e.position.set(cpath.points.first()).lerp(cpath.points.peek(), t);
		e.rotation = -t * len * 40;
	}

}
