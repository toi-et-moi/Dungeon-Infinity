package dev.xkmc.dungeon_infinity.content.map;

import dev.xkmc.dungeon_infinity.content.chunkgen.CellInterpreter;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.Arrays;

public class MazeMapPixelMapper {

	private static final Int2ObjectMap<int[][]> CACHE = new Int2ObjectOpenHashMap<>();

	public static void clear() {
		CACHE.clear();
	}

	public static int[][] getPixels(int cell) {
		cell &= 0x1FFF;
		if (CACHE.containsKey(cell))
			return CACHE.get(cell);

		int b = 0xff000000;
		int w = 0xff5f5f5f;
		int a = 0xffafafaf;
		int g = 0xff7fff7f;
		int y = 0xffffaf0f;
		int r = 0xffff7f7f;

		int[][] ans = new int[5][5];

		if (CellInterpreter.isBossRoom(cell)) {
			int boss = CellInterpreter.getBossRoom(cell);
			int col = boss >= 9 ? g : r;
			int c = boss % 9;
			int x = c / 3;
			int z = c % 3;
			for (int i = 0; i < 5; i++)
				Arrays.fill(ans[i], col);
			if (x == 0 || x == 2) {
				int k = x * 2;
				if (z != 1) {
					for (int i = 0; i < 5; i++)
						ans[k][i] = b;
				} else {
					ans[k][0] = ans[k][4] = b;
				}
			}
			if (z == 0 || z == 2) {
				int k = z * 2;
				if (x != 1) {
					for (int i = 0; i < 5; i++)
						ans[i][k] = b;
				} else {
					ans[0][k] = ans[4][k] = b;
				}
			}
		} else if (CellInterpreter.isQuadRoom(cell)) {
			int open = CellInterpreter.getOpenings(cell);
			for (int i = 0; i < 5; i++)
				Arrays.fill(ans[i], r);
			if ((open & 1) == 0) for (int i = 0; i <= 4; i++) ans[0][i] = b;
			if ((open & 2) == 0) for (int i = 0; i <= 4; i++) ans[4][i] = b;
			if ((open & 4) == 0) for (int i = 0; i <= 4; i++) ans[i][0] = b;
			if ((open & 8) == 0) for (int i = 0; i <= 4; i++) ans[i][4] = b;
		} else {
			int open = CellInterpreter.getOpenings(cell);
			int c = (open & 16) != 0 ? y : (open & 32) != 0 ? g : a;
			if (CellInterpreter.isHallway(cell)) {
				for (int i = 0; i < 5; i++)
					Arrays.fill(ans[i], w);
				if ((open & 1) != 0) for (int i = 0; i <= 2; i++) ans[i][2] = c;
				if ((open & 2) != 0) for (int i = 2; i <= 4; i++) ans[i][2] = c;
				if ((open & 4) != 0) for (int i = 0; i <= 2; i++) ans[2][i] = c;
				if ((open & 8) != 0) for (int i = 2; i <= 4; i++) ans[2][i] = c;
			} else {
				for (int i = 0; i < 5; i++)
					Arrays.fill(ans[i], c);
			}
			ans[0][0] = ans[0][4] = ans[4][0] = ans[4][4] = b;
			if ((open & 1) == 0) for (int i = 1; i <= 3; i++) ans[0][i] = b;
			if ((open & 2) == 0) for (int i = 1; i <= 3; i++) ans[4][i] = b;
			if ((open & 4) == 0) for (int i = 1; i <= 3; i++) ans[i][0] = b;
			if ((open & 8) == 0) for (int i = 1; i <= 3; i++) ans[i][4] = b;
		}

		CACHE.put(cell, ans);
		return ans;
	}

}
