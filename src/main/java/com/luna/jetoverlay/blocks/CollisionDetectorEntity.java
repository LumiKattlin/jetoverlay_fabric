package com.luna.jetoverlay.blocks;

import com.luna.jetoverlay.ModItems;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.Objects;

public class CollisionDetectorEntity extends BlockEntity {
	public int _redstonePower = 0;
	public int width = 0;
	public int range = 3;

	public CollisionDetectorEntity(BlockPos pos, BlockState blockState) {
		super(ModItems.COLLISION_DETECTOR_ENTITY, pos, blockState);
	}

	static Pair<BlockPos, BlockPos> getDetectionMinMax(BlockPos __at, Direction __dir, int __length, int __thickness) {
		Vec3i normal = __dir.getNormal();
		Vec3i absNormal = new Vec3i(Math.abs(normal.getX()), Math.abs(normal.getY()), Math.abs(normal.getZ()));
		Vec3i facingAway = new Vec3i(1, 1, 1).subtract(absNormal).multiply(__thickness);

		Vec3i min = __at.subtract(facingAway).offset(normal);
		Vec3i max = __at.offset(facingAway).relative(__dir, __length);

		return new Pair<>(new BlockPos(min.getX(), min.getY(), min.getZ()),
				new BlockPos(max.getX(), max.getY(), max.getZ()));
	}

	public static <T extends BlockEntity> void tick(Level __level, BlockPos __blockPos, BlockState __blockState, T __entity) {
		// Only update every 4 game ticks
		if (__level.getGameTime() % 4 == 0) {
			return;
		}

		if (!(__entity instanceof CollisionDetectorEntity collisionBlock)) {
			return;
		}

		boolean blockDetected = false;
		Direction direction = __blockState.getValue(BlockStateProperties.HORIZONTAL_FACING);

		Pair<BlockPos, BlockPos> minMax = getDetectionMinMax(__blockPos, direction, collisionBlock.range, collisionBlock.width);

		// Iterate over all positions within the bounding box
		for (BlockPos pos : BlockPos.betweenClosed(minMax.getFirst(), minMax.getSecond())) {
			BlockState blockState = __level.getBlockState(pos);
			if (!blockState.isAir()) { // Check if there's a block (not air)
				blockDetected = true;
			}
		}

		int newRedstonePower = blockDetected ? 15 : 0;
		if (newRedstonePower != collisionBlock._redstonePower) {
			collisionBlock._redstonePower = newRedstonePower;
			Objects.requireNonNull(__level).blockUpdated(__blockPos, __blockState.getBlock());
		}
	}

	@Override
	public void load(CompoundTag __nbt) {
		super.load(__nbt);
		if (__nbt.contains("range"))
			range = __nbt.getInt("range");
		if (__nbt.contains("width"))
			width = __nbt.getInt("width");
	}

	@Override
	public void saveAdditional(CompoundTag __nbt) {
		super.saveAdditional(__nbt);
		// Save the current value of the number to the nbt
		__nbt.putInt("range", range);
		__nbt.putInt("width", width);
	}

}
