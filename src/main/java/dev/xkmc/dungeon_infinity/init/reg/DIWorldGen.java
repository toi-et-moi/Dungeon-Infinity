package dev.xkmc.dungeon_infinity.init.reg;

import dev.xkmc.dungeon_infinity.content.chunkgen.MazeChunkGenerator;
import dev.xkmc.dungeon_infinity.init.DungeonInfinity;
import dev.xkmc.l2core.init.reg.simple.CdcReg;
import dev.xkmc.l2core.init.reg.simple.CdcVal;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.chunk.ChunkGenerator;

public class DIWorldGen {

	private static final CdcReg<ChunkGenerator> CGR = CdcReg.of(DungeonInfinity.REG, BuiltInRegistries.CHUNK_GENERATOR);
	public static final CdcVal<MazeChunkGenerator> CG_MAZE = CGR.reg("maze", MazeChunkGenerator.CODEC);

	static {

	}

	public static void register() {
	}

}
