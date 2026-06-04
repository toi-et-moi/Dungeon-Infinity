package dev.xkmc.dungeon_infinity.content.maze.map;

public record MapVisibilityData(
		int y, Rect rect, byte[] data
) {

	public record Rect(int x0, int z0, int x1, int z1){}

}
