package com.kingokksa.noput;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kingokksa.noput.config.Config;
import com.kingokksa.noput.network.NoJbPutNetwork;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

/**
 * No JB Put —— 可开关的副手拦截 mod。
 *
 * <p>构造器里只做两件「只碰自己东西」的事：注册自己的网络通道、注册自己的配置。
 * 不在这里 {@code MinecraftForge.EVENT_BUS.register(this)}（Forge 1.16.5/1.20.1 会在加载期
 * 并行构造 mod，此时碰全局事件总线可能与其它 mod 并发冲突），游戏总线事件交给
 * {@code @Mod.EventBusSubscriber} 注解的类。
 */
@Mod(NoJbPut.MODID)
public class NoJbPut {

    public static final String MODID = "nojbput";

    private static final Logger LOGGER = LogManager.getLogger(MODID);

    public NoJbPut() {
        NoJbPutNetwork.register();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        LOGGER.info("No JB Put loaded");
    }

    /** 供其它类使用的全局事件总线（按需注册用）。 */
    public static net.minecraftforge.eventbus.api.IEventBus forgeBus() {
        return MinecraftForge.EVENT_BUS;
    }
}
