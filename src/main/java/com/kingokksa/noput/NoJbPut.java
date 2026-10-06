package com.kingokksa.noput;

import com.kingokksa.noput.config.Config;
import com.kingokksa.noput.network.NoJbPutNetwork;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod(NoJbPut.MODID)
public class NoJbPut {

    public static final String MODID = "nojbput";

    public NoJbPut() {
        // 只做本 mod 私有的初始化，不碰全局事件总线（构造期是并行执行的）
        NoJbPutNetwork.register();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
}
