package com.luna.jetoverlay.client;

import com.luna.jetoverlay.CameraRotationDirection;
import com.luna.jetoverlay.ModItems;
import com.luna.jetoverlay.ModNetworking;
import com.luna.jetoverlay.armor.JetGoggles;
import com.luna.jetoverlay.screens.GogglesReceiverScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public class GogglesReceiverScreen extends AbstractContainerScreen<GogglesReceiverScreenHandler> {
	private static final ResourceLocation TEXTURE = new ResourceLocation("jetoverlay", "textures/receiver_ui.png");

	public GogglesReceiverScreen(GogglesReceiverScreenHandler __handler, Inventory __inventory, Component __title) {
		// The parameter is the title of the screen,
		// which will be narrated when you enter the screen.
		super(__handler, __inventory, __title);
	}

	private void updateButtonText() {
		ItemStack item = getMenu().getSlot(0).getItem();

		boolean isLinkedJetGoggles = false;

		if (item.is(ModItems.JET_GOGGLES)) {
			isLinkedJetGoggles = JetGoggles.itemGetBlocks(item).contains(getMenu().receiverPosition);
		}

		if (_isLinkable != isLinkedJetGoggles) {
			_isLinkable = isLinkedJetGoggles;
			linkButton.setMessage(isLinkedJetGoggles ? Component.literal("Unlink") : Component.literal("Link"));
		}
	}

	public Button linkButton;
	private boolean _isLinkable = false;

	public Button[] directionButtons;

	@Override
	protected void renderBg(GuiGraphics __graphics, float __delta, int __mouseX, int __mouseY) {
		renderTooltip(__graphics, __mouseX, __mouseY);

		int textureRes = 256;
		__graphics.blit(TEXTURE, leftPos, topPos, 0, 0, 0, textureRes, textureRes, textureRes, textureRes);
		updateButtonText();
	}

	@Override
	protected void init() {
		super.init();

		CameraRotationDirection selectedDirection = getMenu().receiverDirection;

		linkButton = Button.builder(Component.literal("Link"), button -> {
					ClientPlayNetworking.send(ModNetworking.LINK_GOGGLES_PACKET_ID, PacketByteBufs.create().writeBlockPos(getMenu().receiverPosition));
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
						getMenu().receiverDirection = i;
						clearWidgets();
						init();
						var buffer = PacketByteBufs.create();
						buffer.writeBlockPos(getMenu().receiverPosition);
						buffer.writeInt(i.ordinal());

						ClientPlayNetworking.send(ModNetworking.SET_ROTATION_BLOCK_DIRECTION_PACKET_ID, buffer);
					})
					.bounds(leftPos + buttonPosX, topPos + 9 + i.ordinal() * 17, buttonWidth, 16).build();

			addRenderableWidget(directionButtons[i.ordinal()]);
		}

		addRenderableWidget(linkButton);
	}
}