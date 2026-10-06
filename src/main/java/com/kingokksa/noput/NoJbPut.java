package com.kingokksa.noput;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kingokksa.noput.config.Config;
import com.kingokksa.noput.network.NoJbPutNetwork;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod(NoJbPut.MODID)
public class NoJbPut {

    public static final String MODID = "nojbput";

    private static final Logger LOGGER = LogManager.getLogger(MODID);

    public NoJbPut() {
        NoJbPutNetwork.register();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        LOGGER.info("No JB Put loaded");
    }

    public static net.minecraftforge.eventbus.api.IEventBus forgeBus() {
        return MinecraftForge.EVENT_BUS;
    }
}
