package com.luna.jetoverlay;

import com.luna.jetoverlay.screens.GogglesReceiverScreenHandler;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JetOverlay implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("jetoverlay");

	public static final MenuType<GogglesReceiverScreenHandler> GOGGLES_RECEIVER_SCREEN_HANDLER
			= new ExtendedScreenHandlerType<>(GogglesReceiverScreenHandler::new);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		// ScreenHandlerRegistry;
		Registry.register(BuiltInRegistries.MENU, new ResourceLocation("jetoverlay", "redstoneoutputter"), GOGGLES_RECEIVER_SCREEN_HANDLER);
		ModItems.initialize();
		ModNetworking.initialize();
	}
}