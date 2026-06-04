package dev.xkmc.dungeon_infinity.content.maze.map;

import com.mojang.blaze3d.platform.NativeImage;
import dev.xkmc.dungeon_infinity.content.maze.chunkgen.MazeDimHolder;
import dev.xkmc.dungeon_infinity.init.DungeonInfinity;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.Identifier;

public class MazeMapTextureManager implements AutoCloseable {

	public static MazeMapTextureManager get() {
		return ((MapTextureManagerProvider) (Minecraft.getInstance().getMapTextureManager())).dungeon_infinity$getMazeData();
	}

	private final Long2ObjectMap<MazeLevelMapSet> dims = new Long2ObjectOpenHashMap<>();

	public MapTextureData getDetail(long seed, int x, int y, int z) {
		var dim = dims.computeIfAbsent(seed, MazeLevelMapSet::new);
		return dim.getDetail(x, y, z);
	}

	@Override
	public void close() {
		for (var e : dims.values())
			e.close();
		dims.clear();
	}

	public static class MazeLevelMapSet implements AutoCloseable {

		private final MazeDimHolder dim;
		private final Long2ObjectMap<MapTextureData> detail = new Long2ObjectOpenHashMap<>();

		public MazeLevelMapSet(long seed) {
			this.dim = new MazeDimHolder(seed);
		}

		public MapTextureData getDetail(int x, int y, int z) {
			var id = BlockPos.asLong(x, y, z);
			return detail.computeIfAbsent(id, _ -> new MapTextureData(dim, new Vec3i(x, y, z)));
		}

		@Override
		public void close() {
			for (var e : detail.values())
				e.close();
			detail.clear();
		}

	}

	public static class MapTextureData implements AutoCloseable {

		private final MazeDimHolder dim;
		private final DynamicTexture texture;
		private final Vec3i pos;

		public final Identifier id;

		public int w, h;
		public int[][] data;

		public MapTextureData(MazeDimHolder dim, Vec3i pos) {
			this.dim = dim;
			this.pos = pos;
			w = 25;
			h = 25;
			data = new int[128][128];
			this.texture = new DynamicTexture(() -> "Maze Map " + pos, 128, 128, true);
			this.id = DungeonInfinity.loc("maze_map/" + Long.toUnsignedString(new BlockPos(pos).asLong(), 16));
			Minecraft.getInstance().getTextureManager().register(id, texture);
			fill();
		}

		public void fill() {
			NativeImage pixels = this.texture.getPixels();
			int[][] maze = dim.getRegion(pos.getX(), pos.getY(), pos.getZ());
			for (int x = 0; x < 25; x++) {
				for (int z = 0; z < 25; z++) {
					int cell = maze[x][z];
					int[][] px = MazeMapPixelMapper.getPixels(cell);
					for (int ix = 0; ix < 5; ix++) {
						System.arraycopy(px[ix], 0, data[x * 5 + ix], z * 5, 5);
					}
				}
			}

			data[127][127] = 0xafff00ff;

			for (int y = 0; y < 128; y++) {
				for (int x = 0; x < 128; x++) {
					pixels.setPixel(x, y, data[x][y]);
				}
			}

			this.texture.upload();
		}

		@Override
		public void close() {
			texture.close();
		}
	}

}
