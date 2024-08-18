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

	private Entity _trackedEntity;
	@Inject(at = @At("HEAD"), method = "renderEntity")
	private void renderEntity(Entity entity, double camX, double camY, double camZ, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, CallbackInfo ci) {
		_trackedEntity = entity;
		if(JetOverlayClient.shouldRenderOutline) {
            if (JetOverlayClient.markedEntities.contains(entity.getId())) {
				MultiBufferSource multiBufferSource;
				OutlineBufferSource outlineBufferSource = this.renderBuffers.outlineBufferSource();
				multiBufferSource = outlineBufferSource;
				int i = entity.getTeamColor();
				outlineBufferSource.setColor(0x16711680, 0, 0, 255);

            }
		}

	}
	@Inject(at = @At("HEAD"), method = "shouldShowEntityOutlines", cancellable = true)
	private void doEntityOutline(CallbackInfoReturnable<Boolean> cir) {
        if (!this.minecraft.gameRenderer.isPanoramicMode() && this.entityTarget != null && this.entityEffect != null && this.minecraft.player != null || JetOverlayClient.shouldRenderOutline && _trackedEntity != null && JetOverlayClient.markedEntities.contains((Object)_trackedEntity.getId())) {
			cir.setReturnValue(true);
		}


	}

}