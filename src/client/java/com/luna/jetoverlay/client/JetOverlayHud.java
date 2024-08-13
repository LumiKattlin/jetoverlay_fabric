package com.luna.jetoverlay.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.DisplayRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.core.pattern.TextRenderer;
import org.joml.*;
import java.awt.*;
import java.lang.Math;
import java.util.List;

public class JetOverlayHud implements HudRenderCallback {
    boolean shouldDraw = false;

    @Override
    public void onHudRender(GuiGraphics drawContext, float tickDelta) {
        int y;
        int x;
        Minecraft client = Minecraft.getInstance();

        if (client != null) {
            int width = client.getWindow().getGuiScaledWidth();
            int height = client.getWindow().getGuiScaledHeight();
            x = width / 2;
            y = height;
            int range = 20;
            BlockPos pos = Minecraft.getInstance().player.blockPosition();
            Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
            LocalPlayer player = Minecraft.getInstance().player;

            var worldIn = Minecraft.getInstance().level;
            List<LivingEntity> entities = worldIn.getNearbyEntities(LivingEntity.class,
                    TargetingConditions.DEFAULT,
                    player,
                    new AABB(pos.subtract(new BlockPos(range, range, range)), pos.offset(new BlockPos(range, range, range)))
            );
            Camera renderInfo = Minecraft.getInstance().gameRenderer.getMainCamera();
        }

    }



}
