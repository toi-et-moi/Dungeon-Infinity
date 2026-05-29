package dev.xkmc.dungeon_infinity.content.maze.generator;

public class MazeConfig {

	public int[] path = {3, 6, 51, 0, 0, 0, 0}, loop = {5, 55, 0, 0, 0, 0, 0};
	public double path_fac = 0.35, loop_fac = 0.65, conn_pri = 0.05, conn_sec = 0.75;

	public int invariant = 0, survive = 0;
	public int[][] invarianceRim = {};

	public MazeConfig() {
	}

	public MazeConfig(int[] p, int[] l, double pf, double lf, double c0, double c1) {
		path = p;
		loop = l;
		path_fac = pf;
		loop_fac = lf;
		conn_pri = c0;
		conn_sec = c1;
	}

	public boolean testConn(IRandom r, boolean b) {
		return b ? r.nextDouble() < conn_pri : r.nextDouble() < conn_sec;
	}

	int randLoop(int i, MazeGen.StateRim rim, IRandom r) {
		if (i < invariant) return 0;
		int len = (int) Math.ceil(rim.aviLoop() * loop_fac);
		return randSel(r, loop, rim.path == 0, len);
	}

	int randPath(int i, MazeGen.StateRim rim, IRandom r, int c) {
		if (i <= invariant) return i <= 1 ? 1 : i == 2 ? 4 : 1;
		int len = (int) Math.ceil(rim.aviPath() * path_fac);
		return randSel(r, path, i < survive || c == 1 || !rim.state.isRoot(), len);
	}

	private int randSel(IRandom r, int[] arr, boolean beg, int len) {
		int a = 0, b = 0;
		for (int i = 0; i < arr.length; i++)
			b += arr[i];
		if (beg)
			a += arr[0];
		for (int i = len + 1; i < arr.length; i++)
			b -= arr[i];
		int v = a + r.nextInt(b - a);
		for (int i = 0; i < arr.length; i++) {
			if (v < arr[i])
				return i;
			v -= arr[i];
		}
		return arr.length - 1;
	}

}