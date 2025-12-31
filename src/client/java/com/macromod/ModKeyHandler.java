package com.macromod;


import com.macromod.macros.MiningMacro;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class ModKeyHandler {

    private static boolean miningMacroActive = false;

        
    public static void register() {
        // Register the keybind
        

        // Listen to client tick events
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (ModKeybinds.TOGGLE_MINING.wasPressed()) {
                miningMacroActive = !miningMacroActive; // flip the toggle
                
            }

            // Apply the action if toggle is enabled
            if (miningMacroActive && client.player != null) {
                MiningMacro.update();
                client.player.setSprinting(true);
            }
        });

    }

    
}
