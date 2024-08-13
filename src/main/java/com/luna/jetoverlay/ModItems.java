package com.luna.jetoverlay;

import com.luna.jetoverlay.armor.JetGoggles;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {

    public static final Item jetgoggles = register(new ArmorItem(JetGoggles.INSTANCE, ArmorItem.Type.HELMET, new Item.Settings()), "jet_goggles");

    public static Item register(Item item, String id) {
        Identifier itemid = new Identifier("jetoverlay", id);
        Item registeredItem = Registry.register(Registries.ITEM, itemid, item);

        return registeredItem;
    }
    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register((itemgroup) -> itemgroup.add(ModItems.jetgoggles));
    }

}
