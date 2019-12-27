package net.mgsx.dl10.engine.model.components.logics;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;

import net.mgsx.dl10.engine.model.EBase;
import net.mgsx.dl10.engine.model.components.CLogic;
import net.mgsx.dl10.engine.model.components.CPath;
import net.mgsx.dl10.engine.model.engines.PlatformerLevel;

public class MobLogicJumping implements CLogic {

private CPath cpath;
	
	private float t;
	private boolean inverse;
	private float len;
	private float jump;
	
	public MobLogicJumping(CPath cpath) {
		this.cpath = cpath;
		len = cpath.points.peek().dst(cpath.points.first());
	}

	@Override
	public void update(PlatformerLevel level, EBase e, float delta) {
		
		if(inverse){
			t -= delta / len;
			if(t <= 0){
				inverse = !inverse;
			}
		}else{
			t += delta / len;
			if(t >= 1){
				inverse = !inverse;
			}
		}
		
		jump += delta;
		
		e.position.set(cpath.points.first()).lerp(cpath.points.peek(), t);
		e.position.y += Interpolation.sine.apply(Math.max(0, MathUtils.sin((jump % 1f) * MathUtils.PI2)) ) * 3;
	}

}
