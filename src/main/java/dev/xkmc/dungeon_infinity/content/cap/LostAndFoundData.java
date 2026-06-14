package dev.xkmc.dungeon_infinity.content.cap;

import dev.xkmc.l2core.capability.player.PlayerCapabilityTemplate;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

@SerialClass
public class LostAndFoundData extends PlayerCapabilityTemplate<LostAndFoundData> {

	@SerialField
	public final List<ItemStack> list = new ArrayList<>();

	public List<ItemStack> poll(int count) {
		List<ItemStack> ans = new ArrayList<>();
		List<ItemStack> rem = new ArrayList<>();
		for (var e : list) {
			if (ans.size() < count) {
				ans.add(e);
			} else {
				rem.add(e);
			}
		}
		list.clear();
		list.addAll(rem);
		return ans;
	}

	public void add(ItemStack e) {
		if (e.isEmpty()) return;
		list.add(e);
	}

}
