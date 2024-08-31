package com.luna.jetoverlay.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class CollisionDetector extends Block implements EntityBlock {
    public CollisionDetector (Properties properties) {
        super(properties);
    }
    
    @Override
    public int getSignal (BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return super.getSignal(state, level, pos, direction);
    }
    
    @Override
    public @Nullable BlockEntity newBlockEntity (BlockPos pos, BlockState state) {
        return new CollisionDetectorEntity(pos, state);
    }
    
    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker (Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide())
            return null;
        return CollisionDetectorEntity::tick;
    }
}
