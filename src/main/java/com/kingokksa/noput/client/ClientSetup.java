package com.kingokksa.noput.client;

import com.kingokksa.noput.NoJbPut;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * 客户端专用初始化。
 *
 * <p>放在独立的 {@code Dist.CLIENT} 类里，这样专用服务端不会加载到
 * {@link ConfigScreenHandler}（它引用客户端类）。
 *
 * <p>注册配置界面扩展点后，Mod 列表里本模组的「Config」按钮会直接打开
 * {@link ConfigScreen}。
 */
@Mod.EventBusSubscriber(modid = NoJbPut.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ClientSetup {

    private ClientSetup() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        ModLoadingContext.get().registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((mc, parent) -> new ConfigScreen(parent)));
    }
}
