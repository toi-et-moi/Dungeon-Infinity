package dev.xkmc.dungeon_infinity.content.maze.cap;

import dev.xkmc.dungeon_infinity.init.reg.DIMeta;
import dev.xkmc.l2core.capability.attachment.GeneralCapabilityTemplate;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.jspecify.annotations.Nullable;

@SerialClass
public class MazeRoomData extends GeneralCapabilityTemplate<LevelChunk, MazeRoomData> {

	@Nullable
	public static SectionRoom get(ServerLevel sl, SectionPos pos) {
		if (pos.y() < 0 || pos.y() >= 16) return null;
		var chunk = sl.getChunk(pos.x(), pos.z(), ChunkStatus.FULL, false);
		if (!(chunk instanceof LevelChunk lc)) return null;
		if (!DIMeta.ROOM.type().isProper(lc)) return null;
		var cap = DIMeta.ROOM.type().getOrCreate(lc);
		var ans = cap.sections[pos.y()];
		if (ans == null) {
			ans = cap.sections[pos.y()] = new SectionRoom();
		}
		ans.update(sl, lc, pos);
		return ans;
	}

	@SerialField
	public final @Nullable SectionRoom[] sections = new SectionRoom[16];

}
