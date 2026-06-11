package dev.xkmc.dungeon_infinity.content.spawn;

import dev.xkmc.dungeon_infinity.content.cap.MobRoomHolder;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

import java.util.*;

@SerialClass
public class TestSpawnTicker implements MobSpawnTicker {

	private final List<LivingEntity> mobs = new LinkedList<>();

	@SerialField
	public final Set<UUID> mobIds = new LinkedHashSet<>();

	@Override
	public boolean isActive() {
		return !mobs.isEmpty();
	}

	@Override
	public void addTargetPos(BlockPos pos) {

	}

	@Override
	public void start(ServerLevel level, MobRoomHolder ins) {
		for (var r : ins.list) {
			var pos = r.getBlockPos().offset(8, 3, 8);
			var e = EntityType.ZOMBIE.spawn(level, pos, EntitySpawnReason.TRIAL_SPAWNER);
			if (e != null) {
				level.addFreshEntity(e);
				mobs.add(e);
				mobIds.add(e.getUUID());
			}
		}
	}

	@Override
	public void tick(ServerLevel level, MobRoomHolder ins) {
		mobs.clear();
		for (var id : mobIds) {
			var e = level.getEntity(id);
			if (!(e instanceof LivingEntity le)) continue;
			if (le.level() != level || !le.isAlive()) continue;
			if (!ins.contains(le))
				le.snapTo(ins.holder.getBlockPos().offset(8, 3, 8).getCenter());
			mobs.add(le);
		}
		mobIds.clear();
		for (var e : mobs) {
			mobIds.add(e.getUUID());
		}
	}

	@Override
	public void stop(ServerLevel level, MobRoomHolder ins) {
		for (var e : mobs) {
			e.discard();
		}
		mobs.clear();
		mobIds.clear();
	}

}
