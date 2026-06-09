package dev.xkmc.dungeon_infinity.init.data;

import dev.xkmc.dungeon_infinity.content.config.TemplateConfig;
import dev.xkmc.dungeon_infinity.init.DungeonInfinity;
import dev.xkmc.l2core.serial.config.ConfigDataProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;

import java.util.concurrent.CompletableFuture;

public class DIConfigGen extends ConfigDataProvider {

	public DIConfigGen(DataGenerator generator, CompletableFuture<HolderLookup.Provider> pvd) {
		super(generator, pvd, "Golem Spawn Config");
	}

	public void add(ConfigDataProvider.Collector map) {
		map.add(DungeonInfinity.TEMPLATES, DungeonInfinity.loc("preset"), new TemplateConfig()
				.start("stone")
				.room("boss").variant("", 100).end()
				.room("quad").variant("", 100).end()
				.room("stairs").variant("", 100).end()
				.room("cross_stairs").variant("", 100).end()
				.room("path/corner").variant("", 100).end()
				.room("path/cross").variant("", 100).end()
				.room("path/straight").variant("", 100).end()
				.room("path/t_way").variant("", 100).end()
				.room("room/end").variant("", 100).end()
				.room("room/corner").variant("", 100).end()
				.room("room/cross").variant("", 100).end()
				.room("room/straight").variant("", 100).end()
				.room("room/t_way").variant("", 100).end()
				.end()

				.start("mineshaft")
				.room("boss").variant("", 100).end()
				.room("quad").variant("", 100).end()
				.room("stairs").variant("", 100).end()
				.room("cross_stairs").variant("", 100).end()
				.room("path/corner").variant("", 100).end()
				.room("path/cross").variant("", 100).end()
				.room("path/straight").variant("", 100).end()
				.room("path/t_way").variant("", 100).end()
				.room("room/end").variant("", 100).end()
				.room("room/corner").variant("", 100).end()
				.room("room/cross").variant("", 100).end()
				.room("room/straight").variant("", 100).end()
				.room("room/t_way").variant("", 100).end()
				.end()

				.start("copper")
				.room("boss").variant("", 100).end()
				.room("quad").variant("", 100).end()
				.room("stairs").variant("", 100).end()
				.room("cross_stairs").variant("", 100).end()
				.room("path/corner").variant("", 100).end()
				.room("path/cross").variant("", 100).end()
				.room("path/straight").variant("", 100).end()
				.room("path/t_way").variant("", 100).end()
				.room("room/end").variant("", 100).end()
				.room("room/corner").variant("", 100).end()
				.room("room/cross").variant("", 100).end()
				.room("room/straight").variant("", 100).end()
				.room("room/t_way").variant("", 100).end()
				.end()

				.start("deepslate")
				.room("boss").variant("", 100).end()
				.room("quad").variant("", 100).end()
				.room("stairs").variant("", 100).end()
				.room("cross_stairs").variant("", 100).end()
				.room("path/corner").variant("", 100).end()
				.room("path/cross").variant("", 100).end()
				.room("path/straight").variant("", 100).end()
				.room("path/t_way").variant("", 100).end()
				.room("room/end").variant("", 100).end()
				.room("room/corner").variant("", 100).end()
				.room("room/cross").variant("", 100).end()
				.room("room/straight").variant("", 100).end()
				.room("room/t_way").variant("", 100).end()
				.end()

				.start("sculk")
				.room("boss").variant("", 100).end()
				.room("quad").variant("", 100).end()
				.room("stairs").variant("", 100).end()
				.room("cross_stairs").variant("", 100).end()
				.room("path/corner").variant("", 100).end()
				.room("path/cross").variant("", 100).end()
				.room("path/straight").variant("", 100).end()
				.room("path/t_way").variant("", 100).end()
				.room("room/end").variant("", 100).end()
				.room("room/corner").variant("", 100).end()
				.room("room/cross").variant("", 100).end()
				.room("room/straight").variant("", 100).end()
				.room("room/t_way").variant("", 100).end()
				.end()
		);
	}

}
