package dev.xkmc.dungeon_infinity.content.spawn;

import dev.xkmc.dungeon_infinity.content.cap.MobRoomHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

public interface MobSpawnTicker {

	void stop(ServerLevel level, MobRoomHolder ins);

	boolean isActive();

	void tick(ServerLevel level, MobRoomHolder ins);

	void start(ServerLevel level, MobRoomHolder ins);

	void addTargetPos(BlockPos pos);

}
