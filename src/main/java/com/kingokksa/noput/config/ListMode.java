package com.kingokksa.noput.config;

/**
 * 名单模式。
 *
 * <p>判定含义（{@code matches} = 「该手命中拦截条件」）：
 * <ul>
 *   <li>{@link #BLACKLIST}：物品在名单内 → 命中（拦截）；不在名单内 → 不命中（放行）。</li>
 *   <li>{@link #WHITELIST}：物品在名单内 → 不命中（放行，作为例外）；不在名单内 → 命中（拦截）。</li>
 * </ul>
 *
 * <p>空名单视为「不参与过滤」，一律命中（不产生任何例外）。
 */
public enum ListMode {
    WHITELIST,
    BLACKLIST
}
