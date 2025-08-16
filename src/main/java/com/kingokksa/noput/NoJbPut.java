package com.kingokksa.noput;

import com.kingokksa.noput.util.ToggleHandler;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

@Mod(NoJbPut.MODID)
public class NoJbPut {
    public static final String MODID = "nojbput";
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(MODID, "toggle_sync"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);

    static {
        CHANNEL.registerMessage(
                0,
                ToggleSyncMessage.class,
                ToggleSyncMessage::encode,
                ToggleSyncMessage::decode,
                ToggleSyncMessage::handle);
    }

    public NoJbPut() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!ToggleHandler.isEnabled())
            return;

        Player player = event.getEntity();
        if (event.getHand() == InteractionHand.OFF_HAND &&
                player.getOffhandItem().getItem() instanceof BlockItem) {
            event.setCanceled(true);
        }
    }
}