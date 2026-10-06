package com.kingokksa.noput.client;

import com.kingokksa.noput.config.Config;
import com.kingokksa.noput.config.InterceptMode;
import com.kingokksa.noput.network.NoJbPutNetwork;
import com.kingokksa.noput.util.ModState;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public final class ClientToggle {

    private ClientToggle() {
    }

    public static void cycle() {
        if (!Config.masterSwitch()) {
            message("message.nojbput.disabled");
            return;
        }
        if (!InterceptMode.anyAvailable()) {
            message("message.nojbput.none");
            return;
        }
        setMode(ModState.getClientMode().next());
    }

    public static void setMode(InterceptMode mode) {
        ModState.setClientMode(mode);
        NoJbPutNetwork.sendToServer(mode);
        message("message.nojbput.mode", Component.translatable(langKey(mode)));
    }

    public static String langKey(InterceptMode mode) {
        return switch (mode) {
            case BLOCK_PLACEMENT -> "gui.nojbput.mode.placement";
            case BLOCK_USAGE -> "gui.nojbput.mode.usage";
            case BLOCK_ALL -> "gui.nojbput.mode.all";
            case ALLOW -> "gui.nojbput.mode.allow";
        };
    }

    private static void message(String key, Object... args) {
        var player = Minecraft.getInstance().player;
        if (player != null) {
            player.displayClientMessage(Component.translatable(key, args), true);
        }
    }
}
