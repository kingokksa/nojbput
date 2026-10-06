package com.kingokksa.noput.network;

import java.util.function.Supplier;

import com.kingokksa.noput.config.InterceptMode;
import com.kingokksa.noput.util.ModState;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

/**
 * 双向的拦截状态同步包（携带放置/使用两个拦截位）。
 *
 * <ul>
 *   <li>客户端 → 服务端：客户端请求切到某个状态（服务端为权威）。</li>
 *   <li>服务端 → 客户端：服务端下发归一化后的真实状态。</li>
 * </ul>
 */
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
                // setMode 内部会按服务端配置归一化（客户端可能送来越权的状态）
                ModState.setMode(sender, message.mode());
                // 回执：把服务端的权威状态发回给这个玩家
                NoJbPutNetwork.sendToPlayer(sender, ModState.getMode(sender));
            } else {
                ModState.setClientMode(message.mode());
            }
        });
        context.setPacketHandled(true);
    }
}
