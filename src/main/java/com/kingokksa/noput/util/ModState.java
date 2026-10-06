package com.kingokksa.noput.util;

import com.kingokksa.noput.config.InterceptMode;

import net.minecraft.server.level.ServerPlayer;

public final class ModState {

    public static final String NBT_BLOCK_PLACEMENT = "nojbput_block_placement";
    public static final String NBT_BLOCK_USAGE = "nojbput_block_usage";

    private static volatile InterceptMode clientMode = InterceptMode.ALLOW;

    private ModState() {
    }

    public static InterceptMode getMode(ServerPlayer player) {
        var data = player.getPersistentData();
        return InterceptMode.of(data.getBooleanOr(NBT_BLOCK_PLACEMENT, false),
                data.getBooleanOr(NBT_BLOCK_USAGE, false)).normalize();
    }

    public static void setMode(ServerPlayer player, InterceptMode mode) {
        InterceptMode normalized = mode.normalize();
        var data = player.getPersistentData();
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
