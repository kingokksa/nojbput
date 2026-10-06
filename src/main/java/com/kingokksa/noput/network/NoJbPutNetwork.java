package com.kingokksa.noput.network;

import com.kingokksa.noput.NoJbPut;
import com.kingokksa.noput.config.InterceptMode;
import com.kingokksa.noput.util.ModState;

import net.minecraft.network.protocol.PacketFlow;
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
        // 一个 payload Type 在每个 ConnectionProtocol 上只能注册一次。
        // 拆成 playToServer + playToClient 两次调用会抛
        // "Cannot register payload nojbput:state_sync as it is already registered."
        // （NetworkRegistry.register 里对 PAYLOAD_REGISTRATIONS 做了 containsKey 检查）。
        // 1.21.1 的 playBidirectional 只收一个 handler，方向在 handle() 里按 flow 分派。
        registrar.playBidirectional(StateSyncMessage.TYPE, StateSyncMessage.STREAM_CODEC, NoJbPutNetwork::handle);
    }

    /**
     * 一个 handler 同时服务两个方向。
     *
     * <p>服务端（SERVERBOUND）：以服务端为权威保存（内部会按配置归一化），并回执给该玩家。
     * <p>客户端（CLIENTBOUND）：更新本地缓存，仅供徽标与提示显示。
     */
    private static void handle(StateSyncMessage message, IPayloadContext context) {
        if (context.flow() == PacketFlow.SERVERBOUND) {
            context.enqueueWork(() -> {
                if (context.player() instanceof ServerPlayer player) {
                    ModState.setMode(player, message.mode());
                    sendToPlayer(player, ModState.getMode(player));
                }
            });
        } else {
            context.enqueueWork(() -> ModState.setClientMode(message.mode()));
        }
    }

    public static void sendToServer(InterceptMode mode) {
        PacketDistributor.sendToServer(new StateSyncMessage(mode));
    }

    public static void sendToPlayer(ServerPlayer player, InterceptMode mode) {
        PacketDistributor.sendToPlayer(player, new StateSyncMessage(mode));
    }
}
