package dev.xkmc.dungeon_infinity.init.reg;

import dev.xkmc.dungeon_infinity.content.maze.cap.MazeHistory;
import dev.xkmc.dungeon_infinity.content.maze.cap.MazeLevelData;
import dev.xkmc.dungeon_infinity.content.maze.cap.MazeRoomData;
import dev.xkmc.dungeon_infinity.init.DungeonInfinity;
import dev.xkmc.dungeon_infinity.init.data.DIDimensionGen;
import dev.xkmc.l2core.capability.player.PlayerCapabilityNetworkHandler;
import dev.xkmc.l2core.init.reg.simple.AttReg;
import dev.xkmc.l2core.init.reg.simple.AttVal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

public class DIMeta {

	private static final AttReg ATT = AttReg.of(DungeonInfinity.REG);

	public static final AttVal.PlayerVal<MazeHistory> HISTORY = ATT.player("maze_history", MazeHistory.class, MazeHistory::new, PlayerCapabilityNetworkHandler::new);

	public static final AttVal.CapVal<LevelChunk, MazeRoomData> ROOM = ATT.entity("room", MazeRoomData.class, MazeRoomData::new, LevelChunk.class,
			e -> e.getLevel().dimension().identifier().equals(DIDimensionGen.LEVEL_MAZE.identifier()));

	public static final AttVal.CapVal<Level, MazeLevelData> ACTIVE = ATT.entity("maze_level_data", MazeLevelData.class, MazeLevelData::new, Level.class,
			e -> e.dimension().identifier().equals(DIDimensionGen.LEVEL_MAZE.identifier()));

	public static void register() {
	}

}
