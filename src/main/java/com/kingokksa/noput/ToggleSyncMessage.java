package com.kingokksa.noput;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ToggleSyncMessage {
    private boolean enabled;

    public ToggleSyncMessage(boolean enabled) {
        this.enabled = enabled;
    }

    public static void encode(ToggleSyncMessage message, FriendlyByteBuf buffer) {
        buffer.writeBoolean(message.enabled);
    }

    public static ToggleSyncMessage decode(FriendlyByteBuf buffer) {
        return new ToggleSyncMessage(buffer.readBoolean());
    }

    public static void handle(ToggleSyncMessage message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            // Only execute on the server side
            if (context.get().getDirection().getReceptionSide().isServer()) {
                com.kingokksa.noput.util.ToggleHandler.setEnabled(message.enabled);
            }
        });
        context.get().setPacketHandled(true);
    }
}