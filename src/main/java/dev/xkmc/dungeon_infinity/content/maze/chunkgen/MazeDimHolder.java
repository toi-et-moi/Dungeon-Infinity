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

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;
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

	public synchronized int[][] getRegion(int x, int y, int z) {
		int x2 = Mth.floorDiv(x, r2);
		int z2 = Mth.floorDiv(z, r2);
		var stack = get(x2, z2);
		int cx = x - x2 * r2;
		int cz = z - z2 * r2;
		stack.getColumn(cx, cz).check();
		return stack.getRegion(y).getRegion(cx, cz).getMaze();
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

	private MazeGen genMaze(int rad, long regionSeed) {
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
				long[] seeds = new long[3];
				MazeRandHelper.getChildrenSeeds(seed, seeds);
				int[][] maze = genMaze(r2, seeds[0]).ans;
				TopWall x0 = getWall(pos.getY(), Direction.Axis.X);
				TopWall x1 = get(pos.getX() + 1, pos.getZ()).getWall(pos.getY(), Direction.Axis.X);
				TopWall z0 = getWall(pos.getY(), Direction.Axis.Z);
				TopWall z1 = get(pos.getX(), pos.getZ() + 1).getWall(pos.getY(), Direction.Axis.Z);
				MazeRandHelper.getChildrenSeeds(seeds[1], subSeed);
				var rand = new Random(seeds[2]);
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
				public final long roomSeed;

				private boolean checked = false;

				public SubRegion(long seed, int cx, int cz) {
					this.cx = cx;
					this.cz = cz;
					long[] seeds = new long[2];
					MazeRandHelper.getChildrenSeeds(seed, seeds);
					this.maze = genMaze(r1, seeds[0]).ans;
					roomSeed = seeds[1];
					this.col = getColumn(cx, cz);
					int x0 = getSubWall(cx, cz, Direction.Axis.X);
					int x1 = getSubWall(cx + 1, cz, Direction.Axis.X);
					int z0 = getSubWall(cx, cz, Direction.Axis.Z);
					int z1 = getSubWall(cx, cz + 1, Direction.Axis.Z);
					if (x0 >= 0) maze[0][x0] |= 1;
					if (x1 >= 0) maze[r1 - 1][x1] |= 2;
					if (z0 >= 0) maze[z0][0] |= 4;
					if (z1 >= 0) maze[z1][r1 - 1] |= 8;
					if (col.bossRoom[y] > 0) {
						int offset = col.bossRoom[y] - 1;
						for (int dx = 0; dx <= 2; dx++) {
							for (int dz = 0; dz <= 2; dz++) {
								maze[r1 / 2 - 1 + dx][r1 / 2 - 1 + dz] |= CellInterpreter.setBossRoom(offset * 9 + dx * 3 + dz + 1);
							}
						}
					}
				}

				public int[][] getMaze() {
					check();
					return maze;
				}

				public int getCellType(int x, int z) {
					check();
					return maze[x][z];
				}

				private void check() {
					if (checked) return;
					checked = true;
					col.check();
					var rand = new Random(roomSeed);
					int[][] roomType = new int[r2][r2];
					new Marker(rand, roomType, maze).mark();
					for (int x = 0; x < r2; x++) {
						for (int z = 0; z < r2; z++) {
							maze[x][z] |= CellInterpreter.getRoomTypeMask(maze[x][z], roomType[x][z]);
						}
					}
				}

				private class Marker {

					private final Random rand;
					private final int[][] roomType;
					private final int[][] maze;
					Queue<int[]> rooms = new ArrayDeque<>();
					Queue<int[]> hallways = new ArrayDeque<>();

					private Marker(Random rand, int[][] roomType, int[][] maze) {
						this.rand = rand;
						this.roomType = roomType;
						this.maze = maze;
					}

					private void mark() {
						for (int x = 0; x < r2; x++) {
							for (int z = 0; z < r2; z++) {
								int cell = maze[x][z];
								int flag = CellInterpreter.getCellFlags(cell);
								if (flag != 3) {
									int ans = roomType[x][z] = CellInterpreter.getRoomMarker(cell, flag);
									if (ans == CellInterpreter.ROOM) rooms.add(new int[]{x, z});
									if (ans == CellInterpreter.HALLWAY) hallways.add(new int[]{x, z});
								}
							}
						}
						while (!rooms.isEmpty() || !hallways.isEmpty()) {
							while (!rooms.isEmpty()) {
								var r = rooms.poll();
								int x = r[0];
								int z = r[1];
								int cell = maze[x][z];
								if ((cell & 1) != 0) markHallway(x - 1, z);
								if ((cell & 2) != 0) markHallway(x + 1, z);
								if ((cell & 4) != 0) markHallway(x, z - 1);
								if ((cell & 8) != 0) markHallway(x, z + 1);
							}
							if (!hallways.isEmpty()) {
								var r = hallways.poll();
								int x = r[0];
								int z = r[1];
								int cell = maze[x][z];
								if ((cell & 1) != 0) markRoom(x - 1, z);
								if ((cell & 2) != 0) markRoom(x + 1, z);
								if ((cell & 4) != 0) markRoom(x, z - 1);
								if ((cell & 8) != 0) markRoom(x, z + 1);
							}
						}
					}

					private void markHallway(int x, int z) {
						if (x < 0 || x >= r1 || z < 0 || z >= r1) return;
						if (roomType[x][z] != 0) return;
						roomType[x][z] = CellInterpreter.HALLWAY;
						hallways.add(new int[]{x, z});
					}

					private void markRoom(int x, int z) {
						if (x < 0 || x >= r1 || z < 0 || z >= r1) return;
						if (roomType[x][z] != 0) return;
						if (rand.nextFloat() < CellInterpreter.getRoomChance(maze[x][z])) {
							roomType[x][z] = CellInterpreter.ROOM;
							rooms.add(new int[]{x, z});
						} else {
							roomType[x][z] = CellInterpreter.HALLWAY;
							hallways.add(new int[]{x, z});
						}
					}

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
			private final int[] bossRoom = new int[32];
			private final long stairSeed;

			private boolean checked = false;

			public MazeColumn(long seed, int cx, int cz) {
				this.cx = cx;
				this.cz = cz;
				var rand = new Random(seed);
				stairSeed = rand.nextLong();
				int[] spaces = new int[6];
				Arrays.fill(spaces, 2);
				for (int i = 0; i < 5; i++) {
					int layer = rand.nextInt(6);
					spaces[layer]++;
				}
				int layer = 0;
				for (int i = 0; i < 5; i++) {
					int e = spaces[i];
					layer += e;
					for (int j = 0; j < 3; j++)
						bossRoom[layer + j] = j + 1;
					layer += 3;
				}
			}

			public void check() {
				if (checked) return;
				checked = true;
				var regions = new TopRegion.SubRegion[y1];
				for (int i = 0; i < y1; i++) {
					regions[i] = getRegion(i).getRegion(cx, cz);
				}
				fillStairs(regions);
			}

			private void prefill(int[][] marker, int[][] maze) {
				for (int dx = 0; dx < r1; dx++) {
					for (int dz = 0; dz < r1; dz++) {
						int cell = maze[dx][dz];
						if (cell == 1 && x > 0) marker[dx - 1][dz] = 1;
						if (cell == 2 && x < r1 - 1) marker[dx + 1][dz] = 1;
						if (cell == 4 && z > 0) marker[dx][dz - 1] = 1;
						if (cell == 8 && z < r1 - 1) marker[dx][dz + 1] = 1;
					}
				}
			}

			private void fillLayer(int[][] prevMarker, int[][] marker, int[][] low, int[][] maze, int m, int y, Random rand) {
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
						if (stairs) chance = 0.3f + 0.7f * (y + 1) / y1;
						else if (cross) chance = 0.2f + 0.3f * (y + 1) / y1;
						if (chance <= 0) continue;
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
			}

			private void fillStairs(TopRegion.SubRegion[] regions) {
				var rand = new Random(stairSeed);
				int[][] prevMarker = new int[r1][r1];
				prefill(prevMarker, regions[0].maze);
				int m = 2;
				for (int y = 1; y < y1; y++) {
					var low = regions[y - 1].maze;
					var maze = regions[y].maze;
					int[][] marker = new int[r1][r1];
					prefill(marker, maze);
					if (bossRoom[y] != 2)
						fillLayer(prevMarker, marker, low, maze, m, y, rand);
					prevMarker = marker;
				}
			}

		}
	}

}
