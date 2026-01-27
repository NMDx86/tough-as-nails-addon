package com.example.tanaddon;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ToughAsNailsAddon implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("tan-addon");

	@Override
	public void onInitialize() {
		LOGGER.info("TAN Addon has been loaded successfully!");
	}
}