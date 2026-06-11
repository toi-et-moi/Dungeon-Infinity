package dev.xkmc.dungeon_infinity.content.spawn;

import dev.xkmc.dungeon_infinity.compat.GolemSpawnTicker;
import dev.xkmc.dungeon_infinity.content.cap.SectionRoom;
import dev.xkmc.dungeon_infinity.content.config.TemplateConfig;
import org.jspecify.annotations.Nullable;

public class SpawnHelper {

	public static MobSpawnTicker createTickerFromTemplate(TemplateConfig.TemplateData info, @Nullable SectionRoom[][][] rooms) {
		var ans = new GolemSpawnTicker();
		ans.trial = info.spawn();
		for (SectionRoom[][] room : rooms) {
			for (var e : room[0]) {
				if (e == null) continue;
				ans.addTargetPos(e.getBlockPos().offset(8, 3, 8));
			}
		}
		return ans;
	}

}
