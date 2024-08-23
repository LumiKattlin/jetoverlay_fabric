package com.luna.jetoverlay.blocks;

import com.luna.jetoverlay.ModItems;
import com.luna.jetoverlay.screens.GogglesReceiverScreenHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RotationToRedstoneEntity extends BaseContainerBlockEntity implements MenuProvider {
    public RotationToRedstoneEntity(BlockPos pos, BlockState blockState) {
        super(ModItems.JET_GOGGLES_RECEIVER_ENTITY, pos, blockState);
    }

    ItemStack insertedItem = ItemStack.EMPTY;

    @Override
    public @NotNull Component getDisplayName() {
        return Component.literal("Goggles Receiver");
    }

    @Override
    protected @NotNull Component getDefaultName() {
        return getDisplayName();
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new GogglesReceiverScreenHandler(i, inventory);
    }

    @Override
    protected @NotNull AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new GogglesReceiverScreenHandler(containerId, inventory);
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public @NotNull ItemStack getItem(int slot) {
        return insertedItem;
    }

    @Override
    public @NotNull ItemStack removeItem(int slot, int amount) {
        insertedItem.setCount(insertedItem.getCount() - amount);
        return insertedItem;
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int slot) {
        insertedItem = ItemStack.EMPTY;
        return insertedItem;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        insertedItem = stack;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        insertedItem = ItemStack.EMPTY;
    }
}
