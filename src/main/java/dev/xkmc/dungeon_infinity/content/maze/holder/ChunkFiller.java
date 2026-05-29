package dev.xkmc.dungeon_infinity.content.maze.holder;

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


		if (cell < 16) {
			var state = Blocks.STONE.defaultBlockState();
			for (int i = 0; i < 16; i++) {
				access.setBlockState(m.setWithOffset(o, 0, i, 0), state);
				access.setBlockState(m.setWithOffset(o, 0, i, 15), state);
				access.setBlockState(m.setWithOffset(o, 15, i, 0), state);
				access.setBlockState(m.setWithOffset(o, 15, i, 15), state);
				for (int j = 0; j < 16; j++) {
					access.setBlockState(m.setWithOffset(o, i, 0, j), state);
					access.setBlockState(m.setWithOffset(o, i, 15, j), state);
					if ((cell & 1) == 0) access.setBlockState(m.setWithOffset(o, 0, i, j), state);
					if ((cell & 2) == 0) access.setBlockState(m.setWithOffset(o, 15, i, j), state);
					if ((cell & 4) == 0) access.setBlockState(m.setWithOffset(o, i, j, 0), state);
					if ((cell & 8) == 0) access.setBlockState(m.setWithOffset(o, i, j, 15), state);
				}
			}
		} else {
			var state = Blocks.OBSIDIAN.defaultBlockState();
			int room = (cell >> 4) - 1;
			int layer = room / 9;
			int x = room % 9 / 3;
			int z = room % 3;
			for (int i = 0; i < 16; i++) {
				for (int j = 0; j < 16; j++) {
					if (layer == 0) access.setBlockState(m.setWithOffset(o, i, 0, j), state);
					if (layer == 2) access.setBlockState(m.setWithOffset(o, i, 15, j), state);
					if (x == 0 && (cell & 1) == 0) access.setBlockState(m.setWithOffset(o, 0, i, j), state);
					if (x == 2 && (cell & 2) == 0) access.setBlockState(m.setWithOffset(o, 15, i, j), state);
					if (z == 0 && (cell & 4) == 0) access.setBlockState(m.setWithOffset(o, i, j, 0), state);
					if (z == 2 && (cell & 8) == 0) access.setBlockState(m.setWithOffset(o, i, j, 15), state);
				}
			}
		}
	}


}
