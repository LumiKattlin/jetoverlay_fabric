package com.luna.jetoverlay;

import com.luna.jetoverlay.armor.JetGoggles;
import com.luna.jetoverlay.blocks.RotationToRedstoneEntity;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;

public class ModNetworking {
	public static final ResourceLocation LINK_GOGGLES_PACKET_ID = new ResourceLocation("jetgoggles", "packets/link_goggles");
	public static final ResourceLocation SET_BLOCK_DIRECTION_PACKET_ID = new ResourceLocation("jetgoggles", "packets/block_direction");

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
		player.setItemSlot(EquipmentSlot.HEAD, ModItems.JET_GOGGLES.getDefaultInstance());
		receiverEntity._boundDirection = direction;
		JetOverlay.LOGGER.info("New block direction: " + direction);
		receiverEntity.setChanged();
		
	}

	public static void initialize() {
		ServerPlayNetworking.registerGlobalReceiver(LINK_GOGGLES_PACKET_ID, (server, player, handler, buf, responseSender) -> {
			BlockPos pos = buf.readBlockPos();
			// Receiving packets happens on a different thread.
			// Make sure we're on the main server thread before accessing any of the block entity stuff.
			server.execute(() -> handleLinkPacket(pos, player));
		});

		ServerPlayNetworking.registerGlobalReceiver(SET_BLOCK_DIRECTION_PACKET_ID, (server, player, handler, buf, responseSender) -> {
			BlockPos pos = buf.readBlockPos();
			CameraRotationDirection direction = CameraRotationDirection.values()[buf.readInt()];
			// Receiving packets happens on a different thread.
			// Make sure we're on the main server thread before accessing any of the block entity stuff.
			server.execute(() -> handleSetDirectionPacket(pos, direction, player));
		});
	}
}
