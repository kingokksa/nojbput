package com.kingokksa.noput.client;

import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

/** 客户端专用初始化。只被 {@code NoJbPut} 在 {@code Dist.CLIENT} 分支里调用。 */
public final class ClientSetup {

    private ClientSetup() {
    }

    /** 让 NeoForge 自带的配置界面接管本 mod 的 COMMON 配置（模组列表 → Config）。 */
    public static void registerConfigScreen(ModContainer modContainer) {
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }
}
