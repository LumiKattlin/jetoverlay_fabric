package com.luna.jetoverlay.blocks;

import com.luna.jetoverlay.ModItems;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class DistanceSensorEntity extends BaseContainerBlockEntity
        implements ExtendedScreenHandlerFactory, MenuProvider {
    
    public DistanceSensorEntity (BlockPos pos, BlockState blockState) {
        super(ModItems.DISTANCE_SENSOR_ENTITY, pos, blockState);
    }
    
    @Override
    protected Component getDefaultName () {
        return Component.literal("Distance sensor");
    }
    
    @Override
    protected AbstractContainerMenu createMenu (int containerId, Inventory inventory) {
        return null;
    }
    
    @Override
    public int getContainerSize () {
        return 0;
    }
    
    @Override
    public boolean isEmpty () {
        return false;
    }
    
    @Override
    public ItemStack getItem (int slot) {
        return null;
    }
    
    @Override
    public ItemStack removeItem (int slot, int amount) {
        return null;
    }
    
    @Override
    public ItemStack removeItemNoUpdate (int slot) {
        return null;
    }
    
    @Override
    public void setItem (int slot, ItemStack stack) {
    
    }
    
    @Override
    public boolean stillValid (Player player) {
        return false;
    }
    
    @Override
    public void clearContent () {
    
    }
    
    @Override
    public void writeScreenOpeningData (ServerPlayer player, FriendlyByteBuf buf) {
    
    }
}
