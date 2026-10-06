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

@Mod(NoJbPut.MODID)
public class NoJbPut {

    public static final String MODID = "nojbput";

    private static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    public NoJbPut(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            ClientSetup.registerConfigScreen(modContainer);
        }
        LOGGER.info("No JB Put loaded");
    }
}
