package dev.xkmc.dungeon_infinity.content.maze.cap;

import net.minecraft.core.Direction;

public class MobRoomIns {

	private final SectionRoom[][][] rooms;

	public MobRoomIns(SectionRoom[][][] rooms) {
		this.rooms = rooms;
	}

	public void setWall(boolean gen) {
		int xn = rooms.length;
		for (int x = 0; x < xn; x++) {
			int yn = rooms[x].length;
			for (int y = 0; y < yn; y++) {
				int zn = rooms[x][y].length;
				for (int z = 0; z < zn; z++) {
					var room = rooms[x][y][z];
					if (room == null) continue;
					int cell = room.getCell();
					if ((cell & 1) != 0 && (x == 0 || rooms[x - 1][y][z] == null))
						room.setWall(Direction.WEST, gen);
					if ((cell & 2) != 0 && (x >= xn - 1 || rooms[x + 1][y][z] == null))
						room.setWall(Direction.EAST, gen);
					if ((cell & 4) != 0 && (z == 0 || rooms[x][y][z - 1] == null))
						room.setWall(Direction.SOUTH, gen);
					if ((cell & 8) != 0 && (z >= zn - 1 || rooms[x][y][z + 1] == null))
						room.setWall(Direction.NORTH, gen);
					if ((cell & 32) != 0)
						room.setWall(Direction.DOWN, gen);
				}
			}
		}
	}

}
