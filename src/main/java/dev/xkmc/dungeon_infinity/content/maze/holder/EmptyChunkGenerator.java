package dev.xkmc.dungeon_infinity.content.maze.holder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.RandomState;

import java.util.List;
import java.util.function.Function;

public abstract class EmptyChunkGenerator extends ChunkGenerator {

	public EmptyChunkGenerator(BiomeSource biomeSource) {
		super(biomeSource);
	}

	public EmptyChunkGenerator(BiomeSource biomeSource, Function<Holder<Biome>, BiomeGenerationSettings> generationSettingsGetter) {
		super(biomeSource, generationSettingsGetter);
	}

	@Override
	public void applyCarvers(WorldGenRegion level, long seed, RandomState random, BiomeManager biomeManager, StructureManager structureManager, ChunkAccess chunkAccess) {

	}

	@Override
	public void buildSurface(WorldGenRegion level, StructureManager structureManager, RandomState random, ChunkAccess chunk) {

	}

	@Override
	public void spawnOriginalMobs(WorldGenRegion level) {

	}

	@Override
	public int getSeaLevel() {
		return 0;
	}

	@Override
	public int getMinY() {
		return 0;
	}

	@Override
	public void addDebugScreenInfo(List<String> info, RandomState random, BlockPos pos) {

	}

}
