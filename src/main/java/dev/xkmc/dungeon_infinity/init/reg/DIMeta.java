package dev.xkmc.dungeon_infinity.init.reg;

import dev.xkmc.dungeon_infinity.content.maze.cap.MazeHistory;
import dev.xkmc.dungeon_infinity.init.DungeonInfinity;
import dev.xkmc.l2core.capability.player.PlayerCapabilityNetworkHandler;
import dev.xkmc.l2core.init.reg.simple.AttReg;
import dev.xkmc.l2core.init.reg.simple.AttVal;

public class DIMeta {

	private static final AttReg ATT = AttReg.of(DungeonInfinity.REG);

	public static final AttVal.PlayerVal<MazeHistory> HISTORY = ATT.player("maze_history", MazeHistory.class, MazeHistory::new, PlayerCapabilityNetworkHandler::new);

	public static void register() {
	}

}
