package com.luna.jetoverlay.client;

import com.luna.jetoverlay.screens.GogglesReceiverScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

@Environment(EnvType.CLIENT)
public class GogglesReceiverScreen extends AbstractContainerScreen<GogglesReceiverScreenHandler> {
	private static final ResourceLocation TEXTURE = new ResourceLocation("jetoverlay", "textures/receiver_ui.png");

	public GogglesReceiverScreen(GogglesReceiverScreenHandler __handler, Inventory __inventory, Component __title) {
		// The parameter is the title of the screen,
		// which will be narrated when you enter the screen.
		super(__handler, __inventory, __title);
	}

	public Button button1;

	public Button[] directionButtons;

	@Override
	protected void renderBg(GuiGraphics __graphics, float __delta, int __mouseX, int __mouseY) {
		// render cool background here

		renderTooltip(__graphics, __mouseX, __mouseY);

		int textureRes = 256;
		__graphics.blit(TEXTURE, leftPos, topPos, 0, 0, 0, textureRes, textureRes, textureRes, textureRes);
	}

	CameraRotationDirection selectedDirection = CameraRotationDirection.RIGHT;

	@Override
	protected void init() {
		super.init();
		button1 = Button.builder(Component.literal("Link"), button -> {
					// Set position of the goggles here
				})
				.bounds(leftPos + 8, topPos + 40, 40, 16)
				.tooltip(Tooltip.create(Component.literal("Link Jet Goggles with this block")))
				.build();

		directionButtons = new Button[4];
		for (var i : CameraRotationDirection.values()) {
			if (i.ordinal() >= 4) {
				break;
			}

			boolean selected = selectedDirection == i;

			int buttonPosX = selected ? 116 : 120;
			int buttonWidth = selected ? 52 : 44;

			directionButtons[i.ordinal()] = Button.builder(Component.literal(i.name()), button -> {
						selectedDirection = i;
						clearWidgets();
						init();
					})
					.bounds(leftPos + buttonPosX, topPos + 9 + i.ordinal() * 17, buttonWidth, 16).build();

			addRenderableWidget(directionButtons[i.ordinal()]);
		}

		addRenderableWidget(button1);
	}
}