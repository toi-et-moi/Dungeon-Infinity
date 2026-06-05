package dev.xkmc.dungeon_infinity.content.maze.map;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;

public class MazeMapRenderer {

	public static void renderMap(long seed, BlockPos pos, PoseStack pose, SubmitNodeCollector col, int light) {
		int x = Math.floorDiv(pos.getX(), 16 * 25);
		int z = Math.floorDiv(pos.getZ(), 16 * 25);
		int y = Mth.clamp(pos.getY() / 16, 0, 15);
		var tex = MazeMapTextureManager.get().getDetail(seed, x, y, z);
		col.submitCustomGeometry(pose, RenderTypes.text(tex.id), (mat, buffer) -> {
			buffer.addVertex(mat, 1, 126, -0.01F).setColor(-1).setUv(0, 125 / 128f).setLight(light);
			buffer.addVertex(mat, 126, 126, -0.01F).setColor(-1).setUv(125 / 128f, 125 / 128f).setLight(light);
			buffer.addVertex(mat, 126, 1, -0.01F).setColor(-1).setUv(125 / 128f, 0).setLight(light);
			buffer.addVertex(mat, 1, 1, -0.01F).setColor(-1).setUv(0, 0).setLight(light);
		});
		pose.pushPose();
		int px = pos.getX() - x * 16 * 25;
		int pz = pos.getZ() - z * 16 * 25;
		pose.translate(1 + px / 16f * 5f, 1 + pz / 16f * 5f, 0);
		float r = Mth.sin(((int) (System.currentTimeMillis() % 1000)) / 1000f * Math.PI) * 0 + 2;
		pose.scale(r, r, 1);
		var yrot = Minecraft.getInstance().player.getYRot();
		pose.mulPose(Axis.ZP.rotationDegrees(yrot));
		col.submitCustomGeometry(pose, RenderTypes.text(tex.id), (mat, buffer) -> {
			buffer.addVertex(mat, 0, 1, -0.02F).setColor(-1).setUv(127f / 128f, 1).setLight(light);
			buffer.addVertex(mat, 1, -1f, -0.02F).setColor(-1).setUv(1, 1).setLight(light);
			buffer.addVertex(mat, 0, -0.5f, -0.02F).setColor(-1).setUv(1, 127f / 128f).setLight(light);
			buffer.addVertex(mat, -1, -1f, -0.02F).setColor(-1).setUv(127f / 128f, 127f / 128f).setLight(light);
		});
		pose.popPose();
	}

}
