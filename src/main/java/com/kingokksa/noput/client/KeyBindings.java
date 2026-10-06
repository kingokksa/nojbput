package com.kingokksa.noput.client;

import com.kingokksa.noput.NoJbPut;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = NoJbPut.MODID, value = Dist.CLIENT, bus = Bus.MOD)
public final class KeyBindings {

    public static final String KEY_CATEGORY = "key.category.nojbput";
    public static final String KEY_TOGGLE = "key.nojbput.toggle";

    private static KeyBinding toggleKey;

    private KeyBindings() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        toggleKey = new KeyBinding(KEY_TOGGLE, InputMappings.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_ALT, KEY_CATEGORY);
        toggleKey.setKeyConflictContext(KeyConflictContext.IN_GAME);
        ClientRegistry.registerKeyBinding(toggleKey);
    }

    public static KeyBinding toggleKey() {
        return toggleKey;
    }
}
