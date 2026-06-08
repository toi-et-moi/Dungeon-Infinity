package dev.xkmc.dungeon_infinity.init.data;

import com.tterrag.registrate.providers.RegistrateTagsProvider;
import dev.xkmc.dungeon_infinity.init.DungeonInfinity;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class DITagGen {

	public static final TagKey<Block> MAZE_STONE_AFFINITIVE = block("maze_stone_affinitive");
	public static final TagKey<Block> FORCEFIELD_CANNOT_REPLACE = block("forcefield_cannot_replace");


	private static TagKey<Block> block(String id) {
		return BlockTags.create(DungeonInfinity.loc(id));
	}

	public static void genBlockTags(RegistrateTagsProvider.IntrinsicImpl<Block> pvd) {
		pvd.tag(MAZE_STONE_AFFINITIVE).add(Blocks.BEDROCK, Blocks.BARRIER, Blocks.STRUCTURE_VOID);
		pvd.tag(FORCEFIELD_CANNOT_REPLACE).add(Blocks.BEDROCK, Blocks.BARRIER, Blocks.COMMAND_BLOCK, Blocks.CHAIN_COMMAND_BLOCK, Blocks.REPEATING_COMMAND_BLOCK);
	}
}
