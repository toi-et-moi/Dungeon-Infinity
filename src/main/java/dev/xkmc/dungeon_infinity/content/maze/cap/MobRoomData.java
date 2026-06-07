package dev.xkmc.dungeon_infinity.content.maze.cap;

import dev.xkmc.dungeon_infinity.init.reg.DIMeta;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

import java.util.*;

@SerialClass
public class MobRoomData {

	private final Set<ServerPlayer> players = new LinkedHashSet<>();
	private final List<LivingEntity> mobs = new LinkedList<>();

	@SerialField
	public final Set<UUID> playerIds = new LinkedHashSet<>();
	@SerialField
	public final Set<UUID> mobIds = new LinkedHashSet<>();
	@SerialField
	public boolean started = false;
	@SerialField
	public long lastTick = -1;

	public boolean isDefeated() {
		return started && mobs.isEmpty();
	}

	public void track(ServerPlayer sp) {
		players.add(sp);
		playerIds.add(sp.getUUID());
	}

	private void sanitize(ServerLevel level, MobRoomIns ins) {
		players.clear();
		for (var id : playerIds) {
			var p = level.getPlayerByUUID(id);
			if (!(p instanceof ServerPlayer sp)) continue;
			if (p.isSpectator()) continue;
			if (sp.level() != level || !sp.isAlive()) continue;
			if (!ins.contains(sp)) continue;
			players.add(sp);
		}
		playerIds.clear();
		for (var e : players) {
			playerIds.add(e.getUUID());
		}

		mobs.clear();
		for (var id : mobIds) {
			var e = level.getEntity(id);
			if (!(e instanceof LivingEntity le)) continue;
			if (le.level() != level || !le.isAlive()) continue;
			if (!ins.contains(le)) continue;
			mobs.add(le);
		}
		mobIds.clear();
		for (var e : mobs) {
			mobIds.add(e.getUUID());
		}
	}

	public boolean mayStart() {
		for (var e : players) {
			if (!e.isCreative()) return true;
		}
		return false;
	}

	private void spawn(ServerLevel level, MobRoomIns ins) {
		//TODO
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

	public void tick(MobRoomIns ins) {
		var level = ins.holder.level();
		var time = level.getGameTime();
		if (time <= lastTick) return;
		lastTick = time;
		sanitize(level, ins);
		if (!started && mayStart()) {
			started = true;
			ins.setWall(true);
			spawn(level, ins);
		}
		if (isDefeated()) {
			ins.setWall(false);
			for (var e : players) {
				var data = DIMeta.HISTORY.type().getOrCreate(e);
				for (var r : ins.list) {
					var mp = MazePos.map(r.getBlockPos());
					data.getOrCreate(mp).defeat(mp);
				}
			}
			for (var r : ins.list) {
				r.ins = null;
				r.data = null;
			}
		}
	}

}
