package com.kingokksa.noput.client;

import com.kingokksa.noput.NoJbPut;
import com.kingokksa.noput.util.ToggleHandler;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = NoJbPut.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class KeyInputHandler {
    public static final String KEY_CATEGORY_NOJBPUT = "key.category.nojbput.nojbput";
    public static final String KEY_TOGGLE_NOJBPUT = "key.nojbput.toggle";

    public static final KeyMapping TOGGLE_KEY = new KeyMapping(
            KEY_TOGGLE_NOJBPUT,
            GLFW.GLFW_KEY_RIGHT_ALT,
            KEY_CATEGORY_NOJBPUT);

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        while (TOGGLE_KEY.consumeClick()) {
            // 切换状态
            ToggleHandler.toggle();

            // 发送同步消息到服务器
            NoJbPut.sendToggleSync(ToggleHandler.isEnabled());

            // 在屏幕上显示消息
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                String status = ToggleHandler.isEnabled() ? "开" : "关";
                mc.player.displayClientMessage(
                        Component.literal("副手放置拦截: " + status),
                        true);
            }
        }
    }

    @SubscribeEvent
    public static void registerKeyBindings(RegisterKeyMappingsEvent event) {
        event.register(TOGGLE_KEY);
    }
}