package dev.xkmc.dungeon_infinity.init.reg;

import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import dev.xkmc.dungeon_infinity.content.maze.block.ForceFieldBlock;
import dev.xkmc.dungeon_infinity.content.maze.block.MazeWallBlock;
import dev.xkmc.dungeon_infinity.content.maze.map.MazeMapItem;
import dev.xkmc.dungeon_infinity.init.DungeonInfinity;
import dev.xkmc.l2core.init.reg.registrate.SimpleEntry;
import dev.xkmc.l2core.init.reg.simple.DCReg;
import dev.xkmc.l2core.init.reg.simple.DCVal;
import dev.xkmc.l2modularblock.core.DelegateBlock;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

public class DIItems {

	public static final SimpleEntry<CreativeModeTab> TAB = DungeonInfinity.REGISTRATE.buildL2CreativeTab(
			"dungeon_infinity", "Dungeon Infinity",
			b -> b.icon(DIItems::getIcon));

	public static final BlockEntry<DelegateBlock> MAZESTONE;

	public static final BlockEntry<ForceFieldBlock> FORCEFIELD;
	public static final BlockEntry<Block> FORCEFIELD_BLOCK, BROKEN_FORCEFIELD;

	public static final ItemEntry<MazeMapItem> MAP;

	public static final DCReg DC = DCReg.of(DungeonInfinity.REG);
	public static final DCVal<Long> SEED = DC.longVal("seed");

	static {
		MAZESTONE = DungeonInfinity.REGISTRATE.block("mazestone", p ->
						DelegateBlock.newBaseBlock(p, MazeWallBlock.ALL_DIRE_STATE, MazeWallBlock.NEIGHBOR, MazeWallBlock.DROP))
				.properties(p -> p.mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM)
						.strength(-1.0F, 3600000))
				.blockstate(() -> MazeWallBlock.Model::buildModel)
				.item().model(() -> MazeWallBlock.Model::buildItem).build()
				.tag(BlockTags.DRAGON_IMMUNE, BlockTags.WITHER_IMMUNE)
				.register();

		FORCEFIELD = DungeonInfinity.REGISTRATE.block("forcefield", ForceFieldBlock::new)
				.properties(p -> p.mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM)
						.strength(-1.0F, 3600000).noLootTable().forceSolidOff().noOcclusion())
				.blockstate(() -> ForceFieldBlock.Model::buildModel)
				.item().model(() -> ForceFieldBlock.Model::buildItem).build()
				.tag(BlockTags.DRAGON_IMMUNE, BlockTags.WITHER_IMMUNE)
				.register();

		FORCEFIELD_BLOCK = DungeonInfinity.REGISTRATE.block("forcefield_block", Block::new)
				.properties(p -> p.mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM)
						.strength(-1.0F, 3600000).noLootTable())
				.defaultBlockstate().simpleItem()
				.tag(BlockTags.DRAGON_IMMUNE, BlockTags.WITHER_IMMUNE)
				.register();

		BROKEN_FORCEFIELD = DungeonInfinity.REGISTRATE.block("broken_forcefield", Block::new)
				.properties(p -> p.mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM)
						.strength(1, 1).noLootTable())
				.defaultBlockstate().simpleItem()
				.register();


		MAP = DungeonInfinity.REGISTRATE.item("maze_map", MazeMapItem::new)
				.defaultModel().register();

	}

	public static ItemStack getIcon() {
		return MAZESTONE.asStack();
	}

	public static void register() {

	}

}
