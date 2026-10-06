package com.kingokksa.noput.client;

import java.util.function.BiFunction;

import com.kingokksa.noput.NoJbPut;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * 客户端专用初始化。
 *
 * <p>放在独立的 {@code Dist.CLIENT} 类里：{@link ExtensionPoint} 自己就 import 了
 * {@code Minecraft}/{@code Screen}，如果直接在公共主类构造器里引用它，专用服务端会
 * 因为加载到客户端类而崩。
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
                ExtensionPoint.CONFIGGUIFACTORY,
                () -> (BiFunction<Minecraft, Screen, Screen>) (mc, parent) -> new ConfigScreen(parent));
    }
}
