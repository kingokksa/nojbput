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

/**
 * 背包界面里副手槽右上角的 4x4 状态徽标。
 *
 * <p>位置沿用旧版公式：{@code guiLeft + 77 + 16 - 4 + 4 = guiLeft + 93}、
 * {@code guiTop + 65 - 4 = guiTop + 61}。与旧版不同的是这里直接用
 * {@code InventoryScreen#getGuiLeft()}/{@code getGuiTop()}，所以配方书展开时
 * 也跟着面板一起移动，不需要旧版那套「猜宽度」的算法。
 *
 * <p>颜色就是当前拦截状态，四种颜色都是平色，用 {@code fill} 画 4x4 实心方块，
 * 所以不需要任何贴图（顺带修掉了本分支一直缺 switch.png 的问题）：
 * <ul>
 *   <li>绿 {@code #22B14C} 全放行</li>
 *   <li>蓝 {@code #2B6CD4} 只拦副手放置</li>
 *   <li>黄 {@code #FFD400} 只拦副手使用</li>
 *   <li>红 {@code #ED1C24} 全拦</li>
 * </ul>
 * 循环范围由配置决定（见 {@link InterceptMode#isAvailable()}）。
 * 服务器总开关关掉、或两种拦截都被配置禁止时整个徽标隐藏。
 *
 * <p>点击热区严格等于图标本身（4x4），鼠标事件走 {@code GuiScreenEvent.MouseClickedEvent.Pre}，
 * 命中后取消事件，避免原版同时把这次点击当成对副手槽的操作。
 */
@Mod.EventBusSubscriber(modid = NoJbPut.MODID, value = Dist.CLIENT)
public final class InventoryToggleOverlay {

    /** 徽标边长，同时也是点击热区边长。 */
    private static final int SIZE = 4;
    /** 副手槽相对坐标是 (77, 65)，徽标贴在它的右上角：77 + 16 - 4 + 4 = 93。 */
    private static final int OFFSET_X = 93;
    private static final int OFFSET_Y = 61;

    /** 绿色：全放行。 */
    private static final int COLOR_ALLOW = 0xFF22B14C;
    /** 蓝色：只拦副手放置。 */
    private static final int COLOR_BLOCK_PLACEMENT = 0xFF2B6CD4;
    /** 黄色：只拦副手使用。 */
    private static final int COLOR_BLOCK_USAGE = 0xFFFFD400;
    /** 红色：全拦。 */
    private static final int COLOR_BLOCK_ALL = 0xFFED1C24;

    private InventoryToggleOverlay() {
    }

    /** 服务器总开关关掉、或没有任何可循环状态时不显示。 */
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
