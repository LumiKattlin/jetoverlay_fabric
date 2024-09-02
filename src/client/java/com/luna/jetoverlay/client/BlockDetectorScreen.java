package com.luna.jetoverlay.client;

import com.luna.jetoverlay.ModNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class BlockDetectorScreen extends Screen {

	public static final ResourceLocation _background = new ResourceLocation("jetoverlay", "textures/block_detector_ui.png");

	int _range;
	int _maxRange = 10;
	int _width = 0;
	int _maxWidth = 5;
	BlockPos _blockDetectorPos;

	BlockDetectorSlider _rangeSlider = new BlockDetectorSlider(0, 0, 110, 20,
			_maxRange, 1, 0, "Collider range: ", this);
	BlockDetectorSlider _widthSlider = new BlockDetectorSlider(0, 0, 110, 20,
			_maxWidth, 0, 1, "Collider width: ", this);

	public BlockDetectorScreen(BlockPos __screenBlock, int __range, int __width) {
		super(Component.literal("Block Detector"));
		_blockDetectorPos = __screenBlock;
		_range = __range;
		_width = __width;
	}

	@Override
	protected void init() {
		int leftPos = (this.width - 256) / 2;
		int topPos = (this.height - 50) / 2;

		_rangeSlider.setPosition(leftPos + 10, topPos + 20);
		_widthSlider.setPosition(leftPos + 130, topPos + 20);

		_rangeSlider.update(_range);
		_widthSlider.update(_width);
		addRenderableWidget(_rangeSlider);
		addRenderableWidget(_widthSlider);
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		int leftPos = (this.width - 256) / 2;
		int topPos = (this.height - 50) / 2;

		guiGraphics.blit(_background, leftPos, topPos, 0, 0, 0, 256, 50, 256, 50);

		guiGraphics.drawString(Minecraft.getInstance().font, getTitle(), leftPos + 10,  topPos + 6, 0x202020, false);

		super.render(guiGraphics, mouseX, mouseY, partialTick);
	}

	public void setRange(int __value, int __id) {
		if (__id == 0) {
			if (_range == __value)
				return;
			_range = __value;

			FriendlyByteBuf buffer = PacketByteBufs.create();
			buffer.writeBlockPos(_blockDetectorPos);
			buffer.writeInt(_range);

			ClientPlayNetworking.send(ModNetworking.BLOCK_DETECTOR_SET_RANGE_PACKET_ID, buffer);
		}
		else {
			if (_width == __value)
				return;
			_width = __value;

			FriendlyByteBuf buffer = PacketByteBufs.create();
			buffer.writeBlockPos(_blockDetectorPos);
			buffer.writeInt(_width);

			ClientPlayNetworking.send(ModNetworking.BLOCK_DETECTOR_SET_THICKNESS_PACKET_ID, buffer);
		}
	}

	private static class BlockDetectorSlider extends AbstractSliderButton {
		BlockDetectorScreen _parent;
		int _valueRange;
		int _maxValue;
		int _minValue;
		int _id;
		String _text;

		private int getIntValue() {
			return (int) Math.round(value * _valueRange);
		}

		public BlockDetectorSlider(int x, int y, int width, int height,
		                           int __maxValue, int __minValue, int __id, String __text, BlockDetectorScreen parent) {
			super(x, y, width, height, Component.literal(""), 0);
			_parent = parent;
			_valueRange = __maxValue - _minValue;
			_maxValue = __maxValue;
			_minValue = __minValue;
			_text = __text;
			_id = __id;
		}

		public void update(int __value) {
			_valueRange = _maxValue - _minValue;
			value = (double) (__value - _minValue) / _valueRange;
			updateMessage();
		}

		@Override
		protected void updateMessage() {
			if(_id == 0) {
				setMessage(Component.literal(_text + (getIntValue() + _minValue)));
			}
			else {
				setMessage(Component.literal(_text + (getIntValue() + 1)));
			}
			
		}

		@Override
		protected void applyValue() {
			value = (float) getIntValue() / (float) _valueRange;
			_parent.setRange((int) (value * _valueRange) + _minValue, _id);
		}
	}
}
