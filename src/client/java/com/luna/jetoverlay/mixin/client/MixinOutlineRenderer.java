package com.luna.jetoverlay.mixin.client;

import com.luna.jetoverlay.JetOverlayClient;
import com.luna.jetoverlay.client.JetOverlayHud;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelRenderer.class)
public abstract class MixinOutlineRenderer {
	private Object entityTarget;

	@Shadow protected abstract boolean shouldShowEntityOutlines();

	@Shadow @Final private Minecraft minecraft;

	@Shadow @Final private RenderBuffers renderBuffers;

	@Shadow @Final private EntityRenderDispatcher entityRenderDispatcher;

	@Shadow private @Nullable PostChain entityEffect;

	private static int[] outlineColorFromHealthPercentage(float __healthFraction) {
		if (__healthFraction > 0.66f) {
			return new int[]{0, 0xFFFFFFFF, 0};
		}
		if (__healthFraction > 0.33f) {
			return new int[]{0xFFFFFFFF, 0xFFFFFFFF, 0};
		}
		return new int[]{0xFFFFFFFF, 0, 0};
	}

	private LivingEntity _trackedEntity;
	@Inject(at = @At("HEAD"), method = "renderEntity")
	private void renderEntity(Entity entity, double camX, double camY, double camZ, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, CallbackInfo ci) {
		if (entity instanceof LivingEntity) {
			_trackedEntity = (LivingEntity) entity;
		}

		if (JetOverlayClient.markedEntities.contains(_trackedEntity) && JetOverlayClient.shouldRenderOutline) {
			OutlineBufferSource outlineBufferSource = renderBuffers.outlineBufferSource();

			int[] colors = outlineColorFromHealthPercentage(_trackedEntity.getHealth() / _trackedEntity.getMaxHealth());
			outlineBufferSource.setColor(colors[0], colors[1], colors[2], 0xFFFFFFFF);
		}
	}

	@Inject(at = @At("HEAD"), method = "shouldShowEntityOutlines", cancellable = true)
	private void doEntityOutline(CallbackInfoReturnable<Boolean> cir) {
        if (!this.minecraft.gameRenderer.isPanoramicMode() && this.entityTarget != null && this.entityEffect != null && this.minecraft.player != null || JetOverlayClient.shouldRenderOutline && _trackedEntity != null && JetOverlayClient.markedEntities.contains(_trackedEntity)) {
			cir.setReturnValue(true);
        }
	}
}