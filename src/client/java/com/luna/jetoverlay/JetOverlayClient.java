package com.luna.jetoverlay;

import com.luna.jetoverlay.client.JetOverlayHud;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PlayerRideable;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import org.joml.Quaternionf;

import java.util.List;

public class JetOverlayClient implements ClientModInitializer {
	private static void afterEntities(WorldRenderContext context) {
		var worldIn = Minecraft.getInstance().level;
		Player player = Minecraft.getInstance().player;
		int range = 25;
		BlockPos pos = Minecraft.getInstance().player.blockPosition();
		List<LivingEntity> entities = worldIn.getNearbyEntities(LivingEntity.class,
				TargetingConditions.DEFAULT,
				player,
				new AABB(pos.subtract(new BlockPos(range, range, range)), pos.offset(new BlockPos(range, range, range)))
		);
		Camera renderInfo = Minecraft.getInstance().gameRenderer.getMainCamera();
		PoseStack stack = new PoseStack();
		Minecraft client = Minecraft.getInstance();
		for (var entity : entities) {
			System.out.println(renderInfo.rotation().x + " " + renderInfo.rotation().y + " " + renderInfo.rotation().z + " " + renderInfo.rotation().w);
			String text = "yippe";
			float size = 0.04f;
			stack.pushPose();
			stack.translate(-renderInfo.getPosition().x + entity.position().x, -renderInfo.getPosition().y + entity.position().y + 2, -renderInfo.getPosition().z + entity.position().z);
			stack.mulPose(new Quaternionf(0,1,0,0));
			stack.scale(-size, -size, -size);
			float dunnosize = -client.font.width(text) / 2f;
			client.font.drawInBatch(text, dunnosize, 0, 0xFFDF5050, false,
					stack.last().pose(), Minecraft.getInstance().renderBuffers().bufferSource(), Font.DisplayMode.NORMAL,
					0, 10);
			Minecraft.getInstance().renderBuffers().bufferSource().endBatch();
			stack.popPose();

		}
	}

	@Override
	public void onInitializeClient() {

		HudRenderCallback.EVENT.register(new JetOverlayHud());
		WorldRenderEvents.AFTER_TRANSLUCENT.register(JetOverlayClient::afterEntities);
	}


}
