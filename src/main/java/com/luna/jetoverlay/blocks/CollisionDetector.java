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
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class CollisionDetector extends Block implements EntityBlock {
	public CollisionDetector(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH));
	}
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		return Objects.requireNonNull(super.getStateForPlacement(ctx))
				.setValue(BlockStateProperties.HORIZONTAL_FACING, ctx.getHorizontalDirection().getOpposite());
	}

	@Override
	public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
		return ((CollisionDetectorEntity) Objects.requireNonNull(level.getBlockEntity(pos)))._redstonePower;
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new CollisionDetectorEntity(pos, state);
	}

	@Override
	public @NotNull InteractionResult use(BlockState state, Level level, BlockPos pos,
	                                      Player player, InteractionHand hand, BlockHitResult hit) {
		if (!level.isClientSide()) {
			var buf = PacketByteBufs.create();
			buf.writeBlockPos(pos);
			buf.writeInt(((CollisionDetectorEntity) Objects.requireNonNull(level.getBlockEntity(pos))).range);
			buf.writeInt(((CollisionDetectorEntity) Objects.requireNonNull(level.getBlockEntity(pos))).width);
			ServerPlayNetworking.send((ServerPlayer) player, ModNetworking.OPEN_BLOCK_DETECTOR_PACKET_ID, buf);
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(BlockStateProperties.HORIZONTAL_FACING);
	}

	@Override
	public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
		if (level.isClientSide())
			return null;
		return CollisionDetectorEntity::tick;
	}


}
