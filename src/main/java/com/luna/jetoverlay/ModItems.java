package com.luna.jetoverlay;

import com.luna.jetoverlay.armor.JetGoggles;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.S2CPlayChannelEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import org.intellij.lang.annotations.Identifier;

public class ModItems {

    public static final Item jetgoggles = register(new ArmorItem(JetGoggles.INSTANCE, ArmorItem.Type.HELMET, new Item.Properties()), "jet_goggles");
    public static Item register(Item item, String id) {

        return Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("jetoverlay", id), item);

    }
    public static void initialize() {
       ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COMBAT).register(( itemgroup) -> itemgroup.prepend((ModItems.jetgoggles)));

    }

}
