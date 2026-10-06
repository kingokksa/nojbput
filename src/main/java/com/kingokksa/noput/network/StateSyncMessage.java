package com.kingokksa.noput.network;

import java.util.function.Supplier;

import com.kingokksa.noput.config.InterceptMode;
import com.kingokksa.noput.util.ModState;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public final class StateSyncMessage {

    private final boolean blockPlacement;
    private final boolean blockUsage;

    public StateSyncMessage(boolean blockPlacement, boolean blockUsage) {
        this.blockPlacement = blockPlacement;
        this.blockUsage = blockUsage;
    }

    public StateSyncMessage(InterceptMode mode) {
        this(mode.blocksPlacement(), mode.blocksUsage());
    }

    public boolean blockPlacement() {
        return blockPlacement;
    }

    public boolean blockUsage() {
        return blockUsage;
    }

    public InterceptMode mode() {
        return InterceptMode.of(blockPlacement, blockUsage);
    }

    public static void encode(StateSyncMessage message, FriendlyByteBuf buf) {
        buf.writeBoolean(message.blockPlacement);
        buf.writeBoolean(message.blockUsage);
    }

    public static StateSyncMessage decode(FriendlyByteBuf buf) {
        return new StateSyncMessage(buf.readBoolean(), buf.readBoolean());
    }

    public static void handle(StateSyncMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            if (context.getDirection().getReceptionSide().isServer()) {
                ServerPlayer sender = context.getSender();
                if (sender == null) {
                    return;
                }
                ModState.setMode(sender, message.mode());
                NoJbPutNetwork.sendToPlayer(sender, ModState.getMode(sender));
            } else {
                ModState.setClientMode(message.mode());
            }
        });
        context.setPacketHandled(true);
    }
}
