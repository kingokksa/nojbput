package com.kingokksa.noput.client;

import com.kingokksa.noput.config.Config;
import com.kingokksa.noput.config.InterceptMode;
import com.kingokksa.noput.network.StateSyncMessage;
import com.kingokksa.noput.util.ModState;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

/**
 * 客户端循环切换拦截状态。
 *
 * <p>客户端只改本地缓存并把请求发给服务端；服务端保存玩家级状态后会回执，
 * 客户端以回执为准（服务端权威）。
 *
 * <p>26.x 的 {@code PacketDistributor} 没有 {@code sendToServer}，
 * 客户端 → 服务端只能用 {@code ClientPacketDistributor}。
 */
public final class ClientToggle {

    private ClientToggle() {
    }

    /** 循环到下一个可达状态（绿 → 蓝 → 黄 → 红 → 绿，跳过配置不允许的）。 */
    public static void cycle() {
        if (!Config.masterSwitch()) {
            message("message.nojbput.disabled");
            return;
        }
        if (!InterceptMode.anyAvailable()) {
            // 配置里两种拦截都被禁止，没有任何状态可切
            message("message.nojbput.none");
            return;
        }
        setMode(ModState.getClientMode().next());
    }

    public static void setMode(InterceptMode mode) {
        ModState.setClientMode(mode);
        ClientPacketDistributor.sendToServer(new StateSyncMessage(mode));
        message("message.nojbput.mode", Component.translatable(langKey(mode)));
    }

    /** 状态对应的 lang 键，徽标 tooltip 也用它。 */
    public static String langKey(InterceptMode mode) {
        return switch (mode) {
            case BLOCK_PLACEMENT -> "gui.nojbput.mode.placement";
            case BLOCK_USAGE -> "gui.nojbput.mode.usage";
            case BLOCK_ALL -> "gui.nojbput.mode.all";
            case ALLOW -> "gui.nojbput.mode.allow";
        };
    }

    private static void message(String key, Object... args) {
        var player = Minecraft.getInstance().player;
        if (player != null) {
            // 26.x 用 sendOverlayMessage 取代了 displayClientMessage(Component, boolean)
            player.sendOverlayMessage(Component.translatable(key, args));
        }
    }
}
