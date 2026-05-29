package dev.xkmc.dungeon_infinity.init.data;

import dev.xkmc.l2core.serial.config.ConfigDataProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceKey;

import java.util.concurrent.CompletableFuture;

public class DIConfigGen extends ConfigDataProvider {

	private static CompletableFuture<HolderLookup.Provider> pvd;

	public DIConfigGen(DataGenerator generator, CompletableFuture<HolderLookup.Provider> pvd) {
		super(generator, pvd, "Golem Spawn Config");
		this.pvd = pvd;
	}

	public static <T> Holder<T> resolve(ResourceKey<T> key) {
		try {
			return pvd.get().holderOrThrow(key);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void add(ConfigDataProvider.Collector map) {

	}

}
