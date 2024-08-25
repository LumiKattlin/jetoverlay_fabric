package com.luna.jetoverlay.blocks;

import com.luna.jetoverlay.CameraRotationDirection;
import com.luna.jetoverlay.ModItems;
import com.luna.jetoverlay.armor.JetGoggles;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Objects;

public class RotationToRedstoneEntity extends BaseContainerBlockEntity
        implements ExtendedScreenHandlerFactory, MenuProvider {
    public RotationToRedstoneEntity(BlockPos pos, BlockState blockState) {
        super(ModItems.JET_GOGGLES_RECEIVER_ENTITY, pos, blockState);
    }

    ItemStack insertedItem = ItemStack.EMPTY;
    public GogglesReceiverScreenHandler _currentScreen = null;
    public CameraRotationDirection _boundDirection = CameraRotationDirection.RIGHT;
    private byte _redstoneValue = 0;
    private static long _lastRotationUpdateTick = 0;
    private static HashMap<Player, Vec2> _oldPlayerRotations = new HashMap<>();

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
        if (_boundDirection == null) {
            _boundDirection = CameraRotationDirection.RIGHT;
        }
    }

    @Override
    public void saveAdditional(CompoundTag __nbt) {
        super.saveAdditional(__nbt);
        // Save the current value of the number to the nbt
        __nbt.putInt("Direction", _boundDirection.ordinal());
    }

    private void setRedstoneValueFromRotation(float rotationValue) {
        byte newValue = (byte) Math.max(rotationValue / 5.0f, 0.0f);
        if (newValue == 0 && rotationValue > 0) {
            newValue = 1;
        }

        if (newValue == _redstoneValue)
            return;

        _redstoneValue = newValue;
        Objects.requireNonNull(getLevel()).blockUpdated(getBlockPos(), getBlockState().getBlock());
    }
    public byte getRedstoneValue() {
        return _redstoneValue;
    }

    private static void sendRotationToBlock(Level __level, BlockPos __block,  Vec2 __rotation) {
        var entity = __level.getBlockEntity(__block);
        if (entity == null)
            return;

        if (!(entity instanceof RotationToRedstoneEntity receiverEntity))
            return;

        switch (receiverEntity._boundDirection) {
	        case RIGHT -> {
                receiverEntity.setRedstoneValueFromRotation(-__rotation.y);
	        }
	        case LEFT -> {
                receiverEntity.setRedstoneValueFromRotation(__rotation.y);
	        }
	        case UP -> {
                receiverEntity.setRedstoneValueFromRotation(__rotation.x);
	        }
	        case DOWN -> {
                receiverEntity.setRedstoneValueFromRotation(-__rotation.x);
	        }
	        case NOTHING -> {
	        }
        }
    }

    private static void updateGlobalRotations(Level __level) {
        var players = __level.players();

        for (Player player : players) {
            var headItem = player.getInventory().getArmor(3);

            if (!headItem.is(ModItems.JET_GOGGLES))
                continue;

            Vec2 rotation = player.getRotationVector();

            if (!_oldPlayerRotations.containsKey(player)) {
                _oldPlayerRotations.put(player, rotation);
                continue;
            }

            Vec2 oldRotation = _oldPlayerRotations.get(player);
            _oldPlayerRotations.put(player, rotation);

            Vec2 difference = oldRotation.add(rotation.negated());

            for (BlockPos block : JetGoggles.itemGetBlocks(headItem)) {
                sendRotationToBlock(__level, block, difference);
            }
        }
    }

    public static <T extends BlockEntity> void tick(Level __level, BlockPos __blockPos, BlockState __blockState, T __entity) {
        long currentTick = __level.getGameTime();

        if (currentTick != _lastRotationUpdateTick) {
            _lastRotationUpdateTick = currentTick;
            updateGlobalRotations(__level);
        }
    }

}
