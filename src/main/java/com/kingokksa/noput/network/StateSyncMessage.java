package com.kingokksa.noput.network;

import com.kingokksa.noput.NoJbPut;
import com.kingokksa.noput.config.InterceptMode;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record StateSyncMessage(boolean blockPlacement, boolean blockUsage) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<StateSyncMessage> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(NoJbPut.MODID, "state_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, StateSyncMessage> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL, StateSyncMessage::blockPlacement,
                    ByteBufCodecs.BOOL, StateSyncMessage::blockUsage,
                    StateSyncMessage::new);

    public StateSyncMessage(InterceptMode mode) {
        this(mode.blocksPlacement(), mode.blocksUsage());
    }

    public InterceptMode mode() {
        return InterceptMode.of(blockPlacement, blockUsage);
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
