package com.luna.jetoverlay.data;

import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class RedstoneEmitterData {
    public static class EmitterStruct {
        public static Player G_playerName;
        public static float G_blockXCord;
        public static float G_blockYCord;
    }
    public static List<EmitterStruct> G_emitterValidationData = new ArrayList<>();

}
