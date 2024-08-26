package com.luna.jetoverlay;

import com.luna.jetoverlay.armor.JetGoggles;
import com.luna.jetoverlay.blocks.RotationToRedstone;
import com.luna.jetoverlay.blocks.RotationToRedstoneEntity;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModItems {

    public static final Item JET_GOGGLES = registerItem(new ArmorItem(JetGoggles.INSTANCE, ArmorItem.Type.HELMET,
                                                                      new Item.Properties().requiredFeatures(FeatureFlags.VANILLA)),
                                                        "jet_goggles");
    public static final Block JET_GOGGLES_RECEIVER = Registry.register(
            BuiltInRegistries.BLOCK,
            new ResourceLocation("jetoverlay", "redstoneoutputter"),
            new RotationToRedstone(FabricBlockSettings.create().strength(4f))
    );

    public static final BlockEntityType<RotationToRedstoneEntity> JET_GOGGLES_RECEIVER_ENTITY = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            new ResourceLocation("jetoverlay", "redstoneoutputter"),
            BlockEntityType.Builder.of(RotationToRedstoneEntity::new, JET_GOGGLES_RECEIVER).build(null)
    );

    public static Item registerItem(Item item, String id) {
        return Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("jetoverlay", id), item);
    }
    public static void initialize() {

       ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COMBAT).register((itemGroup) -> itemGroup.prepend((ModItems.JET_GOGGLES)));

       var receiverBlock = registerItem(new BlockItem(ModItems.JET_GOGGLES_RECEIVER, new Item.Properties()), "redstoneoutputter");

       ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.BUILDING_BLOCKS).register((itemGroup -> itemGroup.prepend(receiverBlock)));


    }

}
