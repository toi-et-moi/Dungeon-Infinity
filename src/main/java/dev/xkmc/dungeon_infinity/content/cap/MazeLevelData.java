package dev.xkmc.dungeon_infinity.content.cap;

import dev.xkmc.l2core.capability.attachment.GeneralCapabilityTemplate;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.util.LinkedHashSet;
import java.util.Set;

@SerialClass
public class MazeLevelData extends GeneralCapabilityTemplate<Level, MazeLevelData> {

	@SerialField
	public final Set<BlockPos> activeRooms = new LinkedHashSet<>();

	@Override
	public void tick(Level level) {
		if (!(level instanceof ServerLevel sl)) return;
		activeRooms.removeIf(pos -> {
			var sec = MazeRoomData.get(sl, SectionPos.of(pos));
			if (sec == null) return false;
			if (!sec.isActive()) return true;
			var ins = sec.getOrCreateActiveMobRoomInstance();
			if (!ins.data.started) return true;
			ins.data.tick(ins);
			return !ins.data.started;
		});
	}

}
