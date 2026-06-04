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
			var p = new BlockPos(pos.x() << 4, i << 3, pos.z() << 4);
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

	public void fillCellTest(int cell, BlockPos o, ChunkAccess access, RandomState random) {
		BlockPos.MutableBlockPos m = new BlockPos.MutableBlockPos();
		boolean doCeiling = o.getY() + 7 < 255;
		if (cell < 64) {
			var state = DIItems.MAZESTONE.getDefaultState();
			for (int i = 0; i < 8; i++) {
				access.setBlockState(m.setWithOffset(o, 0, i, 0), state);
				access.setBlockState(m.setWithOffset(o, 0, i, 15), state);
				access.setBlockState(m.setWithOffset(o, 15, i, 0), state);
				access.setBlockState(m.setWithOffset(o, 15, i, 15), state);
			}
			for (int i = 0; i < 16; i++) {
				access.setBlockState(m.setWithOffset(o, i, 0, 0), state);
				access.setBlockState(m.setWithOffset(o, i, 0, 15), state);
				access.setBlockState(m.setWithOffset(o, 0, 0, i), state);
				access.setBlockState(m.setWithOffset(o, 15, 0, i), state);
				if (doCeiling) {
					access.setBlockState(m.setWithOffset(o, i, 7, 0), state);
					access.setBlockState(m.setWithOffset(o, i, 7, 15), state);
					access.setBlockState(m.setWithOffset(o, 0, 7, i), state);
					access.setBlockState(m.setWithOffset(o, 15, 7, i), state);
				}
				for (int j = 1; j < 16; j++) {
					if ((cell & 32) == 0) access.setBlockState(m.setWithOffset(o, i, 0, j), state);
					if ((cell & 16) == 0 && doCeiling) access.setBlockState(m.setWithOffset(o, i, 7, j), state);
				}
				for (int y = 1; y < 8; y++) {
					if (doCeiling || y < 7) {
						if ((cell & 1) == 0) access.setBlockState(m.setWithOffset(o, 0, y, i), state);
						if ((cell & 2) == 0) access.setBlockState(m.setWithOffset(o, 15, y, i), state);
						if ((cell & 4) == 0) access.setBlockState(m.setWithOffset(o, i, y, 0), state);
						if ((cell & 8) == 0) access.setBlockState(m.setWithOffset(o, i, y, 15), state);
					}
				}
			}
			if ((cell & 16) != 0 && (cell == 1 || cell == 2 || cell == 4 || cell == 8)) {
				for (int y = 1; y < 8; y++) {
					for (int i = y - 1; i < 15; i++) {
						for (int j = 1; j < 15; j++) {
							if ((cell & 1) != 0) access.setBlockState(m.setWithOffset(o, i, y, j), state);
							if ((cell & 2) != 0) access.setBlockState(m.setWithOffset(o, 15 - i, y, j), state);
							if ((cell & 4) != 0) access.setBlockState(m.setWithOffset(o, j, y, i), state);
							if ((cell & 8) != 0) access.setBlockState(m.setWithOffset(o, j, y, 15 - i), state);
						}
					}
				}
			}
		} else {
			var state = DIItems.MAZESTONE.getDefaultState();
			int room = (cell >> 6) - 1;
			int layer = room / 9;
			int x = room % 9 / 3;
			int z = room % 3;
			for (int i = 0; i < 16; i++) {
				for (int j = 0; j < 16; j++) {
					if (layer == 0) access.setBlockState(m.setWithOffset(o, i, 0, j), state);
					if (layer == 2 && doCeiling) access.setBlockState(m.setWithOffset(o, i, 7, j), state);
				}
				for (int y = 0; y < 8; y++) {
					if (x == 0 && (cell & 1) == 0) access.setBlockState(m.setWithOffset(o, 0, y, i), state);
					if (x == 2 && (cell & 2) == 0) access.setBlockState(m.setWithOffset(o, 15, y, i), state);
					if (z == 0 && (cell & 4) == 0) access.setBlockState(m.setWithOffset(o, i, y, 0), state);
					if (z == 2 && (cell & 8) == 0) access.setBlockState(m.setWithOffset(o, i, y, 15), state);
				}
			}
		}
	}


}
