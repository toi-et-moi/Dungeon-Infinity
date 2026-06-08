package dev.xkmc.dungeon_infinity.events;

import dev.xkmc.dungeon_infinity.init.DungeonInfinity;
import dev.xkmc.dungeon_infinity.init.reg.DIMeta;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

@EventBusSubscriber(modid = DungeonInfinity.MODID)
public class DIEventHandlers {

	@SubscribeEvent
	public static void levelTick(LevelTickEvent.Post event) {
		if (DIMeta.ACTIVE.type().isProper(event.getLevel())) {
			var active = DIMeta.ACTIVE.type().getExisting(event.getLevel());
			if (active.isPresent()) {
				active.get().tick(event.getLevel());
			}
		}
	}

}
