package dev.xkmc.dungeon_infinity.content.cap;

import dev.xkmc.dungeon_infinity.init.reg.DIMeta;
import dev.xkmc.l2serial.network.SerialPacketBase;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;

public record SetRadiusPacket(int rad) implements SerialPacketBase<SetRadiusPacket> {

	@Override
	public void handle(Player player) {
		var data = DIMeta.HISTORY.type().getOrCreate(player);
		data.setRadius(rad);
	}

}
