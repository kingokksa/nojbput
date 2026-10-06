package com.kingokksa.noput.network;

import com.kingokksa.noput.NoJbPut;
import com.kingokksa.noput.config.InterceptMode;
import com.kingokksa.noput.util.ModState;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/** 网络通道。协议版本变更会让新旧客户端互不兼容（这是有意的）。 */
@EventBusSubscriber(modid = NoJbPut.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class NoJbPutNetwork {

    public static final String PROTOCOL_VERSION = "3";

    private NoJbPutNetwork() {
    }

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);
        registrar.playToServer(StateSyncMessage.TYPE, StateSyncMessage.STREAM_CODEC, NoJbPutNetwork::handleFromClient);
        registrar.playToClient(StateSyncMessage.TYPE, StateSyncMessage.STREAM_CODEC, NoJbPutNetwork::handleFromServer);
    }

    /** 服务端收到客户端请求：以服务端为权威保存（内部会按配置归一化），并回执。 */
    private static void handleFromClient(StateSyncMessage message, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                ModState.setMode(player, message.mode());
                sendToPlayer(player, ModState.getMode(player));
            }
        });
    }

    /** 客户端收到服务端回执：更新本地缓存。 */
    private static void handleFromServer(StateSyncMessage message, IPayloadContext context) {
        context.enqueueWork(() -> ModState.setClientMode(message.mode()));
    }

    public static void sendToServer(InterceptMode mode) {
        PacketDistributor.sendToServer(new StateSyncMessage(mode));
    }

    public static void sendToPlayer(ServerPlayer player, InterceptMode mode) {
        PacketDistributor.sendToPlayer(player, new StateSyncMessage(mode));
    }
}
