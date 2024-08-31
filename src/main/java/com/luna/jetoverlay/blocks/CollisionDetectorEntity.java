package com.luna.jetoverlay.blocks;

import com.luna.jetoverlay.ModItems;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Arrays;
import java.util.Objects;

public class CollisionDetectorEntity extends BlockEntity {
    public static byte _redstonePower = 0;
    public CollisionDetectorEntity (BlockPos pos, BlockState blockState) {
        super(ModItems.COLLISION_DETECTOR_ENTITY, pos, blockState);
    }
    static long _lastRotationUpdateTick;
    public static <T extends BlockEntity> void tick (Level __level, BlockPos __blockPos, BlockState __blockState, T __entity) {
        long currentTick = __level.getGameTime();
        System.out.println("yippe");
        if (currentTick != _lastRotationUpdateTick) {
            _lastRotationUpdateTick = currentTick;
            boolean blockDetected = false;
            Direction direction = Direction.SOUTH; // Replace with the desired direction
            
            AABB bb = new AABB(new BlockPos(__blockPos.getX(), __blockPos.getY(), __blockPos.getZ() + 1),
                               new BlockPos(0,0,0)).inflate(0,0,5);
            
            BlockPos minPos = new BlockPos((int)bb.minX, (int)bb.minY, (int)bb.minZ);
            BlockPos maxPos = new BlockPos((int)bb.maxX, (int)bb.maxY, (int)bb.maxZ);
            
            // Iterate over all positions within the bounding box
            for (BlockPos pos : BlockPos.betweenClosed(minPos, maxPos)) {
                BlockState blockState = __level.getBlockState(pos);
                if (!blockState.isAir()) { // Check if there's a block (not air)
                    blockDetected = true;
                }
                else {
                    blockDetected = false;
                }
            }
            
            if(blockDetected) {
                _redstonePower = 15;
                System.out.println("Block found");
            }
            else {
                _redstonePower = 0;
                System.out.println("Block not found");
            }
            Objects.requireNonNull(__level).blockUpdated(__blockPos, __blockState.getBlock());
            
            
        }
    }
    private static AABB createDirectionalBoundingBox(BlockPos pos, Direction direction) {
        double expansion = 2.0; // The length of the hitbox
        
        switch (direction) {
            case NORTH:
                return new AABB(
                        pos.getX() - 2, pos.getY() - 2, pos.getZ() - 2 + expansion,
                        pos.getX() + 2, pos.getY() + 2, pos.getZ() + 2
                );
            case SOUTH:
                return new AABB(
                        pos.getX() - 2, pos.getY() - 2, pos.getZ() - 2,
                        pos.getX() + 2, pos.getY() + 2, pos.getZ() - 2 - expansion
                );
            case WEST:
                return new AABB(
                        pos.getX() - 2 - expansion, pos.getY() - 2, pos.getZ() - 2,
                        pos.getX() + 2, pos.getY() + 2, pos.getZ() + 2
                );
            case EAST:
                return new AABB(
                        pos.getX() - 2, pos.getY() - 2, pos.getZ() - 2,
                        pos.getX() + 2 + expansion, pos.getY() + 2, pos.getZ() + 2
                );
            default:
                return new AABB(
                        pos.getX() - 2, pos.getY() - 2, pos.getZ() - 2,
                        pos.getX() + 2, pos.getY() + 2, pos.getZ() + 2
                );
        }
    }
    
    private static boolean detectBlocksInBoundingBox(Level level, AABB bb) {
        // Convert AABB to BlockPos range
        BlockPos minPos = new BlockPos((int)bb.minX, (int)bb.minY, (int)bb.minZ);
        BlockPos maxPos = new BlockPos((int)bb.maxX, (int)bb.maxY, (int)bb.maxZ);
        
        // Iterate over all positions within the bounding box
        for (BlockPos pos : BlockPos.betweenClosed(minPos, maxPos)) {
            BlockState blockState = level.getBlockState(pos);
            if (!blockState.isAir()) { // Check if there's a block (not air)
                return true; // Block detected
            }
        }
        return false; // No blocks detected
    }
}
