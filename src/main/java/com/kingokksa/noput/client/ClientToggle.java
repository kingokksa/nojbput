package com.kingokksa.noput.client;

import com.kingokksa.noput.config.Config;
import com.kingokksa.noput.config.InterceptMode;
import com.kingokksa.noput.network.NoJbPutNetwork;
import com.kingokksa.noput.util.ModState;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

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
        message("message.nojbput.mode", new TranslationTextComponent(langKey(mode)));
    }

    public static String langKey(InterceptMode mode) {
        switch (mode) {
            case BLOCK_PLACEMENT:
                return "gui.nojbput.mode.placement";
            case BLOCK_USAGE:
                return "gui.nojbput.mode.usage";
            case BLOCK_ALL:
                return "gui.nojbput.mode.all";
            case ALLOW:
            default:
                return "gui.nojbput.mode.allow";
        }
    }

    private static void message(String key, Object... args) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            ITextComponent text = args.length == 0
                    ? new TranslationTextComponent(key)
                    : new TranslationTextComponent(key, args);
            mc.player.displayClientMessage(text, true);
        }
    }
}
