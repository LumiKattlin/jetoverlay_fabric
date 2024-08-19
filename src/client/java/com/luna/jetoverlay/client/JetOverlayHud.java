package com.luna.jetoverlay.client;

import com.luna.jetoverlay.JetOverlayClient;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.loader.impl.util.log.Log;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.*;

import java.util.List;

public class JetOverlayHud implements HudRenderCallback {
	boolean shouldDraw = false;

	protected void drawTextAt(String text, Vector3f worldPosition, GuiGraphics drawContext, LivingEntity entity) {
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
		Float _entityHealth = entity.getHealth();
		Float _entityMaxHealth = entity.getMaxHealth();
		Integer _entityHealthPercentage = (int)(_entityHealth / _entityMaxHealth) * 100;
		String _entityHealthText = String.valueOf(_entityHealthPercentage) + "%";
		xPos -= mc.font.width(_entityHealthText) / 2;
		if (_entityHealthPercentage <= 100 && _entityHealthPercentage > 50 ) {
			drawContext.drawString(mc.font, _entityHealthText, xPos, yPos, 0x00FF00);
		}
		else if(_entityHealthPercentage <= 50 && _entityHealthPercentage > 25) {
			drawContext.drawString(mc.font, _entityHealthText, xPos, yPos, 0xFFAA00);
		}
		else {
			drawContext.drawString(mc.font, _entityHealthText, xPos, yPos, 0xFF0000);
		}


	}

	@Override
    public void onHudRender(GuiGraphics drawContext, float tickDelta) {
		CameraRotationDirection rotationDirection = DetectCameraRotation(Minecraft.getInstance().gameRenderer.getMainCamera());
		if (rotationDirection == CameraRotationDirection.LEFT || rotationDirection == CameraRotationDirection.RIGHT) {
			drawContext.drawString(Minecraft.getInstance().font, rotationDirection.toString(), 0,10,0x00FF00);
		}
		if(rotationDirection == CameraRotationDirection.DOWN || rotationDirection == CameraRotationDirection.UP) {
			drawContext.drawString(Minecraft.getInstance().font, rotationDirection.toString(), 0,20,0x00FF00);

		}
		if (JetOverlayClient.shouldRenderOutline) {
			var worldIn = Minecraft.getInstance().level;
			Player player = Minecraft.getInstance().player;

			if (worldIn == null || player == null) {
				return;
			}
			int range = 30;
			BlockPos pos = player.blockPosition();
			List<LivingEntity> entities = worldIn.getNearbyEntities(LivingEntity.class,
					TargetingConditions.DEFAULT,
					player,
					new AABB(pos.subtract(new BlockPos(range, range, range)), pos.offset(new BlockPos(range, range, range)))
			);
			Vec3 eyeposition = player.getEyePosition();
			float raycastRange = 1000f;
			Vec3 vec32 = player.getViewVector(1.0F);
			Vec3 vec33 = eyeposition.add(vec32.x * raycastRange, vec32.y * raycastRange, vec32.z * raycastRange);
			float f = 1.0F;
			AABB aABB = player.getBoundingBox().expandTowards(vec32.scale(raycastRange)).inflate(raycastRange, raycastRange, raycastRange);
			EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(player, eyeposition, vec33, aABB, (entityx) -> !entityx.isSpectator() && entityx != player, raycastRange);
			if (entityHitResult != null) {
				if(JetOverlayClient.markEntityAsTarget.consumeClick()) {
					System.out.println(entityHitResult.getEntity().isCurrentlyGlowing());

					if (JetOverlayClient.markedEntities.contains(entityHitResult.getEntity().getId())) JetOverlayClient.markedEntities.remove((Object)entityHitResult.getEntity().getId()); else JetOverlayClient.markedEntities.add(entityHitResult.getEntity().getId());
				}
			}
			for (var entity : entities) {
				if (JetOverlayClient.markedEntities.contains((Object)entity.getId())) {
					drawTextAt("", entity.position().toVector3f().add(0, 2.5f, 0), drawContext, entity);
					drawContext.drawString(Minecraft.getInstance().font, "Targets: " + JetOverlayClient.markedEntities.toString(), 0, 2, 0x00FF00);
				}


			}
		}
    }
	float _originalXRot;
	float _originalYRot ;
	public CameraRotationDirection DetectCameraRotation(Camera __camera) {
		//X axis decreases when going up, increases when going down
		if(__camera.getXRot() != _originalXRot) {
			if(__camera.getXRot() < _originalXRot) {
				System.out.println("Camera going up");
				_originalXRot = __camera.getXRot();
				return CameraRotationDirection.UP;

			}
			else {
				System.out.println("Camera going down");
				_originalXRot = __camera.getXRot();
				return CameraRotationDirection.DOWN;
			}
		}
		//Y axis decreases when going to the left, increases when going to the right
		if(__camera.getYRot() != _originalYRot) {
			if(__camera.getYRot() < _originalYRot) {
				System.out.println("Camera going to the left");
				_originalYRot = __camera.getYRot();
				return CameraRotationDirection.LEFT;
			}
			else {
				System.out.println("Camera going to the right");
				_originalYRot = __camera.getYRot();
				return CameraRotationDirection.RIGHT;
			}


		}
		return CameraRotationDirection.NOTHING;
	}
	public float ReturnRedstonePower(float __difference) {
		float _redstonePower = 0;
		return _redstonePower;
	}
}
