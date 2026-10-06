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

/**
 * 网络通道。协议版本变更会让新旧客户端互不兼容（这是有意的）。
 *
 * <p>26.x 的 {@code @EventBusSubscriber} 只有 {@code value()} 与 {@code modid()}，
 * 没有 {@code bus} 属性，mod 总线事件靠事件类型自动路由。
 *
 * <p>这个类会被服务端加载，所以【不能】引用 {@code ClientPacketDistributor}；
 * 客户端 → 服务端的发送放在 {@code client/ClientToggle} 里。
 */
@EventBusSubscriber(modid = NoJbPut.MODID)
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
        // 26.x 的 playBidirectional 有双 handler 重载，参数顺序是 (serverbound, clientbound)。
        registrar.playBidirectional(StateSyncMessage.TYPE, StateSyncMessage.STREAM_CODEC,
                NoJbPutNetwork::handleFromClient, NoJbPutNetwork::handleFromServer);
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

    public static void sendToPlayer(ServerPlayer player, InterceptMode mode) {
        PacketDistributor.sendToPlayer(player, new StateSyncMessage(mode));
    }
}
