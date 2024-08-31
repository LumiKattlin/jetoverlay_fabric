package com.luna.jetoverlay;

import com.luna.jetoverlay.blocks.CollisionDetectorEntity;
import com.luna.jetoverlay.blocks.DistanceSensorEntity;
import com.luna.jetoverlay.client.*;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
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

	private static void createDistanceSensorScreen(BlockPos __block, int __initialRange, boolean __onlyPlayers) {
		if (Minecraft.getInstance().level == null)
			return;

		BlockEntity ent = Minecraft.getInstance().level.getBlockEntity(__block);
		if (!(ent instanceof DistanceSensorEntity))
			return;

		Minecraft.getInstance().setScreen(new DistanceSensorScreen(__block, __initialRange, __onlyPlayers));
	}

	private static void createBlockDetectorScreen(BlockPos __block, int __range, int __width) {
		if (Minecraft.getInstance().level == null)
			return;

		BlockEntity ent = Minecraft.getInstance().level.getBlockEntity(__block);
		if (!(ent instanceof CollisionDetectorEntity))
			return;

		Minecraft.getInstance().setScreen(new BlockDetectorScreen(__block, __range, __width));
	}

	@Override
	public void onInitializeClient() {
		HudRenderCallback.EVENT.register(new JetOverlayHud());
		HudRenderCallback.EVENT.register(new HudOverlay());
		KeyBindingHelper.registerKeyBinding(toggle_outline);
		KeyBindingHelper.registerKeyBinding(markEntityAsTarget);
		MenuScreens.register(JetOverlay.GOGGLES_RECEIVER_SCREEN_HANDLER, GogglesReceiverScreen::new);

		ClientPlayNetworking.registerGlobalReceiver(ModNetworking.OPEN_SENSOR_PACKET_ID,
				(client, handler, buf, responseSender) -> {
					BlockPos pos = buf.readBlockPos();
					int range = buf.readInt();
					boolean onlyPlayers = buf.readBoolean();
					client.execute(() -> createDistanceSensorScreen(pos, range, onlyPlayers));
				});

		ClientPlayNetworking.registerGlobalReceiver(ModNetworking.OPEN_BLOCK_DETECTOR_PACKET_ID,
				(client, handler, buf, responseSender) -> {
					BlockPos pos = buf.readBlockPos();
					int range = buf.readInt();
					int width = buf.readInt();
					client.execute(() -> createBlockDetectorScreen(pos, range, width));
				});

	}
}
