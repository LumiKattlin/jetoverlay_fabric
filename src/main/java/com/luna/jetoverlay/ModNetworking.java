package com.luna.jetoverlay;

import com.luna.jetoverlay.armor.JetGoggles;
import com.luna.jetoverlay.blocks.DistanceSensor;
import com.luna.jetoverlay.blocks.DistanceSensorEntity;
import com.luna.jetoverlay.blocks.RotationToRedstoneEntity;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class ModNetworking {
	public static final ResourceLocation LINK_GOGGLES_PACKET_ID = new ResourceLocation("jetgoggles", "packets/link_goggles");
	public static final ResourceLocation SET_ROTATION_BLOCK_DIRECTION_PACKET_ID = new ResourceLocation("jetgoggles", "packets/block_direction");
	public static final ResourceLocation OPEN_SENSOR_PACKET_ID = new ResourceLocation("jetgoggles", "packets/open_sensor");
	public static final ResourceLocation SET_SENSOR_RANGE_PACKET_ID = new ResourceLocation("jetgoggles", "packets/sensor_range");
	public static final ResourceLocation SENSOR_INCLUDE_PLAYERS_PACKET_ID = new ResourceLocation("jetgoggles", "packets/sensor_include_players");

	private static void handleLinkPacket(BlockPos pos, ServerPlayer player) {
		var entity = player.level().getBlockEntity(pos);
		if (!(entity instanceof RotationToRedstoneEntity receiverEntity)) {
			return;
		}

		if (receiverEntity._currentScreen == null) {
			return;
		}

		var blockSlot = receiverEntity._currentScreen.getSlot(0);

		if (JetGoggles.itemHasBlock(blockSlot.getItem(), pos))
			JetGoggles.itemRemoveBlock(blockSlot.getItem(), pos);
		else
			JetGoggles.itemAddBlock(blockSlot.getItem(), pos);

		blockSlot.setChanged();
	}

	private static void handleSetDirectionPacket(BlockPos pos, CameraRotationDirection direction, ServerPlayer player) {
		var entity = player.level().getBlockEntity(pos);
		if (!(entity instanceof RotationToRedstoneEntity receiverEntity)) {
			return;
		}

		receiverEntity._boundDirection = direction;
		JetOverlay.LOGGER.info("New block direction: " + direction);
		receiverEntity.setChanged();
	}

	private static void handleSetSensorRangePacket(BlockPos pos, int newRange, ServerPlayer player) {
		if (newRange > DistanceSensor.MAX_RANGE)
			return;

		var entity = player.level().getBlockEntity(pos);
		if (!(entity instanceof DistanceSensorEntity sensorEntity)) {
			return;
		}

		JetOverlay.LOGGER.info("new block range: " + newRange);

		sensorEntity.range = newRange;
		sensorEntity.setChanged();
	}
	private static void handleSensorIncludePlayersPacket(BlockPos pos, boolean newValue, ServerPlayer player) {
		var entity = player.level().getBlockEntity(pos);
		if (!(entity instanceof DistanceSensorEntity sensorEntity)) {
			return;
		}

		JetOverlay.LOGGER.info("Sensor includes players: " + newValue);

		sensorEntity.onlyIncludePlayers = newValue;
		sensorEntity.setChanged();
	}

	public static void initialize() {
		ServerPlayNetworking.registerGlobalReceiver(LINK_GOGGLES_PACKET_ID, (server, player, handler, buf, responseSender) -> {
			BlockPos pos = buf.readBlockPos();
			// Receiving packets happens on a different thread.
			// Make sure we're on the main server thread before accessing any of the block entity stuff.
			server.execute(() -> handleLinkPacket(pos, player));
		});

		ServerPlayNetworking.registerGlobalReceiver(SET_ROTATION_BLOCK_DIRECTION_PACKET_ID, (server, player, handler, buf, responseSender) -> {
			BlockPos pos = buf.readBlockPos();
			CameraRotationDirection direction = CameraRotationDirection.values()[buf.readInt()];
			// Receiving packets happens on a different thread.
			// Make sure we're on the main server thread before accessing any of the block entity stuff.
			server.execute(() -> handleSetDirectionPacket(pos, direction, player));
		});

		ServerPlayNetworking.registerGlobalReceiver(SET_SENSOR_RANGE_PACKET_ID, (server, player, handler, buf, responseSender) -> {
			BlockPos pos = buf.readBlockPos();

			int range = buf.readInt();
			// Receiving packets happens on a different thread.
			// Make sure we're on the main server thread before accessing any of the block entity stuff.
			server.execute(() -> handleSetSensorRangePacket(pos, range, player));
		});

		ServerPlayNetworking.registerGlobalReceiver(SENSOR_INCLUDE_PLAYERS_PACKET_ID, (server, player, handler, buf, responseSender) -> {
			BlockPos pos = buf.readBlockPos();

			boolean include = buf.readBoolean();
			// Receiving packets happens on a different thread.
			// Make sure we're on the main server thread before accessing any of the block entity stuff.
			server.execute(() -> handleSensorIncludePlayersPacket(pos, include, player));
		});
	}
}
