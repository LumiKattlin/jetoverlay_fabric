package com.luna.jetoverlay;

import com.luna.jetoverlay.armor.JetGoggles;
import com.luna.jetoverlay.blocks.RotationToRedstone;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;

public class ModItems {

    public static final Item JET_GOGGLES = register(new ArmorItem(JetGoggles.INSTANCE, ArmorItem.Type.HELMET, new Item.Properties()), "jet_goggles");
    public static final Block JET_GOGGLES_RECEIVER = Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation("jetoverlay", "redstoneoutputter"), new RotationToRedstone(FabricBlockSettings.create().strength(4f)));
    public static Item register(Item item, String id) {

        return Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("jetoverlay", id), item);

    }
    public static void initialize() {

       ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COMBAT).register((itemGroup) -> itemGroup.prepend((ModItems.JET_GOGGLES)));
       var whatever = Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("jetoverlay", "redstoneoutputter"), new BlockItem(ModItems.JET_GOGGLES_RECEIVER, new Item.Properties()));
       ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.BUILDING_BLOCKS).register((itemGroup -> itemGroup.prepend(whatever)));

    }

}
