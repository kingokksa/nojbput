package com.kingokksa.noput.client;

import com.kingokksa.noput.NoJbPut;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;

import org.lwjgl.glfw.GLFW;

/**
 * 快捷键注册（mod 总线）。
 *
 * <p>26.x 把 {@code KeyMapping} 的分类从 String 换成了 {@code KeyMapping.Category} record，
 * 分类的显示名走 {@code key.category.<namespace>.<path>} 这个 lang 键。
 */
@EventBusSubscriber(modid = NoJbPut.MODID, value = Dist.CLIENT)
public final class KeyBindings {

    /** 分类 id：{@code nojbput:main} → lang 键 {@code key.category.nojbput.main}。 */
    public static final KeyMapping.Category KEY_CATEGORY =
            new KeyMapping.Category(Identifier.fromNamespaceAndPath(NoJbPut.MODID, "main"));

    public static final String KEY_TOGGLE = "key.nojbput.toggle";

    private static KeyMapping toggleKey;

    private KeyBindings() {
    }

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.registerCategory(KEY_CATEGORY);
        toggleKey = new KeyMapping(KEY_TOGGLE, KeyConflictContext.IN_GAME,
                InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_ALT, KEY_CATEGORY);
        event.register(toggleKey);
    }

    /** 未注册时返回 null，调用方需要判空。 */
    public static KeyMapping toggleKey() {
        return toggleKey;
    }
}
