package com.kingokksa.noput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kingokksa.noput.util.ToggleHandler;
import com.kingokksa.noput.client.ToggleOverlay;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.*;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@Mod(NoJbPut.MODID)
public class NoJbPut {
    public static final String MODID = "nojbput";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
    public static final ResourceLocation TOGGLE_SYNC_ID = ResourceLocation.tryParse(MODID + ":toggle_sync");

    public NoJbPut(IEventBus modEventBus) {
        NeoForge.EVENT_BUS.register(this);
        modEventBus.addListener(NoJbPut::registerPayloads);
        ToggleOverlay.register();
    }

    public static void registerPayloads(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(
                ToggleSyncMessage.TYPE,
                ToggleSyncMessage.STREAM_CODEC,
                (msg, context) -> {
                    ToggleHandler.setEnabled(msg.enabled());
                    context.enqueueWork(() -> {
                        if (context.player() instanceof ServerPlayer serverPlayer) {
                            CompoundTag data = serverPlayer.getPersistentData();
                            data.putBoolean("nojbput_toggle", msg.enabled());
                        }
                    });
                });
    }

    // 客户端发送同步消息的方法
    public static void sendToggleSync(boolean enabled) {
        PacketDistributor.sendToServer(new ToggleSyncMessage(enabled));
    }

    // 玩家登录时加载保存的开关状态
    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer serverPlayer) {
            CompoundTag data = serverPlayer.getPersistentData();
            boolean savedToggle = data.getBoolean("nojbput_toggle");
            ToggleHandler.setEnabled(savedToggle);
        }
    }

    // 玩家登出时保存开关状态
    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer serverPlayer) {
            CompoundTag data = serverPlayer.getPersistentData();
            data.putBoolean("nojbput_toggle", ToggleHandler.isEnabled());
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRightClickBlock(final PlayerInteractEvent.RightClickBlock event) {
        if (!ToggleHandler.isEnabled())
            return;
        final Player player = event.getEntity();
        if (event.getHand() == net.minecraft.world.InteractionHand.OFF_HAND &&
                player.getOffhandItem().getItem() instanceof net.minecraft.world.item.BlockItem) {
            event.setCanceled(true);
        }
    }
}