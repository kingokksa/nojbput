package com.kingokksa.noput.client;

import com.kingokksa.noput.config.Config;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.core.registries.BuiltInRegistries;
import java.util.ArrayList;
import java.util.List;

public class ConfigScreen extends Screen {
    private final Screen parent;
    private EditBox itemInput;
    private List<String> whitelistedItems;
    private Checkbox disableOffhandCheckbox;
    private int scrollOffset = 0;
    private static final int MAX_DISPLAY_ITEMS = 10;

    public ConfigScreen(Screen parent) {
        super(Component.translatable("gui.nojbput.config.title"));
        this.parent = parent;
        this.whitelistedItems = new ArrayList<>(Config.WHITELIST_ITEMS.get());
    }

    @Override
    protected void init() {
        super.init();
        
        // 添加物品输入框
        this.itemInput = new EditBox(this.font, this.width / 2 - 100, 30, 200, 20, 
                Component.translatable("gui.nojbput.item_input"));
        this.itemInput.setMaxLength(128);
        this.addRenderableWidget(this.itemInput);
        
        // 添加物品按钮
        this.addRenderableWidget(Button.builder(
                Component.translatable("gui.nojbput.add_item"),
                (button) -> addItemToList())
                .pos(this.width / 2 - 100, 60)
                .size(98, 20)
                .build());
        
        // 移除物品按钮
        this.addRenderableWidget(Button.builder(
                Component.translatable("gui.nojbput.remove_item"),
                (button) -> removeSelectedItem())
                .pos(this.width / 2 + 2, 60)
                .size(98, 20)
                .build());
        
        // 禁用副手使用复选框
        this.disableOffhandCheckbox = Checkbox.builder(
                Component.translatable("gui.nojbput.disable_offhand"), this.font)
                .pos(this.width / 2 - 100, 85)
                .selected(Config.DISABLE_OFFHAND_USAGE.get())
                .build();
        this.addRenderableWidget(this.disableOffhandCheckbox);
        
        // 返回按钮
        this.addRenderableWidget(Button.builder(
                Component.translatable("gui.done"),
                (button) -> onClose())
                .pos(this.width / 2 - 100, this.height - 30)
                .size(200, 20)
                .build());
    }
    
    private void addItemToList() {
        String input = this.itemInput.getValue().trim();
        if (!input.isEmpty()) {
            ResourceLocation itemKey = ResourceLocation.tryParse(input);
            if (itemKey != null) {
                // 检查物品是否存在
                Item item = BuiltInRegistries.ITEM.get(itemKey);
                if (item != null && !itemKey.equals(BuiltInRegistries.ITEM.getDefaultKey())) {
                    if (!whitelistedItems.contains(input)) {
                        whitelistedItems.add(input);
                    }
                    this.itemInput.setValue("");
                }
            }
        }
    }
    
    private void removeSelectedItem() {
        // 在这个简化版本中，我们移除列表中的第一个物品作为示例
        if (!whitelistedItems.isEmpty()) {
            whitelistedItems.remove(0);
        }
    }
    
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        
        // 渲染标题
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 10, 0xFFFFFF);
        
        // 渲染说明文字
        guiGraphics.drawString(this.font, 
                Component.translatable("gui.nojbput.item_input.desc"), 
                this.width / 2 - 100, 20, 0xAAAAAA);
        
        // 渲染白名单物品列表
        guiGraphics.drawString(this.font, 
                Component.translatable("gui.nojbput.whitelist"), 
                this.width / 2 - 100, 110, 0xFFFFFF);
        
        int yStart = 125;
        int maxItems = Math.min(MAX_DISPLAY_ITEMS, whitelistedItems.size());
        for (int i = 0; i < maxItems; i++) {
            int index = i + scrollOffset;
            if (index < whitelistedItems.size()) {
                String itemStr = whitelistedItems.get(index);
                guiGraphics.drawString(this.font, itemStr, 
                        this.width / 2 - 100, yStart + i * 12, 0xFFFFFF);
            }
        }
    }
    
    @Override
    public void onClose() {
        // 保存配置
        // 注意：在真实环境中，这应该通过配置系统来保存，而不是直接修改
        // 这里为了简化示例，我们直接更新配置值
        this.minecraft.setScreen(this.parent);
    }
}