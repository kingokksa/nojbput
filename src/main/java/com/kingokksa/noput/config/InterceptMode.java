package com.kingokksa.noput.config;

/**
 * 玩家自己可循环切换的拦截状态（两个拦截位：放置 / 使用）。
 *
 * <p>枚举顺序就是循环顺序，也是徽标颜色顺序：
 * <pre>
 *   ALLOW(绿) → BLOCK_PLACEMENT(蓝) → BLOCK_USAGE(黄) → BLOCK_ALL(红) → ALLOW ...
 * </pre>
 *
 * <p>哪些状态可达由服务端配置决定：
 * {@link Config#disableOffhandPlacement()} 为 false 时所有「拦放置」的状态都不可达（蓝色消失），
 * {@link Config#disableOffhandUsage()} 同理（黄色消失）。两个都为 false 时只剩绿色，开关无意义。
 */
public enum InterceptMode {

    /** 绿色：放置与使用都放行。 */
    ALLOW(false, false),
    /** 蓝色：只拦副手放置方块。 */
    BLOCK_PLACEMENT(true, false),
    /** 黄色：只拦副手使用物品。 */
    BLOCK_USAGE(false, true),
    /** 红色：放置与使用都拦。 */
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

    /** 由两个拦截位反查状态。 */
    public static InterceptMode of(boolean blockPlacement, boolean blockUsage) {
        if (blockPlacement) {
            return blockUsage ? BLOCK_ALL : BLOCK_PLACEMENT;
        }
        return blockUsage ? BLOCK_USAGE : ALLOW;
    }

    /** 该状态在当前服务端配置下是否可达。 */
    public boolean isAvailable() {
        return (!blockPlacement || Config.disableOffhandPlacement())
                && (!blockUsage || Config.disableOffhandUsage());
    }

    /**
     * 去掉当前配置不允许的拦截位。
     *
     * <p>例如玩家存了红色，但管理员后来把 {@code disableOffhandUsage} 改回 false，
     * 归一化后变成蓝色，而不是整个丢掉。
     */
    public InterceptMode normalize() {
        return of(blockPlacement && Config.disableOffhandPlacement(),
                blockUsage && Config.disableOffhandUsage());
    }

    /** 循环到下一个可达状态（绿 → 蓝 → 黄 → 红 → 绿，跳过不可达的）。 */
    public InterceptMode next() {
        InterceptMode[] all = values();
        InterceptMode candidate = normalize();
        for (int i = 0; i < all.length; i++) {
            candidate = all[(candidate.ordinal() + 1) % all.length];
            if (candidate.isAvailable()) {
                return candidate;
            }
        }
        // ALLOW 永远可达，这里理论上到不了
        return ALLOW;
    }

    /** 是否存在可循环的状态（两种拦截都被配置禁止时就没有）。 */
    public static boolean anyAvailable() {
        return BLOCK_PLACEMENT.isAvailable() || BLOCK_USAGE.isAvailable();
    }
}
