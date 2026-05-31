package dev.xkmc.dungeon_infinity.content.maze.chunkgen;

import dev.xkmc.dungeon_infinity.content.maze.generator.IRandom;
import dev.xkmc.dungeon_infinity.content.maze.generator.MazeConfig;
import dev.xkmc.dungeon_infinity.content.maze.generator.MazeGen;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import org.jspecify.annotations.Nullable;

import java.util.Random;

public class MazeDimHolder {

	private final int r1 = 25;
	private final int r2 = 25;
	private final int y1 = 32;
	private final long seed;
	private final Long2ObjectMap<RegionStack> stacks = new Long2ObjectOpenHashMap<>();

	public MazeDimHolder(long seed) {
		this.seed = seed;
	}

	private RegionStack get(int x, int z) {
		return stacks.computeIfAbsent(ChunkPos.pack(x, z), k0 -> new RegionStack(x, z));
	}

	public synchronized int[] getColumn(int x, int z) {
		int x2 = Mth.floorDiv(x, r2 * r1);
		int z2 = Mth.floorDiv(z, r2 * r1);
		var stack = get(x2, z2);
		int x1 = x - x2 * r2 * r1;
		int z1 = z - z2 * r2 * r1;
		int cx = x1 / r1;
		int cz = z1 / r1;
		int rx = x1 - cx * r1;
		int rz = z1 - cz * r1;
		stack.getColumn(cx, cz).check();
		int[] ans = new int[y1];
		for (int i = 0; i < y1; i++) {
			ans[i] = stack.getRegion(i).getRegion(cx, cz).getCellType(rx, rz);
		}
		return ans;
	}

	private MazeGen getMaze(int rad, long regionSeed) {
		MazeConfig config = new MazeConfig();
		config.invariant = 2;
		config.survive = 4;
		config.invarianceRim = new int[][]{{0, 1, 2, 3, 4, 5, 6, 7}, {0, 4, 8, 12, 1, 2, 3, 5, 6, 7, 9, 10, 11, 13, 14, 15}};
		var ans = new MazeGen(rad / 2, IRandom.parse(new Random(regionSeed)), config, new MazeGen.Debugger());
		ans.gen();
		return ans;
	}

	public class RegionStack {

		public final int x, z;
		public final @Nullable TopRegion[] regions = new TopRegion[y1];
		public final @Nullable MazeColumn[][] columns = new MazeColumn[r2][r2];
		public final @Nullable TopWall[] wallX = new TopWall[y1];
		public final @Nullable TopWall[] wallZ = new TopWall[y1];

		public RegionStack(int x, int z) {
			this.x = x;
			this.z = z;
		}

		public TopWall getWall(int y, Direction.Axis dir) {
			var side = dir == Direction.Axis.X ? wallX : dir == Direction.Axis.Z ? wallZ : null;
			if (side == null) throw new IllegalArgumentException("Wrong axis");
			if (side[y] != null) return side[y];
			var pos = new Vec3i(x, y, z);
			var ans = new TopWall(MazeRandHelper.getRootWallSeed(seed, pos, dir));
			side[y] = ans;
			return ans;
		}

		public TopRegion getRegion(int y) {
			if (regions[y] != null) return regions[y];
			var pos = new Vec3i(x, y, z);
			var regionSeed = MazeRandHelper.getRootCellSeed(seed, pos);
			var region = new TopRegion(regionSeed, pos);
			regions[y] = region;
			return region;
		}

		public MazeColumn getColumn(int cx, int cz) {
			if (columns[cx][cz] != null) return columns[cx][cz];
			var pos = new Vec3i(x * r2 + cx, 0, z * r2 + cz);
			var colSeed = MazeRandHelper.getColumnSeed(seed, pos.getX(), pos.getZ());
			var ans = new MazeColumn(colSeed, cx, cz);
			columns[cx][cz] = ans;
			return ans;
		}

		public class TopRegion {

			private final int y;
			private final int[][] wallX = new int[r2 + 1][r2];
			private final int[][] wallZ = new int[r2][r2 + 1];
			private final long[] subSeed = new long[r2 * r2];
			private final @Nullable SubRegion[][] sub = new SubRegion[r2][r2];

			public TopRegion(long seed, Vec3i pos) {
				this.y = pos.getY();
				int[][] maze = getMaze(r2, seed).ans;
				TopWall x0 = getWall(pos.getY(), Direction.Axis.X);
				TopWall x1 = get(pos.getX() + 1, pos.getZ()).getWall(pos.getY(), Direction.Axis.X);
				TopWall z0 = getWall(pos.getY(), Direction.Axis.Z);
				TopWall z1 = get(pos.getX(), pos.getZ() + 1).getWall(pos.getY(), Direction.Axis.Z);
				MazeRandHelper.getChildrenSeeds(seed, subSeed);
				var rand = new Random(seed);
				for (int dx = 0; dx < r2 - 1; dx++) {
					for (int dz = 0; dz < r2 - 1; dz++) {
						if ((maze[dx][dz] & 2) != 0)
							wallX[dx + 1][dz] = rand.nextInt(r1);
						if ((maze[dx][dz] & 8) != 0)
							wallX[dx][dz + 1] = rand.nextInt(r1);
					}
				}
				for (int i = 0; i < r2; i++) {
					wallZ[i][0] = -1;
					wallZ[i][r2] = -1;
					wallX[0][i] = -1;
					wallX[r2][i] = -1;
				}
				wallX[0][x0.main] = x0.sub;
				wallX[r2][x1.main] = x1.sub;
				wallZ[z0.main][0] = z0.sub;
				wallZ[z1.main][r2] = z1.sub;
			}

			public SubRegion getRegion(int cx, int cz) {
				if (sub[cx][cz] != null) return sub[cx][cz];
				sub[cx][cz] = new SubRegion(subSeed[cx * r2 + cz], cx, cz);
				return sub[cx][cz];
			}

			public int getSubWall(int cx, int cz, Direction.Axis dir) {
				return (dir == Direction.Axis.X ? wallX : wallZ)[cx][cz];
			}

			public class SubRegion {

				private final int cx, cz;
				private final int[][] maze;
				private final MazeColumn col;

				public SubRegion(long seed, int cx, int cz) {
					this.cx = cx;
					this.cz = cz;
					this.maze = getMaze(r1, seed).ans;
					this.col = getColumn(cx, cz);
					int x0 = getSubWall(cx, cz, Direction.Axis.X);
					int x1 = getSubWall(cx + 1, cz, Direction.Axis.X);
					int z0 = getSubWall(cx, cz, Direction.Axis.Z);
					int z1 = getSubWall(cx, cz + 1, Direction.Axis.Z);
					if (x0 >= 0) maze[0][x0] |= 1;
					if (x1 >= 0) maze[r1 - 1][x1] |= 2;
					if (z0 >= 0) maze[z0][0] |= 4;
					if (z1 >= 0) maze[z1][r1 - 1] |= 8;
					if (y >= col.bossRoom && y <= col.bossRoom + 2) {
						int offset = y - col.bossRoom;
						for (int dx = 0; dx <= 2; dx++) {
							for (int dz = 0; dz <= 2; dz++) {
								maze[r1 / 2 - 1 + dx][r1 / 2 - 1 + dz] |= (offset * 9 + dx * 3 + dz + 1) << 6;
							}
						}
					}
				}

				public int getCellType(int x, int z) {
					return maze[x][z];
				}

			}

		}

		public class TopWall {
			private final int main, sub;

			public TopWall(long seed) {
				var rand = new Random(seed);
				main = rand.nextInt(r2);
				sub = rand.nextInt(r1);
			}

		}

		public class MazeColumn {

			private final int cx, cz;
			private final int bossRoom;
			private final long seed;

			private boolean checked = false;

			public MazeColumn(long seed, int cx, int cz) {
				this.cx = cx;
				this.cz = cz;
				bossRoom = new Random(seed).nextInt(y1 - 2);
				this.seed = seed;
			}

			public void check() {
				if (checked) return;
				checked = true;
				var regions = new TopRegion.SubRegion[y1];
				var rand = new Random(seed);
				rand.nextInt();
				int[][] prevMarker = new int[r1][r1];
				int m = 2;
				for (int i = 0; i < y1; i++) {
					regions[i] = getRegion(i).getRegion(cx, cz);
					if (i == 0) continue;
					var low = regions[i - 1].maze;
					var maze = regions[i].maze;
					int[][] marker = new int[r1][r1];
					for (int dx = m; dx < r1 - m; dx++) {
						for (int dz = m; dz < r1 - m; dz++) {
							if ((marker[dx][dz] & 1) != 0 || (prevMarker[dx][dz] & 1) != 0)
								continue;
							boolean stairs = low[dx][dz] == 1 && maze[dx][dz] == 2 ||
									low[dx][dz] == 2 && maze[dx][dz] == 1 ||
									low[dx][dz] == 4 && maze[dx][dz] == 8 ||
									low[dx][dz] == 8 && maze[dx][dz] == 4;
							boolean cross =
									low[dx][dz] == 3 && maze[dx][dz] == 12 ||
											low[dx][dz] == 12 && maze[dx][dz] == 3;
							float chance = 0;
							if (stairs) chance = 0.3f + 0.7f * (i + 1) / y1;
							else if (cross) chance = 0.2f + 0.3f * (i + 1) / y1;
							if (chance <= 0) return;
							float val = rand.nextFloat();
							if (val < chance) {
								low[dx][dz] |= 16;
								maze[dx][dz] |= 32;
								for (int ddx = -m; ddx <= m; ddx++) {
									for (int ddz = -m; ddz <= m; ddz++) {
										marker[dx + ddx][dz + ddz] |= 1;
									}
								}
							}
						}
					}
					prevMarker = marker;
				}
			}

		}
	}

}
