package com.luna.jetoverlay;

import com.luna.jetoverlay.client.HudOverlay;
import com.luna.jetoverlay.client.JetOverlayHud;
import com.luna.jetoverlay.misc.EasingHUDUtils;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.Camera;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PlayerRideable;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.apache.commons.lang3.ClassUtils;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.lwjgl.glfw.GLFW;
import java.util.ArrayList;
import java.util.List;
public class JetOverlayClient implements ClientModInitializer {
	public static boolean shouldRenderOutline = false;
	public static List<Integer> markedEntities = new ArrayList<>();
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
		ClientTickEvents.END_CLIENT_TICK.register((yippe) -> {
			if(toggle_outline.consumeClick()) {
				shouldRenderOutline = !shouldRenderOutline;
			}
		});
		WorldRenderEvents.END.register((whatever) -> {

		});

	}


}
