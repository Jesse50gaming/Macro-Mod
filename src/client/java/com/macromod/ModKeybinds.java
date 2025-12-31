package com.macromod;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import org.lwjgl.glfw.GLFW;

public class ModKeybinds {

    public static KeyBinding TOGGLE_MINING;

    public static void register() {
        TOGGLE_MINING = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.mymod.toggle_mining", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_P, KeyBinding.Category.MISC));
    }
}
