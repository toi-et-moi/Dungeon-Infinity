package dev.xkmc.dungeon_infinity.content.maze.chunkgen;

import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;

public record CellInstance(String id, int len, int x0, int z0, Rotation rot, Mirror mir) {

	public CellInstance(String id, int len, Rotation rot) {
		this(id, len, 0, 0, rot, Mirror.NONE);
	}

	public CellInstance(String id, int len) {
		this(id, len, Rotation.NONE);
	}

	public CellInstance(String id) {
		this(id, 16);
	}

	public CellInstance(String id, int len, int x0, int z0) {
		this(id, len, x0, z0, Rotation.NONE, Mirror.NONE);
	}

	public CellInstance with(Rotation rot) {
		return new CellInstance(id, len, x0, z0, rot, mir);
	}

	public CellInstance room(String type) {
		return new CellInstance(type + id, len, x0, z0, rot, mir);
	}

	public CellInstance with(Rotation rot, Mirror mir) {
		return new CellInstance(id, len, x0, z0, rot, mir);
	}

	public CellInstance mirror(boolean except, Mirror mir) {
		return except ? this : with(rot, mir);
	}

}
