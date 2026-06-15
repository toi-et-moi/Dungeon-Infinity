package dev.xkmc.dungeon_infinity.content.map;

import dev.xkmc.dungeon_infinity.content.cap.MazePos;
import dev.xkmc.dungeon_infinity.init.reg.DIMeta;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public class MazeMapScreen extends Screen {

	private final long seed;

	protected MazeMapScreen(long seed) {
		super(Component.literal("Maze Map"));
		this.seed = seed;
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor g, int mx, int my, float pt) {
		super.extractRenderState(g, mx, my, pt);
		Player player = Minecraft.getInstance().player;
		if (player == null) return;
		var pos = MazePos.map(player.blockPosition());
		var tex = MazeMapTextureManager.get().getDetail(seed, pos);
		var fog = MazeMapTextureManager.get().getFog(seed, pos);
		var visit = DIMeta.HISTORY.type().getOrCreate(player).getOrCreate(pos);
		tex.update(visit);
		fog.update(visit);
		int x0 = g.guiWidth() / 2, y0 = g.guiHeight() / 2;
		float rate = Math.min(x0 / 64f, y0 / 64f) / 1.5f;
		g.pose().pushMatrix();
		g.pose().translate(x0, y0);
		g.pose().scale(rate, rate);
		g.pose().translate(-63, -63);
		g.blit(RenderPipelines.GUI_TEXTURED, tex.id, 0, 0, 0, 0, 125, 125, 128, 128);
		g.pose().pushMatrix();
		g.pose().scale(5, 5);
		if (!player.isCreative() || !player.isShiftKeyDown())
			g.blit(RenderPipelines.GUI_TEXTURED, fog.id, 0, 0, 0, 0, 25, 25, 32, 32);
		g.pose().popMatrix();
		g.pose().translate(pos.px() / 16f * 5f, pos.pz() / 16f * 5f);
		float r = Mth.sin(((int) (System.currentTimeMillis() % 1000)) / 1000f * Math.PI) / 2 + 1;
		g.pose().scale(r, r);
		;
		g.fill(-1, -1, 1, 1, 0xafff00ff);
		g.pose().popMatrix();
	}

}
