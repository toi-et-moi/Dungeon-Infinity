package dev.xkmc.dungeon_infinity.compat;

import dev.xkmc.dungeon_infinity.init.DungeonInfinity;
import dev.xkmc.golemdungeons.content.config.TrialConfig;
import dev.xkmc.golemdungeons.init.GolemDungeons;
import dev.xkmc.golemdungeons.init.data.spawn.AbstractGolemSpawn;
import dev.xkmc.golemdungeons.init.data.spawn.FactoryGolemSpawn;
import dev.xkmc.l2core.serial.config.ConfigDataProvider;
import net.minecraft.resources.Identifier;

public class GolemSpawnData extends AbstractGolemSpawn {

	public static final Identifier STONE_ROOM = DungeonInfinity.loc("stone/room");
	public static final Identifier STONE_QUAD = DungeonInfinity.loc("stone/quad");
	public static final Identifier STONE_STAIR = DungeonInfinity.loc("stone/stair");
	public static final Identifier STONE_BOSS = DungeonInfinity.loc("stone/boss");

	public static void gen(ConfigDataProvider.Collector col) {

		col.add(GolemDungeons.TRIAL, STONE_ROOM, new TrialConfig()
				.add(of(FactoryGolemSpawn.HUMANOID_BASIC, 1), of(FactoryGolemSpawn.HUMANOID_RANGED, 1))
		);

		col.add(GolemDungeons.TRIAL, STONE_QUAD, new TrialConfig()
				.add(of(FactoryGolemSpawn.LARGE_1, 1), of(FactoryGolemSpawn.HUMANOID_BASIC, 1))
				.add(of(FactoryGolemSpawn.LARGE_1, 1), of(FactoryGolemSpawn.HUMANOID_RANGED, 2))
				.add(of(FactoryGolemSpawn.LARGE_1, 2),
						of(FactoryGolemSpawn.HUMANOID_RANGED, 1),
						of(FactoryGolemSpawn.HUMANOID_TIPPED, 1),
						of(FactoryGolemSpawn.HUMANOID_ROCKET, 1))
		);

		col.add(GolemDungeons.TRIAL, STONE_STAIR, new TrialConfig()
				.add(of(FactoryGolemSpawn.LARGE_1, 1), of(FactoryGolemSpawn.HUMANOID_MELEE, 1), of(FactoryGolemSpawn.HUMANOID_RANGED, 2))
				.add(of(FactoryGolemSpawn.LARGE_2, 2), of(FactoryGolemSpawn.HUMANOID_MELEE, 1),
						of(FactoryGolemSpawn.HUMANOID_RANGED, 2),
						of(FactoryGolemSpawn.HUMANOID_TIPPED, 1),
						of(FactoryGolemSpawn.HUMANOID_ROCKET, 1))
				.add(of(FactoryGolemSpawn.LARGE_2, 3), of(FactoryGolemSpawn.HUMANOID_MELEE, 1),
						of(FactoryGolemSpawn.HUMANOID_RANGED, 2),
						of(FactoryGolemSpawn.HUMANOID_TIPPED, 2),
						of(FactoryGolemSpawn.HUMANOID_ROCKET, 2))
		);

		col.add(GolemDungeons.TRIAL, STONE_BOSS, new TrialConfig()
				.add(of(FactoryGolemSpawn.LARGE_2, 1),
						of(FactoryGolemSpawn.HUMANOID_MELEE, 1),
						of(FactoryGolemSpawn.HUMANOID_TIPPED, 1),
						of(FactoryGolemSpawn.HUMANOID_ROCKET, 1))
				.add(of(FactoryGolemSpawn.LARGE_3, 2), of(FactoryGolemSpawn.HUMANOID_MELEE, 2),
						of(FactoryGolemSpawn.HUMANOID_RANGED, 2),
						of(FactoryGolemSpawn.HUMANOID_TIPPED, 1),
						of(FactoryGolemSpawn.HUMANOID_ROCKET, 1))
				.add(of(FactoryGolemSpawn.LARGE_3, 4), of(FactoryGolemSpawn.HUMANOID_MELEE, 2),
						of(FactoryGolemSpawn.HUMANOID_RANGED, 2),
						of(FactoryGolemSpawn.HUMANOID_TIPPED, 2),
						of(FactoryGolemSpawn.HUMANOID_ROCKET, 2))
		);
	}

}
