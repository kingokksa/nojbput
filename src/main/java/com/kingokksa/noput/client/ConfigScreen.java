package com.kingokksa.noput.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.kingokksa.noput.config.Config;
import com.kingokksa.noput.config.ListMode;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import org.lwjgl.glfw.GLFW;

/**
 * 手写的原版风格配置界面。
 *
 * <p>只有 Forge 1.20.1 / 1.16.5 用它；NeoForge 三个版本用官方自带的
 * {@code ConfigurationScreen}（见各版本 {@code ClientSetup}）。
 *
 * <p>面板画法照抄原版：1px 近黑外框 + 2px 白色高光（上/左）+ 2px {@code #555555} 阴影（下/右）
 * + 平色底 {@code #C6C6C6}，文字 {@code #404040} 不带投影。所有可点控件都用真正的原版
 * {@link Button}（自带 widget/button 贴图与 hover/按下效果），不自己造视觉效果。
 *
 * <p>任何改动都会立刻写回 {@link Config} 并 {@code SPEC.save()} 落盘，所以直接关掉界面也不会丢。
 */
public class ConfigScreen extends Screen {

    // ---------------------------------------------------------------- 布局常量

    private static final int PANEL_W = 320;
    private static final int PANEL_H = 224;

    private static final int COL1_X = 14;
    private static final int COL2_X = 170;
    private static final int COL_W = 140;

    private static final int TITLE_Y = 9;
    private static final int MASTER_Y = 24;
    private static final int PLACEMENT_Y = 44;
    private static final int USAGE_Y = 64;
    private static final int HEADER_Y = 90;
    private static final int MODE_Y = 101;
    private static final int INPUT_Y = 124;
    private static final int ROWS_Y = 147;
    private static final int DONE_Y = 195;

    private static final int ROW_H = 11;
    private static final int VISIBLE_ROWS = 4;

    private static final int BTN_H = 20;
    private static final int INPUT_W = 100;
    private static final int ADD_W = 38;

    // ---------------------------------------------------------------- 原版配色

    private static final int COLOR_BORDER = 0xFF000000;
    private static final int COLOR_HIGHLIGHT = 0xFFFFFFFF;
    private static final int COLOR_SHADOW = 0xFF555555;
    private static final int COLOR_BG = 0xFFC6C6C6;
    private static final int COLOR_TEXT = 0xFF404040;
    private static final int COLOR_LIST_BG = 0xFF9C9C9C;
    private static final int COLOR_REMOVE = 0xFF9B1B1B;

    // ---------------------------------------------------------------- 状态

    private final Screen parent;

    private int left;
    private int top;

    private boolean master;
    private boolean placement;
    private boolean usage;
    private ListMode mainMode;
    private ListMode offMode;
    private final List<String> mainItems = new ArrayList<>();
    private final List<String> offItems = new ArrayList<>();

    private int mainScroll;
    private int offScroll;

    private TextFieldWidget mainInput;
    private TextFieldWidget offInput;

    public ConfigScreen(Screen parent) {
        super(new TranslationTextComponent("gui.nojbput.config.title"));
        this.parent = parent;
        this.master = Config.masterSwitch();
        this.placement = Config.disableOffhandPlacement();
        this.usage = Config.disableOffhandUsage();
        this.mainMode = Config.mainHandListMode();
        this.offMode = Config.offHandListMode();
        this.mainItems.addAll(Config.mainHandItems());
        this.offItems.addAll(Config.offHandItems());
    }

    // ---------------------------------------------------------------- 构建控件

    @Override
    protected void init() {
        left = (width - PANEL_W) / 2;
        top = (height - PANEL_H) / 2;

        int fullW = PANEL_W - COL1_X * 2;

        addButton(new Button(left + COL1_X, top + MASTER_Y, fullW, BTN_H,
                switchLabel("gui.nojbput.config.master", master), b -> {
                    master = !master;
                    b.setMessage(switchLabel("gui.nojbput.config.master", master));
                    apply();
                }));

        addButton(new Button(left + COL1_X, top + PLACEMENT_Y, fullW, BTN_H,
                switchLabel("gui.nojbput.config.placement", placement), b -> {
                    placement = !placement;
                    b.setMessage(switchLabel("gui.nojbput.config.placement", placement));
                    apply();
                }));

        addButton(new Button(left + COL1_X, top + USAGE_Y, fullW, BTN_H,
                switchLabel("gui.nojbput.config.usage", usage), b -> {
                    usage = !usage;
                    b.setMessage(switchLabel("gui.nojbput.config.usage", usage));
                    apply();
                }));

        addButton(new Button(left + COL1_X, top + MODE_Y, COL_W, BTN_H, modeLabel(mainMode), b -> {
            mainMode = other(mainMode);
            b.setMessage(modeLabel(mainMode));
            apply();
        }));

        addButton(new Button(left + COL2_X, top + MODE_Y, COL_W, BTN_H, modeLabel(offMode), b -> {
            offMode = other(offMode);
            b.setMessage(modeLabel(offMode));
            apply();
        }));

        mainInput = addButton(new TextFieldWidget(font, left + COL1_X, top + INPUT_Y, INPUT_W, BTN_H,
                new TranslationTextComponent("gui.nojbput.config.hint")));
        mainInput.setMaxLength(128);

        offInput = addButton(new TextFieldWidget(font, left + COL2_X, top + INPUT_Y, INPUT_W, BTN_H,
                new TranslationTextComponent("gui.nojbput.config.hint")));
        offInput.setMaxLength(128);

        addButton(new Button(left + COL1_X + INPUT_W + 2, top + INPUT_Y, ADD_W, BTN_H,
                new TranslationTextComponent("gui.nojbput.config.add"), b -> addItem(true)));
        addButton(new Button(left + COL2_X + INPUT_W + 2, top + INPUT_Y, ADD_W, BTN_H,
                new TranslationTextComponent("gui.nojbput.config.add"), b -> addItem(false)));

        addButton(new Button(left + PANEL_W / 2 - 40, top + DONE_Y, 80, BTN_H,
                new TranslationTextComponent("gui.nojbput.config.done"), b -> onClose()));
    }

    // ---------------------------------------------------------------- 渲染

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTick) {
        renderBackground(matrix);
        drawPanel(matrix);
        drawList(matrix, left + COL1_X, mainItems, mainScroll);
        drawList(matrix, left + COL2_X, offItems, offScroll);
        super.render(matrix, mouseX, mouseY, partialTick);

        textCentered(matrix, title, left + PANEL_W / 2, top + TITLE_Y);
        text(matrix, header("gui.nojbput.config.mainHand", mainItems.size()), left + COL1_X, top + HEADER_Y);
        text(matrix, header("gui.nojbput.config.offHand", offItems.size()), left + COL2_X, top + HEADER_Y);

        // 输入框上悬停显示名单语法（面板里没地方放说明文字）
        if (overInput(mouseX, mouseY)) {
            renderTooltip(matrix, new TranslationTextComponent("gui.nojbput.config.syntax"), mouseX, mouseY);
        }
    }

    private boolean overInput(double mouseX, double mouseY) {
        return mainInput != null && mainInput.isMouseOver(mouseX, mouseY)
                || offInput != null && offInput.isMouseOver(mouseX, mouseY);
    }

    private void drawPanel(MatrixStack matrix) {
        int x1 = left;
        int y1 = top;
        int x2 = left + PANEL_W;
        int y2 = top + PANEL_H;

        fill(matrix, x1, y1, x2, y2, COLOR_BORDER);
        fill(matrix, x1 + 1, y1 + 1, x2 - 1, y2 - 1, COLOR_BG);
        fill(matrix, x1 + 1, y1 + 1, x2 - 1, y1 + 3, COLOR_HIGHLIGHT);
        fill(matrix, x1 + 1, y1 + 1, x1 + 3, y2 - 1, COLOR_HIGHLIGHT);
        fill(matrix, x1 + 1, y2 - 3, x2 - 1, y2 - 1, COLOR_SHADOW);
        fill(matrix, x2 - 3, y1 + 1, x2 - 1, y2 - 1, COLOR_SHADOW);
    }

    private void drawList(MatrixStack matrix, int colX, List<String> items, int scroll) {
        int x2 = colX + COL_W;
        for (int row = 0; row < VISIBLE_ROWS; row++) {
            int rowY = top + ROWS_Y + row * ROW_H;
            int rowBottom = rowY + ROW_H - 1;

            fill(matrix, colX, rowY, x2, rowBottom, COLOR_BORDER);
            fill(matrix, colX + 1, rowY + 1, x2 - 1, rowBottom - 1, COLOR_LIST_BG);

            int index = scroll + row;
            if (index >= items.size()) {
                continue;
            }
            String text = font.plainSubstrByWidth(items.get(index), COL_W - 16);
            font.draw(matrix, text, (float) (colX + 4), (float) (rowY + 2), COLOR_TEXT);
            font.draw(matrix, "x", (float) (x2 - 9), (float) (rowY + 2), COLOR_REMOVE);
        }
    }

    /** 无投影文字（原版界面里的正文都是无投影的）。 */
    private void text(MatrixStack matrix, ITextComponent value, int x, int y) {
        font.draw(matrix, value, (float) x, (float) y, COLOR_TEXT);
    }

    private void textCentered(MatrixStack matrix, ITextComponent value, int centerX, int y) {
        font.draw(matrix, value, (float) (centerX - font.width(value) / 2), (float) y, COLOR_TEXT);
    }

    // ---------------------------------------------------------------- 交互

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            int hit = rowHit(mouseX, mouseY, left + COL1_X, mainScroll, mainItems.size());
            if (hit >= 0) {
                mainItems.remove(hit);
                mainScroll = clampScroll(mainScroll, mainItems.size());
                apply();
                return true;
            }
            hit = rowHit(mouseX, mouseY, left + COL2_X, offScroll, offItems.size());
            if (hit >= 0) {
                offItems.remove(hit);
                offScroll = clampScroll(offScroll, offItems.size());
                apply();
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (overList(mouseX, mouseY, left + COL1_X) && mainItems.size() > VISIBLE_ROWS) {
            mainScroll = clampScroll(mainScroll - (int) Math.signum(delta), mainItems.size());
            return true;
        }
        if (overList(mouseX, mouseY, left + COL2_X) && offItems.size() > VISIBLE_ROWS) {
            offScroll = clampScroll(offScroll - (int) Math.signum(delta), offItems.size());
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            if (mainInput != null && mainInput.isFocused()) {
                addItem(true);
                return true;
            }
            if (offInput != null && offInput.isFocused()) {
                addItem(false);
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onClose() {
        if (minecraft != null) {
            minecraft.setScreen(parent);
        } else {
            super.onClose();
        }
    }

    // ---------------------------------------------------------------- 内部

    private void addItem(boolean main) {
        TextFieldWidget box = main ? mainInput : offInput;
        List<String> items = main ? mainItems : offItems;
        if (box == null) {
            return;
        }
        String raw = box.getValue().trim().toLowerCase(Locale.ROOT);
        if (raw.isEmpty()) {
            return;
        }
        if (!items.contains(raw)) {
            items.add(raw);
        }
        box.setValue("");
        int scroll = Math.max(0, items.size() - VISIBLE_ROWS);
        if (main) {
            mainScroll = scroll;
        } else {
            offScroll = scroll;
        }
        apply();
    }

    /** 把界面上的值写回配置并落盘。 */
    private void apply() {
        Config.MASTER_SWITCH.set(master);
        Config.DISABLE_OFFHAND_PLACEMENT.set(placement);
        Config.DISABLE_OFFHAND_USAGE.set(usage);
        Config.MAIN_HAND_LIST_MODE.set(mainMode);
        Config.MAIN_HAND_ITEMS.set(new ArrayList<>(mainItems));
        Config.OFF_HAND_LIST_MODE.set(offMode);
        Config.OFF_HAND_ITEMS.set(new ArrayList<>(offItems));
        try {
            Config.SPEC.save();
        } catch (RuntimeException ignored) {
            // 配置还没加载完成时（理论上到不了这里）忽略即可，值已经在内存里生效
        }
    }

    private int rowHit(double mouseX, double mouseY, int colX, int scroll, int size) {
        if (mouseX < colX || mouseX >= colX + COL_W) {
            return -1;
        }
        for (int row = 0; row < VISIBLE_ROWS; row++) {
            int rowY = top + ROWS_Y + row * ROW_H;
            if (mouseY >= rowY && mouseY < rowY + ROW_H - 1) {
                int index = scroll + row;
                return index < size ? index : -1;
            }
        }
        return -1;
    }

    private boolean overList(double mouseX, double mouseY, int colX) {
        return mouseX >= colX && mouseX < colX + COL_W
                && mouseY >= top + ROWS_Y && mouseY < top + ROWS_Y + VISIBLE_ROWS * ROW_H;
    }

    private static int clampScroll(int scroll, int size) {
        return Math.max(0, Math.min(scroll, Math.max(0, size - VISIBLE_ROWS)));
    }

    private static ListMode other(ListMode mode) {
        return mode == ListMode.WHITELIST ? ListMode.BLACKLIST : ListMode.WHITELIST;
    }

    private static ITextComponent modeLabel(ListMode mode) {
        return new TranslationTextComponent(mode == ListMode.WHITELIST
                ? "gui.nojbput.config.whitelist"
                : "gui.nojbput.config.blacklist");
    }

    private static ITextComponent switchLabel(String key, boolean value) {
        return new TranslationTextComponent(key).append(": ")
                .append(new TranslationTextComponent(value ? "gui.nojbput.on" : "gui.nojbput.off"));
    }

    private static ITextComponent header(String key, int count) {
        return new TranslationTextComponent(key).append(" (" + count + ")");
    }
}
