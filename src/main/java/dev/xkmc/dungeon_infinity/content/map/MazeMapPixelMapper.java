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

	private static int grayen(int col) {
		if ((col & 0xffffff) == 0) return col;
		int r = (col >> 16) & 0xFF;
		int g = (col >> 8) & 0xFF;
		int b = col & 0xFF;
		r = (r >> 1) + 0x7F;
		g = (g >> 1) + 0x3F;
		b = (b >> 1) + 0x3F;
		return col & 0xFF000000 | r << 16 | g << 8 | b;
	}

	public static int[][] getPixels(int cell, boolean defeated) {
		int flag = cell & 0x1FFF;
		defeated |= CellInterpreter.isHallway(cell);
		if (!defeated) flag |= 0x2000;
		if (CACHE.containsKey(flag))
			return CACHE.get(flag);

		int b = 0xff000000;
		int w = 0xff5f5f5f;
		int a = 0xffafafaf;
		int g = 0xff7fff7f;
		int y = 0xffffaf0f;
		int r = 0xffff7f7f;

		int[][] ans = new int[5][5];

		if (CellInterpreter.isBossRoom(flag)) {
			int boss = CellInterpreter.getBossRoom(flag);
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
		} else if (CellInterpreter.isQuadRoom(flag)) {
			int open = CellInterpreter.getOpenings(flag);
			for (int i = 0; i < 5; i++)
				Arrays.fill(ans[i], r);
			if ((open & 1) == 0) for (int i = 0; i <= 4; i++) ans[0][i] = b;
			if ((open & 2) == 0) for (int i = 0; i <= 4; i++) ans[4][i] = b;
			if ((open & 4) == 0) for (int i = 0; i <= 4; i++) ans[i][0] = b;
			if ((open & 8) == 0) for (int i = 0; i <= 4; i++) ans[i][4] = b;
		} else {
			int open = CellInterpreter.getOpenings(flag);
			int c = (open & 16) != 0 ? y : (open & 32) != 0 ? g : a;
			if (CellInterpreter.isHallway(flag)) {
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
		if (!defeated) {
			for (int i = 0; i < 5; i++) {
				for (int j = 0; j < 5; j++) {
					ans[i][j] = grayen(ans[i][j]);
				}
			}
		}
		CACHE.put(flag, ans);
		return ans;
	}

}
