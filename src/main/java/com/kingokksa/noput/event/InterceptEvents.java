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

@Mod.EventBusSubscriber(modid = NoJbPut.MODID)
public final class InterceptEvents {

    private InterceptEvents() {
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getHand() != Hand.OFF_HAND) {
            return;
        }
        PlayerEntity player = event.getPlayer();
        if (InterceptRules.isBlockedOnBlock(player.getMainHandItem(), player.getOffhandItem(), modeFor(player))) {
            event.setCanceled(true);
        }
    }

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

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getPlayer() instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
            NoJbPutNetwork.sendToPlayer(player, ModState.getMode(player));
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.getOriginal() instanceof ServerPlayerEntity && event.getPlayer() instanceof ServerPlayerEntity) {
            ModState.setMode((ServerPlayerEntity) event.getPlayer(),
                    ModState.getMode((ServerPlayerEntity) event.getOriginal()));
        }
    }

    private static InterceptMode modeFor(PlayerEntity player) {
        if (player instanceof ServerPlayerEntity) {
            return ModState.getMode((ServerPlayerEntity) player);
        }
        return ModState.getClientMode();
    }
}
