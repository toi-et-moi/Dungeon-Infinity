package dev.xkmc.dungeon_infinity.content.chunkgen;

import dev.xkmc.dungeon_infinity.content.config.TemplateConfig;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;

public record CellInstance(String id, int x0, int z0, Rotation rot, Mirror mir) {

	public CellInstance(String id, Rotation rot) {
		this(id, 0, 0, rot, Mirror.NONE);
	}

	public CellInstance(String id) {
		this(id, Rotation.NONE);
	}

	public CellInstance(String id, int x0, int z0) {
		this(id, x0, z0, Rotation.NONE, Mirror.NONE);
	}

	public CellInstance with(Rotation rot) {
		return new CellInstance(id, x0, z0, rot, mir);
	}

	public CellInstance room(String type) {
		return new CellInstance(type + id, x0, z0, rot, mir);
	}

	public CellInstance with(Rotation rot, Mirror mir) {
		return new CellInstance(id, x0, z0, rot, mir);
	}

	public CellInstance mirror(boolean except, Mirror mir) {
		return except ? this : with(rot, mir);
	}

	public CellInstance variant(int style, int variant) {
		return new CellInstance(TemplateConfig.of(id()).path(id(), style, variant), x0, z0, rot, mir);
	}
}
