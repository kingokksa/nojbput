package com.kingokksa.noput.client;

import com.kingokksa.noput.util.ToggleHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = "nojbput", value = Dist.CLIENT)
public class ToggleOverlay {
    private static final ResourceLocation ICON =
            new ResourceLocation("nojbput", "textures/gui/switch.png");

    private static final int SIZE = 4;
    private static final int TEXTURE_SIZE = 8;

    private static final int INVENTORY_WIDTH = 176;
    private static final int INVENTORY_HEIGHT = 166;
    private static final int OFFHAND_SLOT_X = 77;
    private static final int OFFHAND_SLOT_Y = 65;

    @SubscribeEvent
    public static void onRender(ScreenEvent.Render.Post event) {
        if (!(event.getScreen() instanceof InventoryScreen)) return;

        Minecraft mc = Minecraft.getInstance();
        GuiGraphics guiGraphics = event.getGuiGraphics();

        int guiLeft = (mc.getWindow().getGuiScaledWidth() - INVENTORY_WIDTH) / 2;
        int guiTop = (mc.getWindow().getGuiScaledHeight() - INVENTORY_HEIGHT) / 2;

        int switchX = guiLeft + OFFHAND_SLOT_X + 16 - SIZE +4;
        int switchY = guiTop + OFFHAND_SLOT_Y - SIZE;

        RenderSystem.setShaderTexture(0, ICON);
        guiGraphics.blit(ICON,
                switchX, switchY,
                0,
                ToggleHandler.isEnabled() ? 0 : SIZE,
                SIZE, SIZE,
                TEXTURE_SIZE, TEXTURE_SIZE);
    }

    @SubscribeEvent
    public static void onMouseClick(ScreenEvent.MouseButtonPressed.Pre event) {
        if (!(event.getScreen() instanceof InventoryScreen)) return;
        if (event.getButton() != GLFW.GLFW_MOUSE_BUTTON_LEFT) return;

        Minecraft mc = Minecraft.getInstance();

        int guiLeft = (mc.getWindow().getGuiScaledWidth() - INVENTORY_WIDTH) / 2;
        int guiTop = (mc.getWindow().getGuiScaledHeight() - INVENTORY_HEIGHT) / 2;
        int switchX = guiLeft + OFFHAND_SLOT_X + 16 - SIZE +4;
        int switchY = guiTop + OFFHAND_SLOT_Y - SIZE;

        if (event.getMouseX() >= switchX &&
                event.getMouseX() <= switchX + SIZE &&
                event.getMouseY() >= switchY &&
                event.getMouseY() <= switchY + SIZE) {

            ToggleHandler.toggle();
            event.setCanceled(true);
            event.setResult(net.minecraftforge.eventbus.api.Event.Result.DENY);
        }
    }
}