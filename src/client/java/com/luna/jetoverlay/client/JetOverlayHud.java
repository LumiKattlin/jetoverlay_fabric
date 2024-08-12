package com.luna.jetoverlay.client;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector2i;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.List;

public class JetOverlayHud implements HudRenderCallback {
    boolean shouldDraw = false;
    @Override
    public void onHudRender(GuiGraphics drawContext, float tickDelta) {
        int y;
        int x;
        Minecraft client = Minecraft.getInstance();
        Camera camera = client.gameRenderer.getMainCamera();
        List<Entity> entities =
        if (client != null) {
            int width = client.getWindow().getGuiScaledWidth();
            int height = client.getWindow().getGuiScaledHeight();
            LocalPlayer player = Minecraft.getInstance().player;
            x = width / 2;
            y = height;

            if(shouldDraw)  {

            }

        }

    }
    public static Vector2i toScreenPosition(Vector3d worldPosition, LocalPlayer p) {

        GameRenderer ari = Minecraft.getInstance().gameRenderer;
        Vec3 camera_pos = ari.getMainCamera().getPosition();
        Quaternionf camera_rotation_conj = ari.getMainCamera().rotation();
        camera_rotation_conj.conjugate();

        Vector3f result3f = new Vector3f((float) (camera_pos.x - worldPosition.x),
                (float) (camera_pos.y - worldPosition.y),
                (float) (camera_pos.z - worldPosition.z));
        result3f.rotate(camera_rotation_conj);

        double fov = p.getFieldOfViewModifier() * (float)Minecraft.getInstance().options.fov().get();

        float half_height = (float) Minecraft.getInstance().getWindow().getHeight() / 2;
        float scale_factor = half_height / (result3f.z() * (float) Math.tan(Math.toRadians(fov / 2)));
        return new Vector2i((int) (-result3f.x * scale_factor), (int) (result3f.y * scale_factor));
    }

    public static boolean isScreenPositionVisible(Vector2i pos) {
        var screenSize = new Vector2i(
                Minecraft.getInstance().getWindow().getHeight(),
                Minecraft.getInstance().getWindow().getHeight());

        var screenHalfSize = screenSize.div(2);

        return pos.x < screenHalfSize.x && pos.x > -screenHalfSize.x
                && pos.y < screenHalfSize.y && pos.y > -screenHalfSize.y;
    }
}
