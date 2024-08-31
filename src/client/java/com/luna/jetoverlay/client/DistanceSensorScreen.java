package com.luna.jetoverlay.client;

import com.luna.jetoverlay.ModNetworking;
import com.luna.jetoverlay.blocks.DistanceSensor;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class DistanceSensorScreen extends Screen {

	int _maxValue = DistanceSensor.MAX_RANGE;
	int _value;
	boolean _onlyPlayersValue;
	BlockPos _distanceSensorBlock;
	public static final ResourceLocation _background = new ResourceLocation("jetoverlay", "textures/distance_sensor_ui.png");

	DistanceSensorSlider _slider = new DistanceSensorSlider(0, 0, 100, 20, this);
	Checkbox _onlyPlayersBox = new Checkbox(0, 0, 20, 20, Component.literal("Only detect players"), false);

	public DistanceSensorScreen(BlockPos __screenBlock, int __value, boolean onlyPlayers) {
		super(Component.literal("Distance Sensor"));
		_distanceSensorBlock = __screenBlock;
		_value = __value;
		_onlyPlayersValue = onlyPlayers;
	}

	@Override
	protected void init() {
		int leftPos = (this.width - 256) / 2;
		int topPos = (this.height - 50) / 2;

		_slider.setPosition(leftPos + 10, topPos + 20);
		_onlyPlayersBox.setPosition(leftPos + 122, topPos + 15);
		if (_onlyPlayersBox.selected() != _onlyPlayersValue) {
			_onlyPlayersBox.onPress();
		}
		_slider.update();
		addRenderableWidget(_slider);
		addRenderableWidget(_onlyPlayersBox);
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		int leftPos = (this.width - 256) / 2;
		int topPos = (this.height - 50) / 2;

		guiGraphics.blit(_background, leftPos, topPos, 0, 0, 0, 256, 50, 256, 50);

		guiGraphics.drawString(Minecraft.getInstance().font, getTitle(), leftPos + 10,  topPos + 6, 0x202020, false);

		super.render(guiGraphics, mouseX, mouseY, partialTick);

		if (_onlyPlayersBox.selected() != _onlyPlayersValue) {
			_onlyPlayersValue = _onlyPlayersBox.selected();

			FriendlyByteBuf buffer = PacketByteBufs.create();
			buffer.writeBlockPos(_distanceSensorBlock);
			buffer.writeBoolean(_onlyPlayersValue);

			ClientPlayNetworking.send(ModNetworking.SENSOR_INCLUDE_PLAYERS_PACKET_ID, buffer);
		}
	}

	public void setValue(int __value) {
		if (_value == __value)
			return;
		_value = __value;

		FriendlyByteBuf buffer = PacketByteBufs.create();
		buffer.writeBlockPos(_distanceSensorBlock);
		buffer.writeInt(_value);

		ClientPlayNetworking.send(ModNetworking.SET_SENSOR_RANGE_PACKET_ID, buffer);
	}

	private static class DistanceSensorSlider extends AbstractSliderButton {
		DistanceSensorScreen _parent;
		int _valueRange;

		private int getIntValue() {
			return (int) Math.round(value * _valueRange);
		}

		public DistanceSensorSlider(int x, int y, int width, int height, DistanceSensorScreen parent) {
			super(x, y, width, height, Component.literal(""), 0);
			_parent = parent;
			_valueRange = parent._maxValue - 1;
		}

		public void update() {
			updateMessage();
			_valueRange = _parent._maxValue - 1;
			value = (double) (_parent._value - 1) / _valueRange;
		}

		@Override
		protected void updateMessage() {
			setMessage(Component.literal("Block range: " + _parent._value));
		}

		@Override
		protected void applyValue() {
			value = (float) getIntValue() / (float) _valueRange;
			_parent.setValue((int) (value * _valueRange) + 1);
		}
	}
}
