package com.luna.jetoverlay.blocks;

import com.luna.jetoverlay.ModItems;
import com.luna.jetoverlay.armor.JetGoggles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class RotationToRedstone extends Block implements EntityBlock {
    public static Direction _boundDirection;
    public static final ResourceLocation G_blockPacketChannel = new ResourceLocation("jetoverlay", "redstone_emitter");

    CompoundTag nbt = new CompoundTag();
    public RotationToRedstone(Properties properties) {
        super(properties);
    }

    //Runs every tick when the block is placed in the world
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {

    }

    @Override
    public @NotNull InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        var itemStack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            ((ServerPlayer) player).openMenu((RotationToRedstoneEntity) level.getBlockEntity(pos));
           // blockEntity.createMenu(0, player.getInventory(), player);
        }

        if (itemStack.getItem().equals(ModItems.JET_GOGGLES)) {
            if (JetGoggles.itemHasBlock(itemStack, pos))
                JetGoggles.itemRemoveBlock(itemStack, pos);
            else
                JetGoggles.itemAddBlock(itemStack, pos);

            return InteractionResult.SUCCESS;
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {

    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {

    }

    public static boolean _shouldUpdateSignal = false;

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        direction = _boundDirection != null ? _boundDirection : Direction.SOUTH;
        if(_shouldUpdateSignal) {
            _shouldUpdateSignal = false;
            return 15;
        }
        return 0;
    }

    public static void SetSignalDirection(Direction __direction) {
        _boundDirection = __direction != null ? __direction : Direction.NORTH;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RotationToRedstoneEntity(pos, state);
    }
}
