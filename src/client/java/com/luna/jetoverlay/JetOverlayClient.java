package com.luna.jetoverlay;

import com.luna.jetoverlay.client.JetOverlayHud;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class JetOverlayClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		HudRenderCallback.EVENT.register(new JetOverlayHud());
	}


}