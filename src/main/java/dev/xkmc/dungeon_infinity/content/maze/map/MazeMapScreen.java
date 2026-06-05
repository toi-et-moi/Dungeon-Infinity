package dev.xkmc.dungeon_infinity.content.maze.map;

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
		var pos = player.blockPosition();
		int x = Math.floorDiv(pos.getX(), 16 * 25);
		int z = Math.floorDiv(pos.getZ(), 16 * 25);
		int y = Mth.clamp(pos.getY() / 16, 0, 15);
		var tex = MazeMapTextureManager.get().getDetail(seed, x, y, z);
		var fog = MazeMapTextureManager.get().getFog(seed, x, y, z);
		fog.update(DIMeta.HISTORY.type().getOrCreate(player).getOrCreate(x, y, z));
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
		int px = pos.getX() - x * 16 * 25;
		int pz = pos.getZ() - z * 16 * 25;
		g.pose().translate(px / 16f * 5f, pz / 16f * 5f);
		float r = Mth.sin(((int) (System.currentTimeMillis() % 1000)) / 1000f * Math.PI) / 2 + 1;
		g.pose().scale(r, r);
		;
		g.fill(-1, -1, 1, 1, 0xafff00ff);
		g.pose().popMatrix();
	}

}
