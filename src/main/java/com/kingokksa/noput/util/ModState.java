package com.kingokksa.noput.util;

import com.kingokksa.noput.config.InterceptMode;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

public final class ModState {

    public static final String NBT_BLOCK_PLACEMENT = "nojbput_block_placement";
    public static final String NBT_BLOCK_USAGE = "nojbput_block_usage";

    private static volatile InterceptMode clientMode = InterceptMode.ALLOW;

    private ModState() {
    }

    public static InterceptMode getMode(ServerPlayer player) {
        CompoundTag data = player.getPersistentData();
        return InterceptMode.of(data.getBoolean(NBT_BLOCK_PLACEMENT), data.getBoolean(NBT_BLOCK_USAGE))
                .normalize();
    }

    public static void setMode(ServerPlayer player, InterceptMode mode) {
        InterceptMode normalized = mode.normalize();
        CompoundTag data = player.getPersistentData();
        data.putBoolean(NBT_BLOCK_PLACEMENT, normalized.blocksPlacement());
        data.putBoolean(NBT_BLOCK_USAGE, normalized.blocksUsage());
    }

    public static InterceptMode getClientMode() {
        return clientMode;
    }

    public static void setClientMode(InterceptMode mode) {
        clientMode = mode.normalize();
    }
}
