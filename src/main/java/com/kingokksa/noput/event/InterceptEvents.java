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
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = NoJbPut.MODID)
public final class InterceptEvents {

    private InterceptEvents() {
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getHand() != InteractionHand.OFF_HAND) {
            return;
        }
        Player player = event.getEntity();
        if (InterceptRules.isBlockedOnBlock(player.getMainHandItem(), player.getOffhandItem(), modeFor(player))) {
            event.setCanceled(true);
        }
    }

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

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            NoJbPutNetwork.sendToPlayer(player, ModState.getMode(player));
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.getOriginal() instanceof ServerPlayer oldPlayer
                && event.getEntity() instanceof ServerPlayer newPlayer) {
            ModState.setMode(newPlayer, ModState.getMode(oldPlayer));
        }
    }

    private static InterceptMode modeFor(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            return ModState.getMode(serverPlayer);
        }
        return ModState.getClientMode();
    }
}
