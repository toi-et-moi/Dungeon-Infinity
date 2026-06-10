package dev.xkmc.dungeon_infinity.content.cap;

import dev.xkmc.dungeon_infinity.content.chunkgen.MazeChunkGenerator;
import dev.xkmc.dungeon_infinity.init.DungeonInfinity;
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

	@SerialField
	public int radius = 1;

	public boolean inMazeDim(Player player) {
		return player.level().dimension().identifier().equals(DIDimensionGen.LEVEL_MAZE.identifier());
	}

	public void teleportToRoom(ServerPlayer sp, BlockPos fallback) {
		if (activeMobRoom == null) activeMobRoom = fallback;
		var vec = activeMobRoom.getBottomCenter();
		sp.teleportTo(vec.x, vec.y, vec.z);
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
					teleportToRoom(sp, activeMobRoom);
				else activeMobRoom = null;
			} else activeMobRoom = null;
		}
		var pos = MazePos.map(player.blockPosition());
		var ent = data.computeIfAbsent(pos.key(), k -> new Visit());
		if (player instanceof ServerPlayer sp) {
			if (sp.level().getChunkSource().getGenerator() instanceof MazeChunkGenerator maze) {
				var dim = maze.getMaze(sp.level().getChunkSource().randomState());
				int rad = dim.getVisibility(pos) / 2 + 1;
				if (rad != radius) {
					DungeonInfinity.HANDLER.toClientPlayer(new SetRadiusPacket(rad), sp);
				}
				radius = rad;
			}
		}
		ent.visit(pos, radius);
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

	public void setRadius(int rad) {
		this.radius = rad;
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

		public void visit(MazePos pos, int m) {
			int x = pos.px() >> 4;
			int z = pos.pz() >> 4;
			int index = x * R + z;
			int i = index >> 3;
			int j = 1 << (index & 7);
			if ((visitedGrid[i] & j) != 0) return;
			visitedGrid[i] |= j;
			visited++;
			int old = revision;
			markVisible(x - m, z - m, m * 2 + 1, m * 2 + 1);
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
