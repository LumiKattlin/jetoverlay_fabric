package com.luna.jetoverlay;

import com.luna.jetoverlay.armor.JetGoggles;
import com.luna.jetoverlay.blocks.RotationToRedstone;
import com.luna.jetoverlay.blocks.RotationToRedstoneEntity;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.S2CPlayChannelEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.intellij.lang.annotations.Identifier;

public class ModItems {

    public static final Item jetgoggles = register(new ArmorItem(JetGoggles.INSTANCE, ArmorItem.Type.HELMET, new Item.Properties()), "jet_goggles");
    public static final Block EXAMPLE_BLOCK = Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation("jetoverlay", "redstoneoutputter"), new RotationToRedstone(FabricBlockSettings.create().strength(4f)));
    public static Item register(Item item, String id) {

        return Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("jetoverlay", id), item);

    }
    public static void initialize() {

       ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COMBAT).register(( itemgroup) -> itemgroup.prepend((ModItems.jetgoggles)));
       var whatever = Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("jetoverlay", "redstoneoutputter"), new BlockItem(ModItems.EXAMPLE_BLOCK , new Item.Properties()));
       ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.BUILDING_BLOCKS).register((itemgroup -> itemgroup.prepend(whatever)));

    }

}
