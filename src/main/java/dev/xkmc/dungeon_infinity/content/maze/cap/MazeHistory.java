package dev.xkmc.dungeon_infinity.content.maze.cap;

import dev.xkmc.dungeon_infinity.init.data.DIDimensionGen;
import dev.xkmc.l2core.capability.player.PlayerCapabilityTemplate;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.Nullable;

import java.util.Map;

@SerialClass
public class MazeHistory extends PlayerCapabilityTemplate<MazeHistory> {

	@SerialField
	public final Map<Long, Visit> data = new Long2ObjectOpenHashMap<>();

	@SerialField
	public @Nullable BlockPos activeMobRoom = null;

	public boolean inMazeDim(Player player) {
		return player.level().dimension().identifier().equals(DIDimensionGen.LEVEL_MAZE.identifier());
	}

	public void snapToRoom(ServerPlayer sp, BlockPos fallback) {
		if (activeMobRoom == null) activeMobRoom = fallback;
		sp.snapTo(activeMobRoom.getBottomCenter());
	}

	@Override
	public void tick(Player player) {
		if (!inMazeDim(player)) {
			activeMobRoom = null;
			return;
		}
		if (player instanceof ServerPlayer sp && activeMobRoom != null) {
			var sec = MazeRoomData.get(sp.level(), SectionPos.of(activeMobRoom));
			if (sec != null && sec.isActive()) {
				if (sec.getOrCreateActiveMobRoomInstance().contains(sp))
					activeMobRoom = sp.blockPosition();
				else if (!sp.isCreative())
					sp.snapTo(activeMobRoom.getBottomCenter());
				else activeMobRoom = null;
			} else activeMobRoom = null;
		}
		var pos = MazePos.map(player.blockPosition());
		var ent = data.computeIfAbsent(pos.key(), k -> new Visit());
		ent.visit(pos);
		if (player instanceof ServerPlayer sp) {
			var sec = MazeRoomData.get(sp.level(), SectionPos.of(sp.blockPosition()));
			if (sec != null) {
				sec.tick(ent, pos, sp);
				if (sec.isActive()) {
					activeMobRoom = sp.blockPosition();
				}
			}
		}
	}

	public Visit getOrCreate(MazePos pos) {
		return data.computeIfAbsent(pos.key(), k -> new Visit());
	}

	@SerialClass
	public static class Visit {

		private static final int R = 25, MAX = 80;

		@SerialField
		private final byte[] visibleGrid = new byte[MAX];
		@SerialField
		private final byte[] visitedGrid = new byte[MAX];
		@SerialField
		private final byte[] defeatGrid = new byte[MAX];
		@SerialField
		private int visited = 0, visible = 0, defeat = 0;
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

		public void visit(MazePos pos) {
			int x = pos.px() >> 4;
			int z = pos.pz() >> 4;
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

		public void defeat(MazePos pos) {
			int x = pos.px() >> 4;
			int z = pos.pz() >> 4;
			int index = x * R + z;
			int i = index >> 3;
			int j = 1 << (index & 7);
			if ((defeatGrid[i] & j) != 0) return;
			defeatGrid[i] |= j;
			defeat++;
			revision++;
		}

		public boolean isDefeated(MazePos pos) {
			int x = pos.px() >> 4;
			int z = pos.pz() >> 4;
			int index = x * R + z;
			int i = index >> 3;
			int j = 1 << (index & 7);
			return (defeatGrid[i] & j) != 0;
		}

		public boolean isDefeated(int x, int z) {
			int index = x * R + z;
			int i = index >> 3;
			int j = 1 << (index & 7);
			return (defeatGrid[i] & j) != 0;
		}
	}

}
