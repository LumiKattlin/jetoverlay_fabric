package com.luna.jetoverlay.blocks;

import com.luna.jetoverlay.ModNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class DistanceSensor extends Block implements EntityBlock {

	public static final int MAX_RANGE = 10;

	public DistanceSensor(Properties properties) {
		super(properties);
	}

	@Override
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
		super.onPlace(state, level, pos, oldState, movedByPiston);
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
		super.onRemove(state, level, pos, newState, movedByPiston);
	}

	@Override
	public @NotNull InteractionResult use(BlockState state, Level level, BlockPos pos,
	                                      Player player, InteractionHand hand, BlockHitResult hit) {
		if (!level.isClientSide()) {
			var buf = PacketByteBufs.create();
			buf.writeBlockPos(pos);
			buf.writeInt(((DistanceSensorEntity) Objects.requireNonNull(level.getBlockEntity(pos))).range);
			buf.writeBoolean(((DistanceSensorEntity) Objects.requireNonNull(level.getBlockEntity(pos))).onlyIncludePlayers);
			ServerPlayNetworking.send((ServerPlayer) player, ModNetworking.OPEN_SENSOR_PACKET_ID, buf);
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new DistanceSensorEntity(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		if (level.isClientSide())
			return null;
		return DistanceSensorEntity::tick;
	}

	@Override
	public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
		return ((DistanceSensorEntity) Objects.requireNonNull(level.getBlockEntity(pos))).redstoneValue;
	}
}
