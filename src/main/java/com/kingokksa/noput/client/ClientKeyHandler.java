package com.kingokksa.noput.client;

import com.kingokksa.noput.NoJbPut;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = NoJbPut.MODID, value = Dist.CLIENT)
public final class ClientKeyHandler {

    private ClientKeyHandler() {
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        KeyBinding key = KeyBindings.toggleKey();
        if (key == null) {
            return;
        }
        while (key.consumeClick()) {
            ClientToggle.cycle();
        }
    }
}
