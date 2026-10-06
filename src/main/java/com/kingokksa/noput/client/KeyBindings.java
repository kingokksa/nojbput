package com.kingokksa.noput.client;

import com.kingokksa.noput.NoJbPut;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;

import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = NoJbPut.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class KeyBindings {

    public static final String KEY_CATEGORY = "key.category.nojbput";
    public static final String KEY_TOGGLE = "key.nojbput.toggle";

    private static KeyMapping toggleKey;

    private KeyBindings() {
    }

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        toggleKey = new KeyMapping(KEY_TOGGLE, KeyConflictContext.IN_GAME,
                InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_ALT, KEY_CATEGORY);
        event.register(toggleKey);
    }

    public static KeyMapping toggleKey() {
        return toggleKey;
    }
}
