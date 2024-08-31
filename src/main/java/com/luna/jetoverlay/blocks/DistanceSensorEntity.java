package com.luna.jetoverlay.blocks;

import com.luna.jetoverlay.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class DistanceSensorEntity extends BlockEntity {

	public int range = 5;
	public int redstoneValue = 0;
	public boolean onlyIncludePlayers = false;

	public DistanceSensorEntity(BlockPos pos, BlockState blockState) {
		super(ModItems.DISTANCE_SENSOR_ENTITY, pos, blockState);
	}

	public static <T extends BlockEntity> void tick(Level __level, BlockPos __blockPos, BlockState __blockState, T __entity) {
		if (!(__entity instanceof DistanceSensorEntity sensorEntity))
			return;

		var rangeVec = new BlockPos(sensorEntity.range, sensorEntity.range, sensorEntity.range);
		AABB collisionAabb = new AABB(__blockPos.subtract(rangeVec), __blockPos.offset(rangeVec));

		List<LivingEntity> nearby =
				__level.getNearbyEntities(LivingEntity.class, TargetingConditions.forNonCombat(), null, collisionAabb);

		double distanceFraction = sensorEntity.range;
		for (var ent : nearby) {
			if (sensorEntity.onlyIncludePlayers && !(ent instanceof Player))
				continue;

			double distance = ent.getPosition(0).distanceTo(__blockPos.getCenter());

			if (distance < sensorEntity.range) {
				distanceFraction = Math.min(distanceFraction, distance);
			}
		}
		distanceFraction /= sensorEntity.range;

		int newRedstoneValue = (int) ((1.0 - distanceFraction) * 15);
		if (newRedstoneValue != sensorEntity.redstoneValue) {
			sensorEntity.redstoneValue = newRedstoneValue;
			__level.blockUpdated(__blockPos, __blockState.getBlock());
		}
	}

	@Override
	public void load(CompoundTag __nbt) {
		super.load(__nbt);
		if (__nbt.contains("range")) {
			range = __nbt.getInt("range");
		}
		if (__nbt.contains("onlyPlayers")) {
			onlyIncludePlayers = __nbt.getBoolean("onlyPlayers");
		}
	}

	@Override
	public void saveAdditional(CompoundTag __nbt) {
		super.saveAdditional(__nbt);
		// Save the current value of the number to the nbt
		__nbt.putInt("range", range);
		__nbt.putBoolean("onlyPlayers", onlyIncludePlayers);
	}

}
