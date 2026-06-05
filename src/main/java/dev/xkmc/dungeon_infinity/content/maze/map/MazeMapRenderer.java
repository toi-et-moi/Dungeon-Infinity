package dev.xkmc.dungeon_infinity.content.maze.map;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.xkmc.dungeon_infinity.init.reg.DIMeta;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public class MazeMapRenderer {

	public static void renderMap(long seed, Player player, PoseStack pose, SubmitNodeCollector col, int light) {
		BlockPos pos = player.blockPosition();
		int x = Math.floorDiv(pos.getX(), 16 * 25);
		int z = Math.floorDiv(pos.getZ(), 16 * 25);
		int y = Mth.clamp(pos.getY() / 16, 0, 15);
		var tex = MazeMapTextureManager.get().getDetail(seed, x, y, z);
		var fog = MazeMapTextureManager.get().getFog(seed, x, y, z);
		fog.update(DIMeta.HISTORY.type().getOrCreate(player).getOrCreate(x, y, z));
		pose.pushPose();
		pose.translate(1.5f, 1.5f, 0);
		col.submitCustomGeometry(pose, RenderTypes.text(tex.id), (mat, buffer) -> {
			float m = 125 / 128f;
			buffer.addVertex(mat, 0, 125, -0.01F).setColor(-1).setUv(0, m).setLight(light);
			buffer.addVertex(mat, 125, 125, -0.01F).setColor(-1).setUv(m, m).setLight(light);
			buffer.addVertex(mat, 125, 0, -0.01F).setColor(-1).setUv(m, 0).setLight(light);
			buffer.addVertex(mat, 0, 0, -0.01F).setColor(-1).setUv(0, 0).setLight(light);
		});
		pose.pushPose();
		pose.scale(5, 5, 1);
		if (!player.isCreative() || !player.isShiftKeyDown())
			col.submitCustomGeometry(pose, RenderTypes.text(fog.id), (mat, buffer) -> {
				float m = 25 / 32f;
				buffer.addVertex(mat, 0, 25, -0.02F).setColor(-1).setUv(0, m).setLight(light);
				buffer.addVertex(mat, 25, 25, -0.02F).setColor(-1).setUv(m, m).setLight(light);
				buffer.addVertex(mat, 25, 0, -0.02F).setColor(-1).setUv(m, 0).setLight(light);
				buffer.addVertex(mat, 0, 0, -0.02F).setColor(-1).setUv(0, 0).setLight(light);
			});
		pose.popPose();
		int px = pos.getX() - x * 16 * 25;
		int pz = pos.getZ() - z * 16 * 25;
		pose.translate(px / 16f * 5f, pz / 16f * 5f, 0);
		float r = Mth.sin(((int) (System.currentTimeMillis() % 1000)) / 1000f * Math.PI) * 0 + 2;
		pose.scale(r, r, 1);
		var yrot = player.getYRot();
		pose.mulPose(Axis.ZP.rotationDegrees(yrot));
		col.submitCustomGeometry(pose, RenderTypes.text(tex.id), (mat, buffer) -> {
			float s = 127 / 128f;
			buffer.addVertex(mat, 0, 1, -0.03F).setColor(-1).setUv(s, 1).setLight(light);
			buffer.addVertex(mat, 1, -1f, -0.03F).setColor(-1).setUv(1, 1).setLight(light);
			buffer.addVertex(mat, 0, -0.5f, -0.03F).setColor(-1).setUv(1, s).setLight(light);
			buffer.addVertex(mat, -1, -1f, -0.03F).setColor(-1).setUv(s, s).setLight(light);
		});
		pose.popPose();
	}

}
