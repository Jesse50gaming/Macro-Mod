package com.macromod;

import net.fabricmc.api.ClientModInitializer;


public class MacroModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ModKeybinds.register();
	}
}