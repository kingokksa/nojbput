package com.kingokksa.noput.client;

import com.kingokksa.noput.NoJbPut;
import com.kingokksa.noput.ToggleSyncMessage;
import com.kingokksa.noput.util.ToggleHandler;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = NoJbPut.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class KeyBindings {
    public static final String KEY_CATEGORY = "key.category.nojbput";
    public static final String KEY_TOGGLE = "key.nojbput.toggle";

    public static KeyMapping toggleKey;

    @SubscribeEvent
    public static void registerKeyBindings(RegisterKeyMappingsEvent event) {
        toggleKey = new KeyMapping(
                KEY_TOGGLE,
                KeyConflictContext.IN_GAME,
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_ALT,
                KEY_CATEGORY);

        event.register(toggleKey);
    }

    @Mod.EventBusSubscriber(modid = NoJbPut.MODID, value = Dist.CLIENT)
    public static class ClientTickHandler {
        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase == TickEvent.Phase.END) {
                Minecraft mc = Minecraft.getInstance();
                if (mc.level != null && mc.player != null) {
                    while (toggleKey.consumeClick()) {
                        // 切换状态
                        ToggleHandler.toggle();
                        NoJbPut.CHANNEL.sendToServer(new ToggleSyncMessage(ToggleHandler.isEnabled()));

                        // 显示状态消息
                        String status = ToggleHandler.isEnabled() ? "开" : "关";
                        mc.player.displayClientMessage(
                                Component.literal("副手放置拦截: " + status),
                                true);
                    }
                }
            }
        }
    }
}