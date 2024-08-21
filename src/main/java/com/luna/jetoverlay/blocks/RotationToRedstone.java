package com.luna.jetoverlay.blocks;

import com.luna.jetoverlay.data.RedstoneEmitterData;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraft.world.level.redstone.Redstone;
import net.minecraft.world.phys.BlockHitResult;

public class RotationToRedstone extends Block{
    public static Direction G_boundDirection;
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
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!nbt.contains("EmitterTracker")) {
             nbt.putString("EmitterTracker", "emitter-" + pos.getX() + "-" + pos.getY());
             System.out.println("it works?");
             return InteractionResult.SUCCESS;
        }
        else {
            return InteractionResult.FAIL;
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {

    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if(nbt.contains("EmitterTracker")) {
            String _trackerValue = nbt.getString("EmitterTracker");
            String _trackerPosX = _trackerValue.split("-")[1];
            String _trackerPosY = _trackerValue.split("-")[2];
            ServerPlayNetworking.registerGlobalReceiver(G_blockPacketChannel, (server, player, handler, buf, responseSender) -> {
                if(Float.valueOf(_trackerPosX) == pos.getX() && Float.valueOf(_trackerPosY) == pos.getY()) {
                    System.out.println("It works ? ");
                }
            });
        }

    }

    public static boolean G_shouldUpdateSignal = false;
    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        direction = G_boundDirection != null ? G_boundDirection : Direction.SOUTH;
        if(G_shouldUpdateSignal) {
            G_shouldUpdateSignal = false;
            return 15;
        }
        return 0;
    }

    public static void SetSignalDirection(Direction __direction) {
        G_boundDirection = __direction != null ? __direction : Direction.NORTH;
    }

}
