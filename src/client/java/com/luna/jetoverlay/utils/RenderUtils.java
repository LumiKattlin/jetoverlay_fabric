package com.luna.jetoverlay.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;

import java.util.List;

public class RenderUtils {

    public static void renderTextAboveEntity(Entity entity, PoseStack poseStack, MultiBufferSource bufferSource, Component text, int packedLight) {
        EntityRenderDispatcher entityRenderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        Font textRenderer = Minecraft.getInstance().font;

        // Translate to the entity's position and move text above the entity
        poseStack.pushPose();
        poseStack.translate(0.0D, entity.getBbHeight() + 0.5F, 0.0D);
        poseStack.mulPose(entityRenderDispatcher.cameraOrientation());
        poseStack.scale(-0.025F, -0.025F, 0.025F);

        // Calculate text alignment (centered)
        float xOffset = (float)(-textRenderer.width(text) / 2);

        // Draw the text batch
        textRenderer.drawInBatch(text, xOffset, 0, 0xFFFFFFFF, false, poseStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0, packedLight);

        poseStack.popPose();

    }
    public static void RenderInWorld(LivingEntity entity, String text, WorldRenderContext context) {

        var worldIn = Minecraft.getInstance().level;
        Player player = Minecraft.getInstance().player;
        int range = 25;
        BlockPos pos = Minecraft.getInstance().player.blockPosition();
      /*  List<LivingEntity> entities = worldIn.getNearbyEntities(LivingEntity.class,
                TargetingConditions.DEFAULT,
                player,
                new AABB(pos.subtract(new BlockPos(range, range, range)), pos.offset(new BlockPos(range, range, range)))
        );*/
        Camera renderInfo = Minecraft.getInstance().gameRenderer.getMainCamera();
        PoseStack stack = context.matrixStack();
        Minecraft client = Minecraft.getInstance();
        MultiBufferSource.BufferSource bufferSource = client.renderBuffers().bufferSource();

            System.out.println(renderInfo.rotation().x + " " + renderInfo.rotation().y + " " + renderInfo.rotation().z + " " + renderInfo.rotation().w);
            float size = 0.04f;
            stack.pushPose();

            stack.translate(-renderInfo.getPosition().x + entity.position().x, -renderInfo.getPosition().y + entity.position().y + entity.getBbHeight() * 1.5, -renderInfo.getPosition().z + entity.position().z);
            stack.mulPoseMatrix(new Matrix4f().rotation(renderInfo.rotation().invert()));
            stack.scale(-size, -size, -size);
            float dunnosize = -client.font.width(text) / 2f;
            client.font.drawInBatch(text, dunnosize, 0, 0xFFDF5050, true,
                    stack.last().pose(), Minecraft.getInstance().renderBuffers().bufferSource(), Font.DisplayMode.NORMAL,
                    0, 100);
            stack.popPose();
            Minecraft.getInstance().renderBuffers().bufferSource().endBatch();




}
