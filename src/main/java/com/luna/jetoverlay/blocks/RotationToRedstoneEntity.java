package com.luna.jetoverlay.blocks;

import com.luna.jetoverlay.CameraRotationDirection;
import com.luna.jetoverlay.ModItems;
import com.luna.jetoverlay.screens.GogglesReceiverScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RotationToRedstoneEntity extends BaseContainerBlockEntity implements ExtendedScreenHandlerFactory, MenuProvider {
    public RotationToRedstoneEntity(BlockPos pos, BlockState blockState) {
        super(ModItems.JET_GOGGLES_RECEIVER_ENTITY, pos, blockState);
    }

    ItemStack insertedItem = ItemStack.EMPTY;
    public GogglesReceiverScreenHandler _currentScreen = null;
    public CameraRotationDirection _boundDirection;

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
        _currentScreen = new GogglesReceiverScreenHandler(i, inventory, this);
        return _currentScreen;
    }

    @Override
    protected @NotNull AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        _currentScreen = new GogglesReceiverScreenHandler(containerId, inventory, (FriendlyByteBuf) null);
        return _currentScreen;
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
        if (slot == 0)
            return insertedItem;
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull ItemStack removeItem(int slot, int amount) {
        ItemStack newStack = insertedItem.copy();
        newStack.setCount(amount);
        insertedItem.setCount(insertedItem.getCount() - amount);
        if (insertedItem.getCount() <= 0) {
            insertedItem = ItemStack.EMPTY;
        }
        return newStack;
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int slot) {
        insertedItem = ItemStack.EMPTY;
        return insertedItem;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot == 0)
            insertedItem = stack;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void writeScreenOpeningData(ServerPlayer serverPlayerEntity, FriendlyByteBuf packetByteBuf) {
        //The pos field is a public field from BlockEntity
        packetByteBuf.writeBlockPos(getBlockPos());
        packetByteBuf.writeInt(_boundDirection.ordinal());
    }

    @Override
    public void clearContent() {
        insertedItem = ItemStack.EMPTY;
    }

    @Override
    public void load(CompoundTag __nbt) {
        super.load(__nbt);
        _boundDirection = CameraRotationDirection.values()[__nbt.getInt("Direction")];
    }

    @Override
    public void saveAdditional(CompoundTag __nbt) {
        super.saveAdditional(__nbt);
        // Save the current value of the number to the nbt
        __nbt.putInt("Direction", _boundDirection.ordinal());
    }
}
