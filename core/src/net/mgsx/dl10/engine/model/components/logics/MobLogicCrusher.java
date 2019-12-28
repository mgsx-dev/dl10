package net.mgsx.dl10.engine.model.components.logics;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;

import net.mgsx.dl10.assets.GameAssets;
import net.mgsx.dl10.engine.model.EBase;
import net.mgsx.dl10.engine.model.components.CLogic;
import net.mgsx.dl10.engine.model.engines.PlatformerLevel;

public class MobLogicCrusher implements CLogic {

	private float time;
	private float y;
	private int offset;
	private boolean first;

	public MobLogicCrusher(EBase e) {
		offset = e.name.charAt(0) - 'a';
		y = e.position.y;
	}

	@Override
	public void update(PlatformerLevel level, EBase e, float delta) {
		
		float t;
		
		float time = (this.time + offset) % 4f;
		
		
		float speed = 3;
		
		if(time < 1){
			// up
			this.time += delta / 2f * speed;
			t = time;
		}else if(time < 2){
			// wait
			this.time += delta / 2f * speed;
			t = 1;
		}else if(time < 3){
			// fall
			this.time += delta * 2 * speed;
			t = time - 2;
			t = Interpolation.pow2.apply(1 - t);
			first = true;
		}else if(time < 4){
			if(first){
				// TODO need something more accurate (take w/h in account and maybe an extra)
				float dx = e.position.x - level.worldPosition.x + level.screenBounds.width/2;
				float dy = e.position.y - level.worldPosition.y + level.screenBounds.height/2;
				
				if(dx < level.screenBounds.width && dy < level.screenBounds.height && dx > 0 && dy > 0){
					
					GameAssets.i.playMobFall();
				}
				
				first = false;
			}
			// wait
			this.time += delta / 2f * speed;
			t = 0;
		}else{
			this.time = 0;
			t = 0;
		}
		
		
		e.position.y = MathUtils.lerp(y, y + 5, t);
	}

}
