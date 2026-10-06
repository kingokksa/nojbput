package com.kingokksa.noput.event;

import com.kingokksa.noput.NoJbPut;
import com.kingokksa.noput.config.InterceptMode;
import com.kingokksa.noput.network.NoJbPutNetwork;
import com.kingokksa.noput.util.InterceptRules;
import com.kingokksa.noput.util.ModState;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 游戏总线事件。用 {@code @Mod.EventBusSubscriber}（Forge 在 mod 构造完成后统一注册），
 * 不在 mod 主类构造器里直接 {@code MinecraftForge.EVENT_BUS.register(this)}。
 */
@Mod.EventBusSubscriber(modid = NoJbPut.MODID)
public final class InterceptEvents {

    private InterceptEvents() {
    }

    /** 副手放置方块：右键方块时触发。 */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getHand() != InteractionHand.OFF_HAND) {
            return;
        }
        Player player = event.getEntity();
        if (InterceptRules.isBlocked(InterceptRules.Action.PLACEMENT,
                player.getMainHandItem(), player.getOffhandItem(), modeFor(player))) {
            event.setCanceled(true);
        }
    }

    /**
     * 副手使用物品：右键空气（以及方块交互失败后回落到物品使用时）触发。
     *
     * <p>注意这里【不】拦 RightClickBlock 的使用分支，否则副手空手右键箱子也会被拦掉。
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (event.getHand() != InteractionHand.OFF_HAND) {
            return;
        }
        Player player = event.getEntity();
        InterceptMode mode = modeFor(player);
        ItemStack offHand = player.getOffhandItem();
        boolean blocked = InterceptRules.isBlocked(InterceptRules.Action.PLACEMENT,
                player.getMainHandItem(), offHand, mode)
                || InterceptRules.isBlocked(InterceptRules.Action.USAGE,
                player.getMainHandItem(), offHand, mode);
        if (blocked) {
            event.setCanceled(true);
        }
    }

    /** 登录时把服务端的权威状态下发，避免客户端缓存过期。 */
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            NoJbPutNetwork.sendToPlayer(player, ModState.getMode(player));
        }
    }

    /** 死亡重生/换维度时把状态带给新实体。 */
    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.getOriginal() instanceof ServerPlayer oldPlayer
                && event.getEntity() instanceof ServerPlayer newPlayer) {
            ModState.setMode(newPlayer, ModState.getMode(oldPlayer));
        }
    }

    /** 客户端用本地缓存（服务端才是权威），服务端读玩家自己的状态。 */
    private static InterceptMode modeFor(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            return ModState.getMode(serverPlayer);
        }
        return ModState.getClientMode();
    }
}
