package com.kingokksa.noput.config;

public enum InterceptMode {

    ALLOW(false, false),
    BLOCK_PLACEMENT(true, false),
    BLOCK_USAGE(false, true),
    BLOCK_ALL(true, true);

    private final boolean blockPlacement;
    private final boolean blockUsage;

    InterceptMode(boolean blockPlacement, boolean blockUsage) {
        this.blockPlacement = blockPlacement;
        this.blockUsage = blockUsage;
    }

    public boolean blocksPlacement() {
        return blockPlacement;
    }

    public boolean blocksUsage() {
        return blockUsage;
    }

    public static InterceptMode of(boolean blockPlacement, boolean blockUsage) {
        if (blockPlacement) {
            return blockUsage ? BLOCK_ALL : BLOCK_PLACEMENT;
        }
        return blockUsage ? BLOCK_USAGE : ALLOW;
    }

    public boolean isAvailable() {
        return (!blockPlacement || Config.disableOffhandPlacement())
                && (!blockUsage || Config.disableOffhandUsage());
    }

    public InterceptMode normalize() {
        return of(blockPlacement && Config.disableOffhandPlacement(),
                blockUsage && Config.disableOffhandUsage());
    }

    public InterceptMode next() {
        InterceptMode[] all = values();
        InterceptMode candidate = normalize();
        for (int i = 0; i < all.length; i++) {
            candidate = all[(candidate.ordinal() + 1) % all.length];
            if (candidate.isAvailable()) {
                return candidate;
            }
        }
        return ALLOW;
    }

    public static boolean anyAvailable() {
        return BLOCK_PLACEMENT.isAvailable() || BLOCK_USAGE.isAvailable();
    }
}
