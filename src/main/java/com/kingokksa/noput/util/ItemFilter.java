package com.kingokksa.noput.util;

import java.util.List;
import java.util.Locale;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * 名单条目的匹配器，支持四种写法：
 *
 * <ul>
 *   <li>{@code minecraft:torch} —— 精确物品 ID</li>
 *   <li>{@code minecraft:*_planks} / {@code *bucket*} —— glob 通配（{@code *} 任意长度、{@code ?} 单个字符，大小写不敏感）</li>
 *   <li>{@code create:*} —— 整个模组</li>
 *   <li>{@code #minecraft:logs} —— 物品标签（含所有继承该标签的物品）</li>
 * </ul>
 *
 * <p>不写命名空间时既按完整 ID 也比对路径，所以 {@code torch} 能匹配 {@code minecraft:torch}。
 * 解析不了的条目一律视为不匹配，绝不抛异常。
 */
public final class ItemFilter {

    private ItemFilter() {
    }

    /** @return true 表示该物品命中名单里任意一条。 */
    public static boolean matches(List<? extends String> patterns, ItemStack stack) {
        if (patterns == null || patterns.isEmpty() || stack.isEmpty()) {
            return false;
        }
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (id == null) {
            return false;
        }
        String full = id.toString();
        String path = id.getPath();
        for (int i = 0; i < patterns.size(); i++) {
            String pattern = normalize(patterns.get(i));
            if (pattern.isEmpty()) {
                continue;
            }
            if (pattern.charAt(0) == '#') {
                if (matchesTag(pattern.substring(1).trim(), stack)) {
                    return true;
                }
            } else if (pattern.indexOf(':') >= 0) {
                if (glob(pattern, full)) {
                    return true;
                }
            } else if (glob(pattern, full) || glob(pattern, path)) {
                return true;
            }
        }
        return false;
    }

    /** 去掉首尾空白并转小写；解析不了的条目返回空串。 */
    public static String normalize(String raw) {
        return raw == null ? "" : raw.trim().toLowerCase(Locale.ROOT);
    }

    /**
     * 1.16.5 没有 {@code TagKey}，只能用 {@code ItemTags} 的标签集合按名字查。
     *
     * <p>这里**不能**用 {@code ItemTags.bind(String)}：它对未注册的标签返回 null，
     * 之后调用 {@code contains()} 会抛 {@code IllegalStateException: Tag X used before it was bound}，
     * 也就是配置里写错一个标签就能把服务端搞崩。{@code getTagOrEmpty} 对未知标签返回空标签，是安全的。
     */
    private static boolean matchesTag(String raw, ItemStack stack) {
        ResourceLocation tagId = parseId(raw);
        if (tagId == null) {
            return false;
        }
        ITag<Item> tag = ItemTags.getAllTags().getTagOrEmpty(tagId);
        return tag != null && stack.getItem().is(tag);
    }

    /** 解析一个物品/标签 ID，缺命名空间时补 {@code minecraft:}。 */
    public static ResourceLocation parseId(String raw) {
        String s = raw == null ? "" : raw.trim().toLowerCase(Locale.ROOT);
        if (s.isEmpty()) {
            return null;
        }
        return ResourceLocation.tryParse(s.indexOf(':') >= 0 ? s : "minecraft:" + s);
    }

    /**
     * glob 匹配：{@code *} 匹配任意长度（含空），{@code ?} 匹配恰好一个字符，大小写不敏感。
     */
    public static boolean glob(String pattern, String text) {
        String p = pattern.toLowerCase(Locale.ROOT);
        String t = text.toLowerCase(Locale.ROOT);
        int pn = p.length();
        int tn = t.length();
        int pi = 0;
        int ti = 0;
        int star = -1;
        int mark = 0;
        while (ti < tn) {
            if (pi < pn && (p.charAt(pi) == '?' || p.charAt(pi) == t.charAt(ti))) {
                pi++;
                ti++;
            } else if (pi < pn && p.charAt(pi) == '*') {
                star = pi++;
                mark = ti;
            } else if (star >= 0) {
                pi = star + 1;
                ti = ++mark;
            } else {
                return false;
            }
        }
        while (pi < pn && p.charAt(pi) == '*') {
            pi++;
        }
        return pi == pn;
    }
}
