package dev.xkmc.dungeon_infinity.init.reg;

import dev.xkmc.dungeon_infinity.content.maze.cap.MazeHistory;
import dev.xkmc.dungeon_infinity.content.maze.cap.RoomDataHolder;
import dev.xkmc.dungeon_infinity.init.DungeonInfinity;
import dev.xkmc.dungeon_infinity.init.data.DIDimensionGen;
import dev.xkmc.l2core.capability.player.PlayerCapabilityNetworkHandler;
import dev.xkmc.l2core.init.reg.simple.AttReg;
import dev.xkmc.l2core.init.reg.simple.AttVal;
import net.minecraft.world.level.chunk.LevelChunk;

public class DIMeta {

	private static final AttReg ATT = AttReg.of(DungeonInfinity.REG);

	public static final AttVal.PlayerVal<MazeHistory> HISTORY = ATT.player("maze_history", MazeHistory.class, MazeHistory::new, PlayerCapabilityNetworkHandler::new);

	public static final AttVal.CapVal<LevelChunk, RoomDataHolder> ROOM = ATT.entity("room", RoomDataHolder.class, RoomDataHolder::new, LevelChunk.class,
			e -> e.getLevel().dimension().identifier().equals(DIDimensionGen.LEVEL_MAZE.identifier()));

	public static void register() {
	}

}
