package com.kingokksa.noput.network;

import com.kingokksa.noput.NoJbPut;
import com.kingokksa.noput.config.InterceptMode;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public final class NoJbPutNetwork {

    private static final String PROTOCOL_VERSION = "3";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(NoJbPut.MODID, "state_sync"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);

    private static boolean initialized;

    private NoJbPutNetwork() {
    }

    public static synchronized void register() {
        if (initialized) {
            return;
        }
        initialized = true;
        CHANNEL.registerMessage(0, StateSyncMessage.class,
                StateSyncMessage::encode, StateSyncMessage::decode, StateSyncMessage::handle);
    }

    public static void sendToServer(InterceptMode mode) {
        CHANNEL.sendToServer(new StateSyncMessage(mode));
    }

    public static void sendToPlayer(ServerPlayerEntity player, InterceptMode mode) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new StateSyncMessage(mode));
    }
}
