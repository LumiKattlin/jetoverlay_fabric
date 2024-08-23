package com.luna.jetoverlay.client;

import com.luna.jetoverlay.JetOverlayClient;
import com.luna.jetoverlay.armor.JetGoggles;
import com.luna.jetoverlay.networking.PacketSender;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.*;

import java.util.List;

public class JetOverlayHud implements HudRenderCallback {

	private final float _RAYCAST_RANGE = 1000f;
	private final int _NEARBY_ENTITIES_RANGE = 30;

	float _originalXRot;
	float _originalYRot ;

	protected void drawTextAt(String text, int color, Vector3f worldPosition, GuiGraphics drawContext, LivingEntity entity) {
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

		for (var entity : JetOverlayClient.markedEntities) {
			if (!entity.isAlive() || entity.distanceTo(player) > _NEARBY_ENTITIES_RANGE) {
				JetOverlayClient.markedEntities.remove(entity);
				break;
			}

			int entityHealthPercentage = (int) (entity.getHealth() / entity.getMaxHealth() * 100);
			String entityHealthText = entityHealthPercentage + "%";
			int color = colorFromHealthPercentage(entityHealthPercentage);

			drawTextAt(entityHealthText, color, entity.position().toVector3f().add(0, 2.5f, 0), __drawContext, entity);
		}

		String blockPosString = "Not linked";
		var headSlot = player.getItemBySlot(EquipmentSlot.HEAD);
		if (!headSlot.isEmpty() && headSlot.hasTag() && headSlot.getTag().contains(JetGoggles.GOGGLES_BLOCK_TAG_NAME)) {
			blockPosString = "Linked to: " + headSlot.getTag().get(JetGoggles.GOGGLES_BLOCK_TAG_NAME).getAsString();
		}

		__drawContext.drawString(Minecraft.getInstance().font, blockPosString, 0, 2, 0x00FF00);
	}

	@Override
    public void onHudRender(GuiGraphics __drawContext, float __tickDelta) {

		CameraRotationDirection rotationDirection = detectCameraRotation(Minecraft.getInstance().gameRenderer.getMainCamera());
		if (rotationDirection == CameraRotationDirection.LEFT || rotationDirection == CameraRotationDirection.RIGHT) {
			__drawContext.drawString(Minecraft.getInstance().font, rotationDirection.toString(), 0, 10, 0x00FF00);
		}
		if (rotationDirection == CameraRotationDirection.DOWN || rotationDirection == CameraRotationDirection.UP) {
			__drawContext.drawString(Minecraft.getInstance().font, rotationDirection.toString(), 0, 20, 0x00FF00);
		}

		if (JetOverlayClient.shouldRenderOutline) {
			renderOverlay(__drawContext, __tickDelta);
		}
	}

	ResourceLocation _clientChannel = new ResourceLocation("jetoverlay_client", "redstone_emitter_client");

	public CameraRotationDirection detectCameraRotation(Camera __camera) {
		//X axis decreases when going up, increases when going down

		if (__camera.getXRot() != _originalXRot) {
			if (__camera.getXRot() < _originalXRot) {
				_originalXRot = __camera.getXRot();
				PacketSender.SendPacket("Yippe");
				return CameraRotationDirection.UP;

			}
			else {
				_originalXRot = __camera.getXRot();
				return CameraRotationDirection.DOWN;
			}
		}
		//Y axis decreases when going to the left, increases when going to the right
		if (__camera.getYRot() != _originalYRot) {
			if (__camera.getYRot() < _originalYRot) {
				_originalYRot = __camera.getYRot();
				return CameraRotationDirection.LEFT;
			}
			else {
				_originalYRot = __camera.getYRot();
				return CameraRotationDirection.RIGHT;
			}
		}
		return CameraRotationDirection.NOTHING;
	}
	public float ReturnRotationDifference(float __originalPos, float __newPos) {
		return __newPos - __originalPos;
	}
	public float ReturnRedstonePower(float __difference) {
		float _redstonePower = 0;
		return _redstonePower;
	}
}
