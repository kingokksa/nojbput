package com.kingokksa.noput.util;

import java.util.List;
import java.util.Locale;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class ItemFilter {

    private ItemFilter() {
    }

    public static boolean matches(List<? extends String> patterns, ItemStack stack) {
        if (patterns == null || patterns.isEmpty() || stack.isEmpty()) {
            return false;
        }
        Identifier id = BuiltInRegistries.ITEM.getKey(stack.getItem());
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

    public static String normalize(String raw) {
        return raw == null ? "" : raw.trim().toLowerCase(Locale.ROOT);
    }

    private static boolean matchesTag(String raw, ItemStack stack) {
        Identifier tagId = parseId(raw);
        if (tagId == null) {
            return false;
        }
        TagKey<Item> tag = TagKey.create(Registries.ITEM, tagId);
        return stack.is(holder -> holder.is(tag));
    }

    public static Identifier parseId(String raw) {
        String s = raw == null ? "" : raw.trim().toLowerCase(Locale.ROOT);
        if (s.isEmpty()) {
            return null;
        }
        return Identifier.tryParse(s.indexOf(':') >= 0 ? s : "minecraft:" + s);
    }

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
