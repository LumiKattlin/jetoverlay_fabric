package com.luna.jetoverlay.screens;

import com.luna.jetoverlay.CameraRotationDirection;
import com.luna.jetoverlay.JetOverlay;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class GogglesReceiverScreenHandler extends AbstractContainerMenu {
	private final Container blockInventory;
	public BlockPos receiverPosition = BlockPos.ZERO;
	public CameraRotationDirection receiverDirection = CameraRotationDirection.RIGHT;


	//This constructor gets called on the client when the server wants it to open the screenHandler,
	//The client will call the other constructor with an empty Inventory and the screenHandler will automatically
	//sync this empty inventory with the inventory on the server.
	public GogglesReceiverScreenHandler(int syncId, Inventory playerInventory, FriendlyByteBuf buf) {
		this(syncId, playerInventory, new SimpleContainer(1));

		if (buf != null) {
			receiverPosition = buf.readBlockPos();
			receiverDirection = CameraRotationDirection.values()[buf.readInt()];
			JetOverlay.LOGGER.info("Opened goggles menu for block at {}", receiverPosition);
		}
	}

	//This constructor gets called from the BlockEntity on the server without calling the other constructor first, the server knows the inventory of the container
	//and can therefore directly provide it as an argument. This inventory will then be synced to the client.
	public GogglesReceiverScreenHandler(int syncId, Inventory playerInventory, Container inventory) {
		super(JetOverlay.GOGGLES_RECEIVER_SCREEN_HANDLER, syncId);
		assert(inventory.getContainerSize() == 1);
		blockInventory = inventory;
		//some inventories do custom logic when a player opens it.
		blockInventory.startOpen(playerInventory.player);

		//This will place the slot in the correct locations for a 3x3 Grid. The slots exist on both server and client!
		//This will not render the background of the slots however, this is the Screens job
		int m;
		int l;
		//Our inventory
		addSlot(new Slot(inventory, 0, 19, 17));

		//The player inventory
		for (m = 0; m < 3; ++m) {
			for (l = 0; l < 9; ++l) {
				addSlot(new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 84 + m * 18));
			}
		}
		//The player Hotbar
		for (m = 0; m < 9; ++m) {
			addSlot(new Slot(playerInventory, m, 8 + m * 18, 142));
		}

	}

	@Override
	public boolean stillValid(Player __player) {
		return true;
	}

	// Shift + Player Inv Slot
	@Override
	public @NotNull ItemStack quickMoveStack(Player player, int invSlot) {
		ItemStack newStack = ItemStack.EMPTY;
		Slot slot = slots.get(invSlot);
		if (slot.hasItem()) {
			ItemStack originalStack = slot.getItem();
			newStack = originalStack.copy();
			if (invSlot < blockInventory.getContainerSize()) {
				if (!moveItemStackTo(originalStack, blockInventory.getContainerSize(), this.slots.size(), true)) {
					return ItemStack.EMPTY;
				}
			}
			else if (!moveItemStackTo(originalStack, 0, blockInventory.getContainerSize(), false)) {
				return ItemStack.EMPTY;
			}

			if (originalStack.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			}
			else {
				slot.setChanged();
			}
		}

		return newStack;
	}
}