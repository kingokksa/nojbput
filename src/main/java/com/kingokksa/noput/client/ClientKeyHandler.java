package com.kingokksa.noput.client;

import com.kingokksa.noput.NoJbPut;

import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = NoJbPut.MODID, value = Dist.CLIENT)
public final class ClientKeyHandler {

    private ClientKeyHandler() {
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        KeyMapping key = KeyBindings.toggleKey();
        if (key == null) {
            return;
        }
        while (key.consumeClick()) {
            ClientToggle.cycle();
        }
    }
}
