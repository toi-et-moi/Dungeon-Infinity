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

public class MazeMapTextureManager {

	private final MazeDimHolder dim;

	private Long2ObjectMap<MapTextureData> map = new Long2ObjectOpenHashMap<>();

	public MazeMapTextureManager(long seed) {
		this.dim = new MazeDimHolder(seed);
	}

	public MapTextureData get(int x, int y, int z) {
		var id = BlockPos.asLong(x, y, z);
		return map.computeIfAbsent(id, e -> new MapTextureData(new Vec3i(x, y, z)));
	}

	public class MapTextureData implements AutoCloseable {

		private final DynamicTexture texture;
		private final Identifier id;
		private final Vec3i pos;

		public int w, h;
		public int[][] data;

		public MapTextureData(Vec3i pos) {
			this.pos = pos;
			w = 25;
			h = 25;
			data = new int[128][128];
			this.texture = new DynamicTexture(() -> "Maze Map " + pos, 128, 128, true);
			this.id = DungeonInfinity.loc("maze_map/" + Long.toUnsignedString(new BlockPos(pos).asLong(), 16));
			Minecraft.getInstance().getTextureManager().register(id, texture);
			fill();
		}


		private void fill() {
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
