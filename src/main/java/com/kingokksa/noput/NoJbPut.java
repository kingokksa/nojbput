package com.kingokksa.noput;

import com.kingokksa.noput.util.ToggleHandler;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("nojbput")
public class NoJbPut {
    public NoJbPut() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!ToggleHandler.isEnabled()) return;

        Player player = event.getEntity();
        if (event.getHand() == InteractionHand.OFF_HAND &&
                player.getOffhandItem().getItem() instanceof BlockItem) {
            event.setCanceled(true);
        }
    }
}