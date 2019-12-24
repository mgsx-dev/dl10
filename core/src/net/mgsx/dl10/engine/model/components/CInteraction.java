package net.mgsx.dl10.engine.model.components;

import net.mgsx.dl10.engine.model.EBase;
import net.mgsx.dl10.engine.model.engines.PlatformerEngine;
import net.mgsx.dl10.engine.model.entities.Player;

public interface CInteraction {

	public void onInteraction(PlatformerEngine engine, Player player, EBase e, int direction);
}
