package dev.xkmc.dungeon_infinity.content.maze.cap;

import dev.xkmc.l2core.capability.player.PlayerCapabilityTemplate;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

@SerialClass
public class MazeHistory extends PlayerCapabilityTemplate<MazeHistory> {

	@SerialField
	public final Long2ObjectOpenHashMap<Visit> data = new Long2ObjectOpenHashMap<>();

	@Override
	public void tick(Player player) {
		var pos = player.blockPosition();
		int x = Math.floorDiv(pos.getX(), 16 * 25);
		int z = Math.floorDiv(pos.getZ(), 16 * 25);
		int y = Mth.clamp(pos.getY() / 16, 0, 15);
		var key = BlockPos.asLong(x, y, z);
		var ent = data.computeIfAbsent(key, k -> new Visit());
		int px = pos.getX() - x * 16 * 25;
		int pz = pos.getZ() - z * 16 * 25;
		ent.visit(px / 16, pz / 16);
	}

	public Visit getOrCreate(int x, int y, int z) {
		var key = BlockPos.asLong(x, y, z);
		return data.computeIfAbsent(key, k -> new Visit());
	}

	@SerialClass
	public static class Visit {

		private static final int R = 25, MAX = 80;

		@SerialField
		private final byte[] visibleGrid = new byte[MAX];
		@SerialField
		private final byte[] visitedGrid = new byte[MAX];
		@SerialField
		private int visited = 0, visible = 0;
		@SerialField
		private int revision = 0;

		public int getVer() {
			return revision;
		}

		public boolean isVisited(int x, int z) {
			int index = x * R + z;
			int i = index >> 3;
			int j = 1 << (index & 7);
			return (visitedGrid[i] & j) != 0;
		}

		public boolean isVisible(int x, int z) {
			int index = x * R + z;
			int i = index >> 3;
			int j = 1 << (index & 7);
			return (visibleGrid[i] & j) != 0;
		}

		public void visit(int x, int z) {
			int index = x * R + z;
			int i = index >> 3;
			int j = 1 << (index & 7);
			if ((visitedGrid[i] & j) != 0) return;
			visitedGrid[i] |= j;
			visited++;
			int old = revision;
			markVisible(x - 1, z - 1, 3, 3);
			revision = old + 1;
		}

		public void markVisible(int x, int z, int w, int h) {
			int old = visible;
			for (int dx = 0; dx < w; dx++) {
				for (int dz = 0; dz < h; dz++) {
					int ix = x + dx;
					int iz = z + dz;
					if (ix < 0 || iz < 0 || ix >= R || iz >= R) continue;
					markVisible(ix, iz);
				}
			}
			if (visible != old)
				revision++;
		}

		private void markVisible(int x, int z) {
			int index = x * R + z;
			int i = index >> 3;
			int j = 1 << (index & 7);
			if ((visibleGrid[i] & j) != 0) return;
			visibleGrid[i] |= j;
			visible++;
		}

	}

}
