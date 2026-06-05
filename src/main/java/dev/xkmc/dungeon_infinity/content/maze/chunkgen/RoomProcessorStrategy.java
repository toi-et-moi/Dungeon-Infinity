package dev.xkmc.dungeon_infinity.content.maze.chunkgen;

import java.util.*;

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

	public class StairGen {

		private final int r1, y1;

		public StairGen(int r1, int y1) {
			this.r1 = r1;
			this.y1 = y1;
		}

		private void prefill(int[][] marker, int[][] maze) {
			for (int dx = 0; dx < r1; dx++) {
				for (int dz = 0; dz < r1; dz++) {
					int cell = maze[dx][dz];
					if (cell == 1 && dx > 0) marker[dx - 1][dz] = 1;
					if (cell == 2 && dx < r1 - 1) marker[dx + 1][dz] = 1;
					if (cell == 4 && dz > 0) marker[dx][dz - 1] = 1;
					if (cell == 8 && dz < r1 - 1) marker[dx][dz + 1] = 1;
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

		public void fillStairs(MazeDimHolder.RegionStack.TopRegion.SubRegion[] regions, long stairSeed, int[] bossRoom) {
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

	public class Scanner {

		private final int r1;
		private final int[][] maze;
		private final int[][] roomType;

		public Scanner(int r1, int[][] maze, int[][] roomType) {
			this.r1 = r1;
			this.maze = maze;
			this.roomType = roomType;
		}

		public void scan(Random rand) {
			List<int[]> candidates = new ArrayList<>();
			for (int dx = 0; dx < r1 - 1; dx++) {
				for (int dz = 0; dz < r1 - 1; dz++) {
					if (maze[dx][dz] >= 16) continue;
					if (maze[dx][dz + 1] >= 16) continue;
					if (maze[dx + 1][dz] >= 16) continue;
					if (maze[dx + 1][dz + 1] >= 16) continue;
					int count = 0;
					if ((maze[dx][dz] & 1) != 0) count++;
					if ((maze[dx][dz + 1] & 1) != 0) count++;
					if ((maze[dx + 1][dz] & 2) != 0) count++;
					if ((maze[dx + 1][dz + 1] & 2) != 0) count++;
					if ((maze[dx][dz] & 4) != 0) count++;
					if ((maze[dx + 1][dz] & 4) != 0) count++;
					if ((maze[dx][dz + 1] & 8) != 0) count++;
					if ((maze[dx + 1][dz + 1] & 8) != 0) count++;
					if (count == 1) candidates.add(new int[]{dx, dz});
				}
			}
			int max = getMaxQuadRoom(candidates.size());
			for (int i = 0; i < max; i++) {
				var e = candidates.remove(rand.nextInt(candidates.size()));
				int dx = e[0];
				int dz = e[1];
				maze[dx][dz] |= 10 | CellInterpreter.setQuadRoom(0, 0);
				maze[dx + 1][dz] |= 9 | CellInterpreter.setQuadRoom(1, 0);
				maze[dx][dz + 1] |= 6 | CellInterpreter.setQuadRoom(0, 1);
				maze[dx + 1][dz + 1] |= 5 | CellInterpreter.setQuadRoom(1, 1);
			}
			for (var e : candidates) {
				int dx = e[0];
				int dz = e[1];
				for (int ddx = 0; ddx <= 1; ddx++) {
					for (int ddz = 0; ddz <= 1; ddz++) {
						int cell = maze[dx + ddx][dz + ddz];
						roomType[dx + ddx][dz + ddz] = CellInterpreter.getTemplateType(cell) == 1 ?
								CellInterpreter.ROOM : CellInterpreter.HALLWAY;
					}
				}

			}
		}

	}

	public class Marker {

		private final int r1;
		private final Random rand;
		private final int[][] roomType;
		private final int[][] maze;
		Queue<int[]> rooms = new ArrayDeque<>();
		Queue<int[]> largeRooms = new ArrayDeque<>();
		Queue<int[]> hallways = new ArrayDeque<>();

		public Marker(int r1, Random rand, int[][] roomType, int[][] maze) {
			this.r1 = r1;
			this.rand = rand;
			this.roomType = roomType;
			this.maze = maze;
		}

		private void prefill(int x, int z) {
			if (x < 0 || z < 0 || x >= r1 || z >= r1) return;
			if (roomType[x][z] != 0) return;
			roomType[x][z] = CellInterpreter.HALLWAY;
		}

		public void mark() {
			for (int x = 0; x < r1; x++) {
				for (int z = 0; z < r1; z++) {
					if (maze[x][z] >= 64) {
						roomType[x][z] = CellInterpreter.SPECIAL;
					} else if (roomType[x][z] == 0) {
						int cell = maze[x][z];
						if (CellInterpreter.getTemplateType(cell) == 1) {
							roomType[x][z] = CellInterpreter.ROOM + 1;
						} else {
							int flag = CellInterpreter.getCellFlags(cell);
							if (flag != 3) {
								roomType[x][z] = CellInterpreter.getRoomMarker(cell, flag);
							}
						}
					}
				}
			}
			for (int x = 0; x < r1; x++) {
				for (int z = 0; z < r1; z++) {
					if (roomType[x][z] == CellInterpreter.SPECIAL) {
						int cell = maze[x][z];
						if ((cell & 1) != 0) prefill(x - 1, z);
						if ((cell & 2) != 0) prefill(x + 1, z);
						if ((cell & 4) != 0) prefill(x, z - 1);
						if ((cell & 8) != 0) prefill(x, z + 1);
					}
				}
			}
			for (int x = 0; x < r1; x++) {
				for (int z = 0; z < r1; z++) {
					int ans = roomType[x][z];
					if (ans >= CellInterpreter.HALLWAY) {
						if (ans > CellInterpreter.ROOM) largeRooms.add(new int[]{x, z});
						if (ans == CellInterpreter.ROOM) rooms.add(new int[]{x, z});
						if (ans == CellInterpreter.HALLWAY) hallways.add(new int[]{x, z});
					}
				}
			}
			while (!rooms.isEmpty() || !largeRooms.isEmpty() || !hallways.isEmpty()) {
				while (!rooms.isEmpty()) {
					expand(rooms.poll());
				}
				if (!largeRooms.isEmpty()) {
					expand(largeRooms.poll());
				} else if (!hallways.isEmpty()) {
					expand(hallways.poll());
				}
			}
		}

		private void expand(int[] r) {
			int x = r[0];
			int z = r[1];
			int cell = maze[x][z];
			int room = roomType[x][z];
			if ((cell & 1) != 0) fromRoom(x - 1, z, room);
			if ((cell & 2) != 0) fromRoom(x + 1, z, room);
			if ((cell & 4) != 0) fromRoom(x, z - 1, room);
			if ((cell & 8) != 0) fromRoom(x, z + 1, room);
		}

		private void fromRoom(int x, int z, int src) {
			if (x < 0 || x >= r1 || z < 0 || z >= r1) return;
			if (roomType[x][z] != 0) return;
			if (src > CellInterpreter.ROOM && rand.nextFloat() < getEndLargeRoomChance()) {
				roomType[x][z] = src - 1;
				rooms.add(new int[]{x, z});
			} else if (src >= CellInterpreter.ROOM) {
				roomType[x][z] = CellInterpreter.HALLWAY;
				hallways.add(new int[]{x, z});
			} else if (rand.nextFloat() < getRoomChance(maze[x][z])) {
				if (rand.nextFloat() < getHallLargeRoomChance()) {
					roomType[x][z] = CellInterpreter.ROOM + 1;
					largeRooms.add(new int[]{x, z});
				} else {
					roomType[x][z] = CellInterpreter.ROOM;
					rooms.add(new int[]{x, z});
				}
			} else {
				roomType[x][z] = CellInterpreter.HALLWAY;
				hallways.add(new int[]{x, z});
			}
		}

	}

}

