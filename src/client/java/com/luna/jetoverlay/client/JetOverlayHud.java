package com.luna.jetoverlay.client;

import com.luna.jetoverlay.JetOverlayClient;
import com.luna.jetoverlay.ModItems;
import com.luna.jetoverlay.armor.JetGoggles;
import net.minecraft.client.Camera;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.*;

public class JetOverlayHud implements HudRenderCallback {

	boolean _hudHidden = false;

	protected void drawTextAt(String text, int color, Vector3f worldPosition, GuiGraphics drawContext) {
		Minecraft mc = Minecraft.getInstance();
		// Matrix math shamelessly stolen from here:
		// https://github.com/Klemmbaustein/Klemmgine/blob/f5454be1e95c43cbb91f0f55abf682774572defe/EngineSource/Objects/Components/CameraComponent.cpp#L36-L43
		// And here:
		// https://github.com/Klemmbaustein/Klemmgine/blob/f5454be1e95c43cbb91f0f55abf682774572defe/EngineSource/Rendering/Camera/Camera.cpp#L41-L43
		Camera cam = mc.gameRenderer.getMainCamera();

		if (cam.getXRot() < -85 || cam.getXRot() > 85)
			return;

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
		if (pos.z <= 0)
			return;

		float x = pos.x / pos.z, y = pos.y / pos.z;

		int windowWidth = drawContext.guiWidth(), windowHeight = drawContext.guiHeight();

		if (Float.isNaN(x) || Float.isNaN(y))
			return;

		// Convert to UI coordinates. So from -1 - 1 to 0 - width/height
		int xPos = (int) (x * (windowWidth / 2.0f)) + windowWidth / 2;
		int yPos = (int) (y * (windowHeight / 2.0f)) + windowHeight / 2;
		xPos -= mc.font.width(text) / 2;
		drawContext.drawString(mc.font, text, xPos, yPos, color);
	}

	int colorFromHealthPercentage(int __percentage) {
		if (__percentage > 66) {
			return 0x00FF00;
		}
		if (__percentage > 33) {
			return 0xFFFF00;
		}
		return 0xFF0000;
	}

	private void markEntityLogic(Player __player) {
		final float _RAYCAST_RANGE = 1000f;

		Vec3 eyePosition = __player.getEyePosition();
		Vec3 viewVector = __player.getViewVector(0);
		Vec3 rayCastEnd = eyePosition.add(viewVector.multiply(_RAYCAST_RANGE, _RAYCAST_RANGE, _RAYCAST_RANGE));
		AABB boundingBox = __player.getBoundingBox().expandTowards(viewVector.scale(_RAYCAST_RANGE)).inflate(_RAYCAST_RANGE, _RAYCAST_RANGE, _RAYCAST_RANGE);

		EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(
				__player, eyePosition, rayCastEnd, boundingBox,
				(hitEntity) -> !hitEntity.isSpectator() && hitEntity != __player, _RAYCAST_RANGE);

		if (entityHitResult != null && JetOverlayClient.markEntityAsTarget.consumeClick()) {
			LivingEntity entity = (LivingEntity) entityHitResult.getEntity();
			if (JetOverlayClient.markedEntities.contains(entity))
				JetOverlayClient.markedEntities.remove(entity);
			else
				JetOverlayClient.markedEntities.add(entity);
		}
	}

	private void renderOverlay(GuiGraphics __drawContext, float __tickDelta) {
		ClientLevel world = Minecraft.getInstance().level;
		Player player = Minecraft.getInstance().player;

		if (world == null || player == null) {
			return;
		}

		markEntityLogic(player);

		final int _NEARBY_ENTITIES_RANGE = 30;

		for (var entity : JetOverlayClient.markedEntities) {
			if (!entity.isAlive() || entity.distanceTo(player) > _NEARBY_ENTITIES_RANGE) {
				JetOverlayClient.markedEntities.remove(entity);
				break;
			}

			int entityHealthPercentage = (int) (entity.getHealth() / entity.getMaxHealth() * 100);
			String entityHealthText = entityHealthPercentage + "%";
			int color = colorFromHealthPercentage(entityHealthPercentage);

			drawTextAt(entityHealthText, color, entity.position().toVector3f().add(0, 2.5f, 0), __drawContext);
		}

		if (JetOverlayClient.TOGGLE_OUTLINE.consumeClick()) {
			_hudHidden = !_hudHidden;
		}

		drawOverlayText(__drawContext, player);
	}

	void drawBlockPositionText(GuiGraphics __drawContext, Player __player, BlockPos __position, String __text) {
		if (__position.getCenter().distanceTo(__player.getPosition(0)) > 16)
			return;

		Vec3 blockPos = __position.getCenter();

		drawTextAt(__text,
				0xFF00A0,
				new Vector3f((float) blockPos.x, (float) blockPos.y, (float) blockPos.z),
				__drawContext);
	}

	void drawOverlayText(GuiGraphics __drawContext, Player __player) {
		var headSlot = __player.getItemBySlot(EquipmentSlot.HEAD);

		if (!headSlot.isEmpty() && headSlot.hasTag() && headSlot.getTag().contains(JetGoggles.GOGGLES_BLOCK_TAG_NAME)) {
			var linkedBlocksTag = headSlot.getTag().getCompound(JetGoggles.GOGGLES_BLOCK_TAG_NAME);

			String showOverlayName = KeyMapping.createNameSupplier(JetOverlayClient.TOGGLE_OUTLINE.getName()).get().getString();
			int color = 0x00FF00;

			if (_hudHidden) {
				__drawContext.drawString(Minecraft.getInstance().font, "Press " + showOverlayName + " to show overlay", 2, 2, color);
				return;
			}

			int receiverId = 1;
			for (String key : linkedBlocksTag.getAllKeys()) {
				int[] value = linkedBlocksTag.getIntArray(key);

				if (value.length != 3)
					continue;

				String drawnString = "Linked receiver #" + receiverId;

				drawBlockPositionText(__drawContext, __player, new BlockPos(value[0], value[1], value[2]), drawnString);
				receiverId++;
			}

			int yOffset = Minecraft.getInstance().font.lineHeight + 1;

			__drawContext.fill(0, 0, 160, (linkedBlocksTag.getAllKeys().size() + 2) * yOffset + 4,
					0x88444444);
			__drawContext.drawString(Minecraft.getInstance().font, "Linked to receivers:", 2, 2, color);

			int yPos = yOffset + 2;
			receiverId = 1;
			for (String key : linkedBlocksTag.getAllKeys()) {
				int[] value = linkedBlocksTag.getIntArray(key);

				if (value.length != 3)
					continue;

				String drawnString = "#" + receiverId + ": X: " + value[0] + " Y: " + value[1] + " Z: " + value[2];
				color += 0x200000;
				__drawContext.drawString(Minecraft.getInstance().font, drawnString, 12, yPos, color);
				yPos += yOffset;
				receiverId++;
			}
			color += 0x200000;
			__drawContext.drawString(Minecraft.getInstance().font, "Press " + showOverlayName + " to hide", 2, yPos, color);
		} else
			__drawContext.drawString(Minecraft.getInstance().font, "Not linked to any receivers.", 0, 2, 0xFFFF00);
	}

	@Override
	public void onHudRender(GuiGraphics __drawContext, float __tickDelta) {
		JetOverlayClient.renderOverlay = Minecraft.getInstance().player != null
				&& Minecraft.getInstance().player.getInventory().getArmor(3).is(ModItems.JET_GOGGLES);

		if (JetOverlayClient.renderOverlay) {
			renderOverlay(__drawContext, __tickDelta);
		}
	}
}
