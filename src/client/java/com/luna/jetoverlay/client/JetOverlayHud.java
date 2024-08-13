package com.luna.jetoverlay.client;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import org.joml.*;

import java.util.List;

public class JetOverlayHud implements HudRenderCallback {
	boolean shouldDraw = false;

	protected void drawTextAt(String text, Vector3f worldPosition, GuiGraphics drawContext) {
		Minecraft mc = Minecraft.getInstance();
		// Matrix math shamelessly stolen from here:
		// https://github.com/Klemmbaustein/Klemmgine/blob/f5454be1e95c43cbb91f0f55abf682774572defe/EngineSource/Objects/Components/CameraComponent.cpp#L36-L43
		// And here:
		// https://github.com/Klemmbaustein/Klemmgine/blob/f5454be1e95c43cbb91f0f55abf682774572defe/EngineSource/Rendering/Camera/Camera.cpp#L41-L43
		Camera cam = mc.gameRenderer.getMainCamera();
		Matrix4f projection = mc.gameRenderer.getProjectionMatrix(mc.options.fov().get());
		Matrix4f view = new Matrix4f().identity();
		view.setLookAt(cam.getLookVector(), new Vector3f(0, 0, 0), new Vector3f(0, 1, 0));
		view.translate(cam.getPosition().toVector3f());
		Matrix4f viewProjection = projection.mul(view);

		Vector4f pos = viewProjection.transform(new Vector4f(
				-worldPosition.x,
				-worldPosition.y,
				-worldPosition.z,
				1));

		// Return if the depth (z coordinate) is less than 0
		// So if the position is behind the camera.
		if (pos.z < 0)
			return;

		float x = pos.x / pos.z, y = pos.y / pos.z;

		int windowWidth = drawContext.guiWidth(), windowHeight = drawContext.guiHeight();

		// Convert to UI coordinates. So from -1 - 1 to 0 - width/height
		int xPos = (int) (x * (windowWidth / 2.0f)) + windowWidth / 2;
		int yPos = (int) (y * (windowHeight / 2.0f)) + windowHeight / 2;

		xPos -= mc.font.width(text) / 2;
		drawContext.drawString(mc.font, text, xPos, yPos, 0xffffffff);
	}

	@Override
    public void onHudRender(GuiGraphics drawContext, float tickDelta) {
		var worldIn = Minecraft.getInstance().level;
		Player player = Minecraft.getInstance().player;

		if (worldIn == null || player == null) {
			return;
		}

		int range = 25;
		BlockPos pos = player.blockPosition();
		List<LivingEntity> entities = worldIn.getNearbyEntities(LivingEntity.class,
				TargetingConditions.DEFAULT,
				player,
				new AABB(pos.subtract(new BlockPos(range, range, range)), pos.offset(new BlockPos(range, range, range)))
		);

		for (var entity : entities) {
			drawTextAt("Health: " + entity.getHealth(), entity.position().toVector3f().add(0, 2.5f, 0), drawContext);
		}
    }
}
