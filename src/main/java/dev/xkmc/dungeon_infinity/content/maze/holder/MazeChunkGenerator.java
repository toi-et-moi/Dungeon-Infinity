package dev.xkmc.dungeon_infinity.content.maze.holder;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.xkmc.dungeon_infinity.init.DungeonInfinity;
import dev.xkmc.l2serial.util.LazyFunction;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;

import java.util.concurrent.CompletableFuture;

public class MazeChunkGenerator extends EmptyChunkGenerator {

	private static final Identifier ID = DungeonInfinity.loc("maze_chunkgen");

	public static final MapCodec<MazeChunkGenerator> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
			Biome.CODEC.fieldOf("biome").forGetter(x -> x.biome)
	).apply(i, MazeChunkGenerator::new));

	private final Holder<Biome> biome;
	private final LazyFunction<Long, MazeDimHolder> maze;
	private final ChunkFiller filler = new ChunkFiller();

	public MazeChunkGenerator(Holder<Biome> biome) {
		super(new FixedBiomeSource(biome));
		this.biome = biome;
		maze = LazyFunction.create(MazeDimHolder::new);
	}

	@Override
	public int getGenDepth() {
		return 256;
	}

	@Override
	protected MapCodec<MazeChunkGenerator> codec() {
		return CODEC;
	}

	@Override
	public CompletableFuture<ChunkAccess> fillFromNoise(Blender blender, RandomState random, StructureManager structures, ChunkAccess access) {
		return CompletableFuture.supplyAsync(() -> {
			var maze = this.maze.get(random.getOrCreateRandomFactory(ID).at(0, 0, 0).nextLong());
			ChunkPos pos = access.getPos();
			filler.fillChunk(maze, pos, access, random);
			return access;
		}, Util.backgroundExecutor());
	}

	@Override
	public int getBaseHeight(int x, int z, Heightmap.Types type, LevelHeightAccessor height, RandomState random) {
		return getGenDepth();
	}

	@Override
	public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor height, RandomState random) {
		var state = Blocks.STONE.defaultBlockState();
		BlockState[] states = new BlockState[height.getHeight()];
		for (int i = 0; i < height.getHeight(); i++) {
			states[i] = state;
		}
		return new NoiseColumn(height.getMinY(), states);
	}

}
