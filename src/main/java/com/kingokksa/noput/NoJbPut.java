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
        NoJbPutNetwork.register();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
}
