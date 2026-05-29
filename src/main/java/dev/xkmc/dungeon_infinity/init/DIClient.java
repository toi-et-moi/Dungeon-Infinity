package dev.xkmc.dungeon_infinity.init;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = DungeonInfinity.MODID)
public class DIClient {

	@SubscribeEvent
	public static void clientSetup(FMLClientSetupEvent event) {
	}

}
