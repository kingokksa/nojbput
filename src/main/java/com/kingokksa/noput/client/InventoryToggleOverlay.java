package com.kingokksa.noput.client;

import com.kingokksa.noput.NoJbPut;
import com.kingokksa.noput.config.Config;
import com.kingokksa.noput.config.InterceptMode;
import com.kingokksa.noput.util.ModState;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = NoJbPut.MODID, value = Dist.CLIENT)
public final class InventoryToggleOverlay {

    private static final int SIZE = 4;
    private static final int OFFSET_X = 93;
    private static final int OFFSET_Y = 61;

    private static final int COLOR_ALLOW = 0xFF22B14C;
    private static final int COLOR_BLOCK_PLACEMENT = 0xFF2B6CD4;
    private static final int COLOR_BLOCK_USAGE = 0xFFFFD400;
    private static final int COLOR_BLOCK_ALL = 0xFFED1C24;

    private InventoryToggleOverlay() {
    }

    private static boolean hidden() {
        return !Config.masterSwitch() || !InterceptMode.anyAvailable();
    }

    private static int color(InterceptMode mode) {
        switch (mode) {
            case BLOCK_PLACEMENT:
                return COLOR_BLOCK_PLACEMENT;
            case BLOCK_USAGE:
                return COLOR_BLOCK_USAGE;
            case BLOCK_ALL:
                return COLOR_BLOCK_ALL;
            case ALLOW:
            default:
                return COLOR_ALLOW;
        }
    }

    @SubscribeEvent
    public static void onDrawScreen(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (hidden() || !(event.getGui() instanceof InventoryScreen)) {
            return;
        }
        InventoryScreen screen = (InventoryScreen) event.getGui();
        int x = screen.getGuiLeft() + OFFSET_X;
        int y = screen.getGuiTop() + OFFSET_Y;

        MatrixStack matrix = event.getMatrixStack();
        InterceptMode mode = ModState.getClientMode();
        AbstractGui.fill(matrix, x, y, x + SIZE, y + SIZE, color(mode));

        if (inside(event.getMouseX(), event.getMouseY(), x, y)) {
            screen.renderTooltip(matrix, new TranslationTextComponent("message.nojbput.mode",
                    new TranslationTextComponent(ClientToggle.langKey(mode))),
                    event.getMouseX(), event.getMouseY());
        }
    }

    @SubscribeEvent
    public static void onMouseClicked(GuiScreenEvent.MouseClickedEvent.Pre event) {
        if (hidden() || event.getButton() != GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            return;
        }
        if (!(event.getGui() instanceof InventoryScreen)) {
            return;
        }
        InventoryScreen screen = (InventoryScreen) event.getGui();
        int x = screen.getGuiLeft() + OFFSET_X;
        int y = screen.getGuiTop() + OFFSET_Y;
        if (inside(event.getMouseX(), event.getMouseY(), x, y)) {
            ClientToggle.cycle();
            event.setCanceled(true);
        }
    }

    private static boolean inside(double mouseX, double mouseY, int x, int y) {
        return mouseX >= x && mouseX < x + SIZE && mouseY >= y && mouseY < y + SIZE;
    }
}
