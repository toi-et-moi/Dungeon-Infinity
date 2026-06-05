package dev.xkmc.dungeon_infinity.content.maze.chunkgen;

import dev.xkmc.dungeon_infinity.init.DungeonInfinity;
import dev.xkmc.dungeon_infinity.init.reg.DIItems;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.RandomSupport;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public class ChunkFiller {


	public ChunkFiller() {
	}

	public void fillChunk(MazeDimHolder maze, ChunkPos pos, ChunkAccess access, RandomState random) {

	}

	public void decorateChunk(MazeDimHolder maze, ChunkPos pos, WorldGenLevel level, ChunkAccess access, RandomState random, StructureTemplateManager templates) {
		WorldgenRandom r = new WorldgenRandom(new XoroshiroRandomSource(RandomSupport.generateUniqueSeed()));
		var col = maze.getColumn(pos.x(), pos.z());
		for (int i = 0; i < col.length; i++) {
			long seed = r.setDecorationSeed(level.getSeed(), pos.x(), pos.z());
			var p = new BlockPos(pos.x() << 4, i << 4, pos.z() << 4);
			fillCell(col[i], p, level, access, r, templates);
		}
	}

	public void fillCell(int cell, BlockPos o, WorldGenLevel level, ChunkAccess access, RandomSource random, StructureTemplateManager templates) {
		var ins = CellInterpreter.getTemplate(cell);
		if (ins == CellInterpreter.SKIP) return;
		if (ins == CellInterpreter.MISSING) {
			DungeonInfinity.LOGGER.error("unexpected cell type " + cell + " at " + o);
			return;
		}
		var id = DungeonInfinity.loc("maze/" + ins.id());
		var opt = templates.get(id);
		if (opt.isEmpty()) {
			DungeonInfinity.LOGGER.error("template " + ins.id() + " not found");
			return;
		}
		var template = opt.get();
		var settings = new StructurePlaceSettings()
				.setKnownShape(true).setIgnoreEntities(true)
				.setRotation(ins.rot()).setMirror(ins.mir());

		int x0 = 0, x1 = 15, z0 = 0, z1 = 15;
		switch (ins.mir()) {
			case LEFT_RIGHT -> z1 = -z1;
			case FRONT_BACK -> x1 = -x1;
		}

		BlockPos offset = switch (ins.rot()) {
			case COUNTERCLOCKWISE_90 -> new BlockPos(-Math.min(z0, z1), 0, Math.max(x0, x1));
			case CLOCKWISE_90 -> new BlockPos(Math.max(z0, z1), 0, -Math.min(x0, x1));
			case CLOCKWISE_180 -> new BlockPos(Math.max(x0, x1), 0, Math.max(z0, z1));
			default -> new BlockPos(-Math.min(x0, x1), 0, -Math.min(z0, z1));
		};
		o = o.offset(offset);
		template.placeInWorld(level, o, o, settings, random, 18);
	}

}
