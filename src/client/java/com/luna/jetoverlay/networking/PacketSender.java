package com.luna.jetoverlay.networking;

import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.resources.ResourceLocation;

public class PacketSender {


    public static void SendPacket(String __message) {
        final ResourceLocation _redstoneEmitterChannel = new ResourceLocation("jetoverlay","redstone_emitter");
        ClientPlayNetworking.send(_redstoneEmitterChannel, PacketByteBufs.create().writeUtf(__message));
    }


}
