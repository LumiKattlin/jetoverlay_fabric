package com.luna.jetoverlay.mixin.client;

import com.luna.jetoverlay.JetOverlayClient;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class MixinOutlineRenderer {
	@Shadow protected abstract boolean shouldShowEntityOutlines();

	@Shadow @Final private Minecraft minecraft;

	@Shadow @Final private RenderBuffers renderBuffers;

	@Shadow @Final private EntityRenderDispatcher entityRenderDispatcher;

	@Inject(at = @At("HEAD"), method = "renderEntity")
	private void renderEntity(Entity entity, double camX, double camY, double camZ, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, CallbackInfo ci) {
		if(JetOverlayClient.shouldRenderOutline) {
			MultiBufferSource multiBufferSource;
			OutlineBufferSource outlineBufferSource = this.renderBuffers.outlineBufferSource();
			multiBufferSource = outlineBufferSource;
			int i = entity.getTeamColor();
			outlineBufferSource.setColor(FastColor.ARGB32.red(i), FastColor.ARGB32.green(i), FastColor.ARGB32.blue(i), 1);
			bufferSource = outlineBufferSource;
			double d = Mth.lerp((double)partialTick, entity.xOld, entity.getX());
			double e = Mth.lerp((double)partialTick, entity.yOld, entity.getY());
			double f = Mth.lerp((double)partialTick, entity.zOld, entity.getZ());
			float g = Mth.lerp(partialTick, entity.yRotO, entity.getYRot());
			this.entityRenderDispatcher
					.render(entity, d - camX, e - camY, f - camZ, g, partialTick, poseStack, bufferSource, this.entityRenderDispatcher.getPackedLightCoords(entity, partialTick));
		}

	}
}