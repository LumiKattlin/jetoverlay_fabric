package com.luna.jetoverlay.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class CollisionDetectorScreen extends Screen {
    protected CollisionDetectorScreen (Component title) {
        super(title);
    }
    
    @Override
    public void render (GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
    
    }
    
    @Override
    public void renderBackground (GuiGraphics guiGraphics) {
        super.renderBackground(guiGraphics);
    }
}
