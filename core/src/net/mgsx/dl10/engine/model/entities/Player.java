package net.mgsx.dl10.engine.model.entities;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import net.mgsx.dl10.engine.model.CUpdatable;
import net.mgsx.dl10.engine.model.EBase;
import net.mgsx.dl10.engine.model.engines.PlatformerEngine;

public class Player extends EBase {

	public static enum State {
		NONE, DEATH, HAPPY, UNHAPPY, ENTER_V, ENTER_H, PANIC
	}
	
	public State state;

	public boolean hurt;
	
	public final Vector2 velocityTarget = new Vector2();
	
	float force;
	public boolean jumping, onGround, jumpKeep;
	float jumpTimeout;
	public float gravity;
	public EBase block;
	
	public float jumpMaxHeight = 2;
	public float walkSpeed = 5;
	
	public float dynScale = 1f;

	public boolean down;
	
	public final Array<CUpdatable> sequence = new Array<CUpdatable>();

	private float hitTimeout;

	public Boolean leftToRight;

	public boolean panicInv;
	
	public void reset(){
		hitTimeout = -1;
		block = null;
		gravity = 0;
		jumpTimeout = -1;
		jumping = false;
		onGround = false;
		jumpKeep = false;
		force = 0;
		velocityTarget.setZero();
		dyn.lastPosition.set(position);
		dyn.velocity.setZero();
	}
	
	public void jump() {
		
	}

	public void jumpOn() {
		if(jumping || !onGround || jumpKeep) return;
		jumping = true;
		jumpKeep = true;
		// 
		block = null;
		
		jumpTimeout = .2f;
		// force = (((float)Math.pow(height, .5f))/ (jumpTimeout));// / 1.8f;
		
		force = jumpMaxHeight / (float)Math.pow(jumpTimeout, 1f) / 2;
		
		// time * vel + a * t ² + vel * ntime
		
		// dplus = a * t ² + force * t
		
		gravity = 0;
		
		onGround = false;
	}

	public void jumpOff() {
		if(!onGround){
			force = 0;
			gravity = -50f * dynScale;
		}
		jumpKeep = false;
	}
	
	public void onGround(float groundY){
		jumping = false;
		dyn.velocity.y = 0;
		position.y = groundY;
		jumpTimeout = -1;
		onGround = true;
		// gravity = -50; // XXX not sure
		gravity = -1;
	}
	
	@Override
	public boolean update(PlatformerEngine engine, float delta){
		
		if(sequence.size > 0){
			if(!sequence.first().update(this, delta)){
				sequence.removeIndex(0);
			}
			return true;
		}
		
		hitTimeout -= delta;
		
		// XXX input 1 -1 0
		velocityTarget.x *= (onGround ? walkSpeed : walkSpeed) * dynScale;
		
		if(velocityTarget.x != 0){
			dyn.velocity.x = MathUtils.lerp(dyn.velocity.x, velocityTarget.x, Math.min(1, delta * (onGround ? 4f : 4f)));
		}else{
			dyn.velocity.x = MathUtils.lerp(dyn.velocity.x, velocityTarget.x, Math.min(1, delta * (onGround ? 12f : 8f)));
		}
		
		boolean wasTimeout = jumpTimeout <= 0;
		jumpTimeout -= delta;
		if(jumpTimeout <= 0){
			force = 0;
			if(!wasTimeout){
				gravity = -20f * dynScale;
				position.y += dyn.velocity.y * (jumpTimeout + delta);
				// velocity.y = 0;
				System.out.println(position.y);
			}
		}else{
			dyn.velocity.y = force;
		}
		
		return true;
	}

	public void inAir() {
		if(onGround){
			gravity = -50f * dynScale;
		}
		onGround = false;
	}

	public void onWall(float x) {
		// TODO Auto-generated method stub
		
	}

	public void onCeil(EBase e) {
		jumping = false;
		dyn.velocity.y = 0;
		gravity = -50f * dynScale;
		jumpTimeout = -1;
	}

	public void setHit() {
		hitTimeout = 3;
		// TODO animate
	}
	
	public boolean canBeHit(){
		return hitTimeout <= 0;
	}


}
