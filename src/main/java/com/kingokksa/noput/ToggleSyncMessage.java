package com.kingokksa.noput;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;

public record ToggleSyncMessage(boolean enabled) implements CustomPacketPayload {
    public static final Type<ToggleSyncMessage> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath("nojbput", "toggle_sync"));

    public static final StreamCodec<ByteBuf, ToggleSyncMessage> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.BOOL, ToggleSyncMessage::enabled,
        ToggleSyncMessage::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}