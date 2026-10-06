package com.kingokksa.noput.util;

import com.kingokksa.noput.config.InterceptMode;

import net.minecraft.server.level.ServerPlayer;

/**
 * 玩家级拦截状态。
 *
 * <p>旧版本用一个全局 static boolean（多人服务器上所有玩家共用一个开关），现在改为：
 * <ul>
 *   <li>服务端：状态存在 {@code ServerPlayer#getPersistentData()} 里，每个玩家独立。</li>
 *   <li>客户端：只保留一份缓存，仅用于徽标/提示的显示；真正的判定在服务端。</li>
 * </ul>
 *
 * <p>状态是 {@link InterceptMode}（放置/使用两个拦截位），默认 {@code ALLOW}（全放行）。
 */
public final class ModState {

    /** 服务端 NBT 键：是否拦副手放置。 */
    public static final String NBT_BLOCK_PLACEMENT = "nojbput_block_placement";
    /** 服务端 NBT 键：是否拦副手使用。 */
    public static final String NBT_BLOCK_USAGE = "nojbput_block_usage";

    /** 客户端缓存（默认全放行）。 */
    private static volatile InterceptMode clientMode = InterceptMode.ALLOW;

    private ModState() {
    }

    // --- 服务端（每个玩家独立） ---

    public static InterceptMode getMode(ServerPlayer player) {
        var data = player.getPersistentData();
        // 没写过就是默认：全放行（getBoolean 对缺失的键返回 false）
        return InterceptMode.of(data.getBoolean(NBT_BLOCK_PLACEMENT), data.getBoolean(NBT_BLOCK_USAGE))
                .normalize();
    }

    /** 写入前先按服务端配置归一化，服务端始终是权威。 */
    public static void setMode(ServerPlayer player, InterceptMode mode) {
        InterceptMode normalized = mode.normalize();
        var data = player.getPersistentData();
        data.putBoolean(NBT_BLOCK_PLACEMENT, normalized.blocksPlacement());
        data.putBoolean(NBT_BLOCK_USAGE, normalized.blocksUsage());
    }

    // --- 客户端缓存 ---

    public static InterceptMode getClientMode() {
        return clientMode;
    }

    public static void setClientMode(InterceptMode mode) {
        clientMode = mode.normalize();
    }
}
