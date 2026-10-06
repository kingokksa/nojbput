package com.kingokksa.noput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kingokksa.noput.client.ClientSetup;
import com.kingokksa.noput.config.Config;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;

/**
 * No JB Put —— 可开关的副手拦截 mod。
 *
 * <p>构造器里只做「只碰自己东西」的事：注册自己的配置与（客户端的）配置界面扩展点。
 * 不在这里 {@code NeoForge.EVENT_BUS.register(this)}，游戏总线事件交给
 * {@code @EventBusSubscriber} 注解的类（26.x 的注解没有 bus 属性，NeoForge 按事件类型自动选总线）。
 */
@Mod(NoJbPut.MODID)
public class NoJbPut {

    public static final String MODID = "nojbput";

    private static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    public NoJbPut(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        if (FMLEnvironment.getDist() == Dist.CLIENT) {
            // 借用 NeoForge 自带的自动配置界面（TOML 之外再给一个游戏内编辑入口）
            ClientSetup.registerConfigScreen(modContainer);
        }
        LOGGER.info("No JB Put loaded");
    }
}
