package com.kingokksa.noput.client;

import org.lwjgl.glfw.GLFW;

import com.kingokksa.noput.NoJbPut;
import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** 快捷键注册（mod 总线）。 */
@Mod.EventBusSubscriber(modid = NoJbPut.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class KeyBindings {

    public static final String KEY_CATEGORY = "key.category.nojbput";
    public static final String KEY_TOGGLE = "key.nojbput.toggle";

    private static KeyMapping toggleKey;

    private KeyBindings() {
    }

    public static KeyMapping toggleKey() {
        return toggleKey;
    }

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        toggleKey = new KeyMapping(KEY_TOGGLE, KeyConflictContext.IN_GAME,
                InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_ALT, KEY_CATEGORY);
        event.register(toggleKey);
    }
}
