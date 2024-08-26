package com.luna.jetoverlay.mixin.client;
import com.luna.jetoverlay.JetOverlayClient;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public abstract class MixinEntityRenderer {
    @Inject(at = @At("HEAD"), method = "shouldEntityAppearGlowing", cancellable = true)
    public void ShouldEntityAppearGlowing(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity ent = null;

        if (entity instanceof LivingEntity) {
            ent = (LivingEntity) entity;
        }

        if(entity.isCurrentlyGlowing() || JetOverlayClient.renderOverlay && JetOverlayClient.markedEntities.contains(ent)) {
            cir.setReturnValue(true);
        }
    }

}
