package com.kingokksa.noput.event;

import com.kingokksa.noput.NoJbPut;
import com.kingokksa.noput.config.InterceptMode;
import com.kingokksa.noput.network.NoJbPutNetwork;
import com.kingokksa.noput.util.InterceptRules;
import com.kingokksa.noput.util.ModState;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
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
        if (event.getHand() != Hand.OFF_HAND) {
            return;
        }
        PlayerEntity player = event.getPlayer();
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
        if (event.getHand() != Hand.OFF_HAND) {
            return;
        }
        PlayerEntity player = event.getPlayer();
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
        if (event.getPlayer() instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
            NoJbPutNetwork.sendToPlayer(player, ModState.getMode(player));
        }
    }

    /** 死亡重生/换维度时把状态带给新实体。 */
    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.getOriginal() instanceof ServerPlayerEntity && event.getPlayer() instanceof ServerPlayerEntity) {
            ModState.setMode((ServerPlayerEntity) event.getPlayer(),
                    ModState.getMode((ServerPlayerEntity) event.getOriginal()));
        }
    }

    /** 客户端用本地缓存（服务端才是权威），服务端读玩家自己的状态。 */
    private static InterceptMode modeFor(PlayerEntity player) {
        if (player instanceof ServerPlayerEntity) {
            return ModState.getMode((ServerPlayerEntity) player);
        }
        return ModState.getClientMode();
    }
}
