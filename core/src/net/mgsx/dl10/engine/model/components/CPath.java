package net.mgsx.dl10.engine.model.components;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import net.mgsx.dl10.engine.model.CUpdatable;
import net.mgsx.dl10.engine.model.EBase;

public class CPath implements CUpdatable 
{
	public final Array<Vector2> points = new Array<Vector2>();
	public float speed = 1;
	public float delay = 0;
	
	private float t;
	private int pathIndex;
	private float wait;
	
	public void setOffset(Vector2 position){
		
		if(points.size >= 2){
			t = (position.x - points.first().x) / (points.peek().x - points.first().x);
		}
		
	}
	
	@Override
	public boolean update(EBase e, float delta) 
	{
		wait -= delta;
		if(wait > 0) return false;
		
		if(points.size > 0){
			Vector2 p1 = points.get(pathIndex);
			
			if(points.size > 1){
				
				Vector2 p2 = points.get((pathIndex+1) % points.size);
				
				float distance = p1.dst(p2);
				
				float spd = speed / distance;
				
				t += spd * delta;
				
				e.position.set(p1).lerp(p2, t);
				
				if(t > 1){
					t -= 1;
					pathIndex++;
					if(pathIndex >= points.size){
						pathIndex = 0;
					}
					wait = delay;
				}
			}else{
				e.position.set(p1);
			}
		}
		
		return false;
	}

	public boolean isReverse() {
		return pathIndex != 0;
	}
}
