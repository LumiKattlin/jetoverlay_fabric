package com.luna.jetoverlay;

import com.luna.jetoverlay.client.GogglesReceiverScreen;
import com.luna.jetoverlay.client.HudOverlay;
import com.luna.jetoverlay.client.JetOverlayHud;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.entity.LivingEntity;
import org.lwjgl.glfw.GLFW;
import java.util.ArrayList;
import java.util.List;

public class JetOverlayClient implements ClientModInitializer {
	public static boolean renderOverlay = false;
	public static List<LivingEntity> markedEntities = new ArrayList<>();
	public static final KeyMapping toggle_outline = new KeyMapping("key.toggle-outline",
			InputConstants.Type.KEYSYM,
			GLFW.GLFW_KEY_LEFT_CONTROL,
			"jetoverlay"
			);
	public static final KeyMapping markEntityAsTarget = new KeyMapping("key.mark-entity-target",
			InputConstants.Type.KEYSYM,
			GLFW.GLFW_KEY_D,
			"jetoverlay"
			);


	@Override
	public void onInitializeClient() {
		HudRenderCallback.EVENT.register(new JetOverlayHud());
		HudRenderCallback.EVENT.register(new HudOverlay());
		KeyBindingHelper.registerKeyBinding(toggle_outline);
		KeyBindingHelper.registerKeyBinding(markEntityAsTarget);
		MenuScreens.register(JetOverlay.GOGGLES_RECEIVER_SCREEN_HANDLER, GogglesReceiverScreen::new);
		WorldRenderEvents.END.register((whatever) -> {
		});

	}


}
