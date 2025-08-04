package com.kingokksa.noput.client;

import com.kingokksa.noput.NoJbPut;
import com.kingokksa.noput.util.ToggleHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;

public class ToggleOverlay {
    private static final ResourceLocation ICON = ResourceLocation.fromNamespaceAndPath(NoJbPut.MODID,
            "textures/gui/switch.png");
    private static final int BUTTON_WIDTH = 4;
    private static final int BUTTON_HEIGHT = 4;
    private static final int INVENTORY_WIDTH = 176;
    private static final int INVENTORY_HEIGHT = 166;
    private static final int OFFHAND_SLOT_X = 77;
    private static final int OFFHAND_SLOT_Y = 65;

    public static void register() {
        NeoForge.EVENT_BUS.register(ToggleOverlay.class);
    }

    @SubscribeEvent
    public static void onScreenRender(ScreenEvent.Render.Post event) {
        Screen screen = event.getScreen();
        if (!(screen instanceof InventoryScreen invScreen)) return;

        int btnX = invScreen.getGuiLeft() + OFFHAND_SLOT_X + 16;
        int btnY = invScreen.getGuiTop() + OFFHAND_SLOT_Y - 5;

        GuiGraphics graphics = event.getGuiGraphics();
        int v = ToggleHandler.isEnabled() ? 0 : 4;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderTexture(0, ICON);
        graphics.pose().pushPose();
        graphics.pose().translate(0, 0, 2000);
        graphics.blit(ICON, btnX, btnY, 0, v, BUTTON_WIDTH, BUTTON_HEIGHT, 4, 8);
        graphics.pose().popPose();
    }

    @SubscribeEvent
    public static void onScreenMouseClick(ScreenEvent.MouseButtonPressed.Pre event) {
        Screen screen = event.getScreen();
        if (!(screen instanceof InventoryScreen invScreen)) return;
        if (event.getButton() != 0) return;

        int btnX = invScreen.getGuiLeft() + OFFHAND_SLOT_X + 16;
        int btnY = invScreen.getGuiTop() + OFFHAND_SLOT_Y - 5;
        double mouseX = event.getMouseX();
        double mouseY = event.getMouseY();

        if (mouseX >= btnX && mouseX <= btnX + BUTTON_WIDTH && mouseY >= btnY && mouseY <= btnY + BUTTON_HEIGHT) {
            ToggleHandler.toggle();
            NoJbPut.sendToggleSync(ToggleHandler.isEnabled());
            event.setCanceled(true);
        }
    }
}
