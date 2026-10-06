package com.kingokksa.noput.config;

import java.util.Locale;

import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.TranslatableEnum;

public enum ListMode implements TranslatableEnum {
    WHITELIST,
    BLACKLIST;

    @Override
    public Component getTranslatedName() {
        return Component.translatable("nojbput.listmode." + name().toLowerCase(Locale.ROOT));
    }
}
