package com.kingokksa.noput.network;

import com.kingokksa.noput.NoJbPut;
import com.kingokksa.noput.config.InterceptMode;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

/** 网络通道。协议版本变更会让新旧客户端互不兼容（这是有意的）。 */
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

    /** 在 mod 构造期调用：只碰本 mod 自己的通道，不碰全局事件总线。 */
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
