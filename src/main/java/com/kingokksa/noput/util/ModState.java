package com.kingokksa.noput.util;

import com.kingokksa.noput.config.InterceptMode;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;

public final class ModState {

    public static final String NBT_BLOCK_PLACEMENT = "nojbput_block_placement";
    public static final String NBT_BLOCK_USAGE = "nojbput_block_usage";

    private static volatile InterceptMode clientMode = InterceptMode.ALLOW;

    private ModState() {
    }

    public static InterceptMode getMode(ServerPlayerEntity player) {
        CompoundNBT data = player.getPersistentData();
        return InterceptMode.of(data.getBoolean(NBT_BLOCK_PLACEMENT), data.getBoolean(NBT_BLOCK_USAGE))
                .normalize();
    }

    public static void setMode(ServerPlayerEntity player, InterceptMode mode) {
        InterceptMode normalized = mode.normalize();
        CompoundNBT data = player.getPersistentData();
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
