package com.luna.jetoverlay.client;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class HudOverlay implements HudRenderCallback {
	public static final ResourceLocation leftElement = new ResourceLocation("jetoverlay", "textures/hud/ui_left_element.png");

	@Override
	public void onHudRender(GuiGraphics drawContext, float tickDelta) {
		// TODO: Replace this with a better overlay UI.
//        if (JetOverlayClient.renderOverlay) {
//            drawContext.blit(leftElement, 0, 90, 0, 0, 0,8 ,200 , 8, 200);
//        }
	}
}
