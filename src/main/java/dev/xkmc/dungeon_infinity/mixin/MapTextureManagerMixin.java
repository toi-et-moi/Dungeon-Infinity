package dev.xkmc.dungeon_infinity.mixin;

import dev.xkmc.dungeon_infinity.content.map.MapTextureManagerProvider;
import dev.xkmc.dungeon_infinity.content.map.MazeMapTextureManager;
import net.minecraft.client.resources.MapTextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MapTextureManager.class)
public class MapTextureManagerMixin implements MapTextureManagerProvider {

	@Unique
	private MazeMapTextureManager dungeon_infinity$maze = new MazeMapTextureManager();

	@Override
	public MazeMapTextureManager dungeon_infinity$getMazeData() {
		return dungeon_infinity$maze;
	}

	@Inject(method = "resetData", at = @At("TAIL"))
	public void dungeon_infinity$resetData(CallbackInfo ci) {
		dungeon_infinity$maze.close();
	}

}
