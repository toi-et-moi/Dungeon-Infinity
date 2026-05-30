package dev.xkmc.dungeon_infinity.content.maze.chunkgen;

import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;

import java.util.Random;

public class MazeRandHelper {

	private static final long SEED = -0x6895d6420d877b11L;

	private static final long[] SEEDS;

	static {
		int n = 18;
		SEEDS = new long[n];
		Random r = new Random(SEED);
		for (int i = 0; i < n; i++) {
			SEEDS[i] = shift(r.nextLong(), (i * 3 + 7) % 64);
		}
	}

	private static long shift(long seed, int n) {
		return seed >>> n | seed << (64 - n);
	}

	public static long getRootCellSeed(long seed, Vec3i raw) {
		long sx = (shift(seed, 6) + raw.getX()) * (shift(seed + raw.getX(), 12) + SEEDS[0]) + SEEDS[1];
		long sy = (shift(seed, 8) + raw.getY()) * (shift(seed + raw.getY(), 14) + SEEDS[2]) + SEEDS[3];
		long sz = (shift(seed, 10) + raw.getZ()) * (shift(seed + raw.getZ(), 16) + SEEDS[4]) + SEEDS[5];
		return seed ^ sx ^ sy ^ sz;
	}

	public static long getRootWallSeed(long seed, Vec3i raw, Direction.Axis axis) {
		long sx = (shift(seed, 7) + raw.getX()) * (shift(seed + raw.getX(), 15) + SEEDS[6]) + SEEDS[7];
		long sy = (shift(seed, 9) + raw.getY()) * (shift(seed + raw.getY(), 17) + SEEDS[8]) + SEEDS[9];
		long sz = (shift(seed, 11) + raw.getZ()) * (shift(seed + raw.getZ(), 19) + SEEDS[10]) + SEEDS[11];
		long sf = (shift(seed, 13) + axis.ordinal()) * (shift(seed + axis.ordinal(), 21) + SEEDS[12]) + SEEDS[13];
		return seed ^ sx ^ sy ^ sz ^ sf;
	}

	public static long getColumnSeed(long seed, int x, int z) {
		long sx = (shift(seed, 15) + x) * (shift(seed + x, 9) + SEEDS[14]) + SEEDS[15];
		long sz = (shift(seed, 17) + z) * (shift(seed + z, 13) + SEEDS[16]) + SEEDS[17];
		return seed ^ sx ^ sz;
	}

	public static void getChildrenSeeds(long seed, long[] toFill) {
		Random r = new Random(seed);
		for (int i = 0; i < toFill.length; i++) {
			toFill[i] = shift(r.nextLong(), (i * 3 + 7) % 64);
		}
	}

}
