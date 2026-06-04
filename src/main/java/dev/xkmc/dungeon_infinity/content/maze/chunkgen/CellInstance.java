package dev.xkmc.dungeon_infinity.content.maze.chunkgen;

import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;

public record CellInstance(String id, Rotation rot, Mirror mir) {

	public CellInstance(String id, Rotation rot) {
		this(id, rot, Mirror.NONE);
	}

	public CellInstance(String id) {
		this(id, Rotation.NONE);
	}

	public CellInstance with(Rotation rot) {
		return new CellInstance(id, rot, mir);
	}

	public CellInstance room(String type) {
		return new CellInstance(type + id, rot, mir);
	}


}
