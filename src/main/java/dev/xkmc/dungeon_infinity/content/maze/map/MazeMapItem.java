package dev.xkmc.dungeon_infinity.content.maze.map;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class MazeMapItem extends Item {

	public MazeMapItem(Properties properties) {
		super(properties);
	}

	public void update(ServerLevel sl, BlockPos pos, MapItemSavedData map) {

	}

}
