package com.luna.jetoverlay.mixin.client;
import com.luna.jetoverlay.JetOverlayClient;
import com.luna.jetoverlay.client.JetOverlayHud;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public abstract class MixinEntityRenderer {
    @Inject(at = @At("HEAD"), method = "shouldEntityAppearGlowing", cancellable = true)
    public void ShouldEntityAppearGlowing(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if(entity.isCurrentlyGlowing() || JetOverlayClient.shouldRenderOutline && JetOverlayClient.markedEntities.contains((Object)entity.getId())) {
            cir.setReturnValue(true);
        }
    }

}
