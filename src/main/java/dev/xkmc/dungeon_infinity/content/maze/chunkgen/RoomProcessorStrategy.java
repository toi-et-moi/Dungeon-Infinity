package dev.xkmc.dungeon_infinity.content.maze.chunkgen;

public class RoomProcessorStrategy {

	public float getRoomChance(int cell) {
		return switch (CellInterpreter.getTemplateType(cell)) {
			case 5 -> 1;
			case 4 -> 0.5f;
			case 2, 3 -> 0.3f;
			default -> 0;
		};
	}

	public float getEndLargeRoomChance() {
		return 0.8f;
	}

	public int getMaxQuadRoom(int max) {
		return Math.clamp((int) (max * 0.8f), Math.min(max, 5), 8);
	}

	public float getHallLargeRoomChance() {
		return 0.8f;
	}

}

