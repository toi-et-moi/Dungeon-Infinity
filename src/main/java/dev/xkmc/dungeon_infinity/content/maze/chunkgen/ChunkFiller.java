package dev.xkmc.dungeon_infinity.content.maze.chunkgen;

import dev.xkmc.dungeon_infinity.init.reg.DIItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.RandomState;

public class ChunkFiller {

	public ChunkFiller() {
	}

	public void fillChunk(MazeDimHolder maze, ChunkPos pos, ChunkAccess access, RandomState random) {
		var col = maze.getColumn(pos.x(), pos.z());
		for (int i = 0; i < col.length; i++) {
			fillCell(col[i], SectionPos.of(pos, i), access, random);
		}
	}

	public void fillCell(int cell, SectionPos pos, ChunkAccess access, RandomState random) {
		BlockPos o = pos.origin();
		BlockPos.MutableBlockPos m = new BlockPos.MutableBlockPos();
		boolean doCeiling = o.getY() + 15 < 255;

		if (cell < 64) {
			var state = DIItems.MAZESTONE.getDefaultState();
			for (int i = 0; i < 16; i++) {
				access.setBlockState(m.setWithOffset(o, 0, i, 0), state);
				access.setBlockState(m.setWithOffset(o, 0, i, 15), state);
				access.setBlockState(m.setWithOffset(o, 15, i, 0), state);
				access.setBlockState(m.setWithOffset(o, 15, i, 15), state);

				access.setBlockState(m.setWithOffset(o, i, 0, 0), state);
				access.setBlockState(m.setWithOffset(o, i, 0, 15), state);
				access.setBlockState(m.setWithOffset(o, 0, 0, i), state);
				access.setBlockState(m.setWithOffset(o, 15, 0, i), state);
				if (doCeiling) {
					access.setBlockState(m.setWithOffset(o, i, 15, 0), state);
					access.setBlockState(m.setWithOffset(o, i, 15, 15), state);
					access.setBlockState(m.setWithOffset(o, 0, 15, i), state);
					access.setBlockState(m.setWithOffset(o, 15, 15, i), state);
				}
				for (int j = 1; j < 16; j++) {
					if ((cell & 32) == 0) access.setBlockState(m.setWithOffset(o, i, 0, j), state);
					if ((cell & 16) == 0 && doCeiling) access.setBlockState(m.setWithOffset(o, i, 15, j), state);
					if (doCeiling || j < 15) {
						if ((cell & 1) == 0) access.setBlockState(m.setWithOffset(o, 0, j, i), state);
						if ((cell & 2) == 0) access.setBlockState(m.setWithOffset(o, 15, j, i), state);
						if ((cell & 4) == 0) access.setBlockState(m.setWithOffset(o, i, j, 0), state);
						if ((cell & 8) == 0) access.setBlockState(m.setWithOffset(o, i, j, 15), state);
					}
				}
			}
			if ((cell & 16) != 0) {
				for (int y = 1; y < 16; y++) {
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
					if (layer == 2 && doCeiling) access.setBlockState(m.setWithOffset(o, i, 15, j), state);
					if (x == 0 && (cell & 1) == 0) access.setBlockState(m.setWithOffset(o, 0, i, j), state);
					if (x == 2 && (cell & 2) == 0) access.setBlockState(m.setWithOffset(o, 15, i, j), state);
					if (z == 0 && (cell & 4) == 0) access.setBlockState(m.setWithOffset(o, i, j, 0), state);
					if (z == 2 && (cell & 8) == 0) access.setBlockState(m.setWithOffset(o, i, j, 15), state);
				}
			}
		}
	}


}
