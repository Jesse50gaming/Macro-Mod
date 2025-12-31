package com.macromod;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import org.lwjgl.glfw.GLFW;

public class ModKeybinds {

    public static KeyBinding TOGGLE_MINING;

    public static void register() {
        TOGGLE_MINING = KeyBindingHelper.registerKeyBinding(
            new KeyBinding(
                "key.mymod.toggle_mining", // translation key
                InputUtil.Type.KEYSYM,     // keyboard
                GLFW.GLFW_KEY_K,           // default key
                KeyBinding.Category.MISC         // category in controls menu
            )
        );
    }
}
