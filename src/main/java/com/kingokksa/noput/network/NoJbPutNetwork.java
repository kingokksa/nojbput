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

@EventBusSubscriber(modid = NoJbPut.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class NoJbPutNetwork {

    public static final String PROTOCOL_VERSION = "3";

    private NoJbPutNetwork() {
    }

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);
        registrar.playBidirectional(StateSyncMessage.TYPE, StateSyncMessage.STREAM_CODEC, NoJbPutNetwork::handle);
    }

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
