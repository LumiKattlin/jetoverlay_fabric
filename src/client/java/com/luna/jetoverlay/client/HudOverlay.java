package com.luna.jetoverlay.client;

import com.luna.jetoverlay.JetOverlayClient;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
public class HudOverlay implements HudRenderCallback {
    public static final ResourceLocation leftElement = new ResourceLocation("jetoverlay", "textures/hud/ui_left_element.png");

    @Override
    public void onHudRender(GuiGraphics drawContext, float tickDelta) {
        if (JetOverlayClient.shouldRenderOutline) {
           // drawContext.blit(leftElement, 0, 90, 0, 0, 0,8 ,200 , 8, 200);
        }
    }
}
