package dev.xkmc.dungeon_infinity.init.reg;

import com.tterrag.registrate.util.entry.BlockEntry;
import dev.xkmc.dungeon_infinity.content.maze.block.MazeWallBlock;
import dev.xkmc.dungeon_infinity.init.DungeonInfinity;
import dev.xkmc.l2core.init.reg.registrate.SimpleEntry;
import dev.xkmc.l2modularblock.core.DelegateBlock;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

public class DIItems {

	public static final SimpleEntry<CreativeModeTab> TAB = DungeonInfinity.REGISTRATE.buildL2CreativeTab(
			"dungeon_infinity", "Dungeon Infinity",
			b -> b.icon(DIItems::getIcon));

	public static final BlockEntry<DelegateBlock> MAZESTONE;

	static {
		MAZESTONE = DungeonInfinity.REGISTRATE.block("mazestone", p ->
						DelegateBlock.newBaseBlock(p, MazeWallBlock.ALL_DIRE_STATE, MazeWallBlock.NEIGHBOR, MazeWallBlock.DROP))
				.initialProperties(() -> Blocks.OBSIDIAN)
				.blockstate(() -> MazeWallBlock.Model::buildModel)
				.item().model(() -> MazeWallBlock.Model::buildItem).build()
				.register();


	}

	public static ItemStack getIcon() {
		return MAZESTONE.asStack();
	}

	public static void register() {

	}

}
