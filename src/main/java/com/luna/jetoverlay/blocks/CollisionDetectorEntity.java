package com.luna.jetoverlay.blocks;

import com.luna.jetoverlay.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class CollisionDetectorEntity extends BlockEntity {
    public CollisionDetectorEntity (BlockPos pos, BlockState blockState) {
        super(ModItems.COLLISION_DETECTOR_ENTITY, pos, blockState);
    }
    static long _lastRotationUpdateTick;
    public static <T extends BlockEntity> void tick (Level __level, BlockPos __blockPos, BlockState __blockState, T __entity) {
        long currentTick = __level.getGameTime();
        System.out.println("yippe");
        if (currentTick != _lastRotationUpdateTick) {
            _lastRotationUpdateTick = currentTick;
            AABB bb = AABB.ofSize(__blockPos.getCenter(), 5, 5, 5);
            var entities = __level.getEntities(null, bb);
            if(entities.size() > 0) {
                
            }
            
            
        }
    }
}
