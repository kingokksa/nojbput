package com.kingokksa.noput.config;

import java.util.Locale;

import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.TranslatableEnum;

/**
 * 名单模式 / List mode.
 *
 * <p>判定含义（{@code matches} = 「该手命中拦截条件」）：
 * <ul>
 *   <li>{@link #BLACKLIST}：物品在名单内 → 命中（拦截）；不在名单内 → 不命中（放行）。</li>
 *   <li>{@link #WHITELIST}：物品在名单内 → 不命中（放行，作为例外）；不在名单内 → 命中（拦截）。</li>
 * </ul>
 *
 * <p>空名单视为「不参与过滤」，一律命中（不产生任何例外）。
 *
 * <p>实现 {@link TranslatableEnum} 是为了让 NeoForge 自带的配置界面把下拉值显示成
 * 「白名单 / 黑名单」而不是字面量 {@code WHITELIST} / {@code BLACKLIST}。
 */
public enum ListMode implements TranslatableEnum {
    WHITELIST,
    BLACKLIST;

    @Override
    public Component getTranslatedName() {
        return Component.translatable("nojbput.listmode." + name().toLowerCase(Locale.ROOT));
    }
}
