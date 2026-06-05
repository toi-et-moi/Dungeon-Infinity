package dev.xkmc.dungeon_infinity.content.maze.cap;

import dev.xkmc.dungeon_infinity.content.maze.chunkgen.CellInterpreter;
import dev.xkmc.dungeon_infinity.content.maze.chunkgen.MazeChunkGenerator;
import dev.xkmc.dungeon_infinity.content.maze.chunkgen.MazeDimHolder;
import dev.xkmc.dungeon_infinity.content.maze.chunkgen.RoomProcessorStrategy;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.Nullable;

import java.util.List;

@SerialClass
public class SectionRoom {

	private ServerLevel sl;
	private LevelChunk lc;
	private SectionPos pos;
	private MazeDimHolder dim;
	private int[][] maze;
	private int x, z;

	public void update(ServerLevel sl, LevelChunk lc, SectionPos pos) {
		if (this.sl != sl || this.lc != lc) {
			this.sl = sl;
			this.lc = lc;
			this.pos = pos;
			if (sl.getChunkSource().getGenerator() instanceof MazeChunkGenerator gen) {
				dim = gen.getMaze(sl.getChunkSource().randomState());
			} else throw new IllegalStateException("Illegal Dimension for Section");
			int rx = Math.floorDiv(pos.x(), 25);
			int rz = Math.floorDiv(pos.z(), 25);
			maze = dim.getRegion(rx, pos.y(), rz);
			x = pos.x() - rx;
			z = pos.z() - rz;
		}
	}

	public @Nullable SectionRoom[][][] findRoom() {
		int cell = maze[x][z];
		if (CellInterpreter.isBossRoom(cell)) {
			int data = CellInterpreter.getBossRoom(cell);
			int layer = data / 9;
			int cx = layer % 9 / 3;
			int cz = layer % 3;
			var ans = new SectionRoom[3][2][3];
			for (int ix = 0; ix < 3; ix++) {
				for (int iz = 0; iz < 3; iz++) {
					for (int iy = 0; iy < 2; iy++) {
						ans[ix][iy][iz] = RoomDataHolder.get(sl, pos.offset(ix - cx, iy - layer, iz - cz));
					}
				}
			}
			return ans;
		}
		if (CellInterpreter.isQuadRoom(cell)) {
			int data = CellInterpreter.getQuadRoom(cell);
			int cx = data / 2;
			int cz = data % 2;
			var ans = new SectionRoom[2][1][2];
			for (int ix = 0; ix < 2; ix++) {
				for (int iz = 0; iz < 2; iz++) {
					ans[ix][0][iz] = RoomDataHolder.get(sl, pos.offset(ix - cx, 0, iz - cz));

				}
			}
			return ans;
		}
		if (CellInterpreter.getRoomType(cell) < CellInterpreter.ROOM)
			return new SectionRoom[0][0][0];
		List<int[]> rel = RoomProcessorStrategy.findRooms(maze, x, z);
		int x0 = 25, z0 = 25, x1 = 0, z1 = 0;
		for (var p : rel) {
			x0 = Math.min(x0, p[0]);
			x1 = Math.max(x1, p[0]);
			z0 = Math.min(z0, p[1]);
			z1 = Math.max(z1, p[1]);
		}
		@Nullable SectionRoom[][][] ans = new SectionRoom[x1 - x0 + 1][1][z1 - z0 + 1];
		for (var p : rel) {
			ans[p[0] - x0][0][p[1] - z0] = RoomDataHolder.get(sl, pos.offset(p[0] - x, 0, p[1] - z));
		}
		return ans;
	}

}
