package net.mgsx.dl10.engine.model;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.dl10.engine.model.components.CBlock;
import net.mgsx.dl10.engine.model.components.CBonus;
import net.mgsx.dl10.engine.model.components.CDynamic;
import net.mgsx.dl10.engine.model.components.CInteraction;
import net.mgsx.dl10.engine.model.components.CLife;
import net.mgsx.dl10.engine.model.components.CLogic;
import net.mgsx.dl10.engine.model.components.CModel;
import net.mgsx.dl10.engine.model.components.CPath;
import net.mgsx.dl10.engine.model.engines.PlatformerEngine;

/**
 * Entity base 
 */
public class EBase {
	
	public final Vector2 position = new Vector2();
	public final Vector2 size = new Vector2();
	public int direction;
	
	public String name;
	public String type;
	
	public CDynamic dyn;
	public CBlock block;
	public CLife life;
	public CPath path;
	public CInteraction interaction;
	public CModel model;
	public CBonus bonus;
	public CLogic logic;
	
	/**
	 * 
	 * @param delta
	 * @return false if entity should be removed.
	 */
	public boolean update(PlatformerEngine engine, float delta){
		if(dyn != null){
			dyn.lastPosition.set(position);
		}
		if(path != null){
			path.update(this, delta);
		}
		if(logic != null){
			logic.update(engine.level, this, delta);
		}
		if(dyn != null){
			dyn.velocity.set(position).sub(dyn.lastPosition).scl(1f / delta);
		}
		return life == null || life.amount > 0;
	}
	
	public Rectangle getBounds(Rectangle r) {
		return r.set(position.x, position.y, size.x, size.y);
	}
}
