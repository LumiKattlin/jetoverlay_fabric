package com.luna.jetoverlay;

import com.luna.jetoverlay.armor.JetGoggles;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import org.intellij.lang.annotations.Identifier;

public class ModItems {

    //public static final Item jetgoggles = register(new ArmorItem(JetGoggles.INSTANCE, ArmorItem.Type.HELMET, new Item.Properties()), "jet_goggles");

    /*public static Item register(Item item, String id) {
      //  return Registry.register(Registries.ITEM.)
    }*/
    public static void initialize() {
       // ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register((itemgroup) -> itemgroup.add(ModItems.jetgoggles));
    }

}
