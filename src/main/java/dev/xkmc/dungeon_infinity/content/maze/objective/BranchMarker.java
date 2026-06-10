package dev.xkmc.dungeon_infinity.content.maze.objective;

public class BranchMarker extends MazeCellData<BranchMarker, MazeGeneralData> {

	public int color = 0;
	public BranchMarker parent;

	@Override
	public void fillData(MazeGeneralData global, BranchMarker[] children) {
		for (BranchMarker c : children) {
			c.parent = this;
		}
	}

	public int getColor() {
		if (color == 0) {
			color = parent.getColor();
		}
		return color;
	}

	@Override
	public double getResult() {
		return 0;
	}
}
