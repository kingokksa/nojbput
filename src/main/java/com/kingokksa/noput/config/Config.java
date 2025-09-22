package com.kingokksa.noput.config;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public class Config {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.ConfigValue<List<? extends String>> WHITELIST_ITEMS;
    public static final ModConfigSpec.BooleanValue DISABLE_OFFHAND_USAGE;

    static {
        BUILDER.push("NoJBPut Configuration");

        WHITELIST_ITEMS = BUILDER
                .comment("主手持有这些物品时允许副手放置方块 (格式: modid:item_name;如第一项)")
                .defineList("whitelistItems",
                        List.of("minecraft:torch", "minecraft:redstone_torch", "minecraft:lantern", "minecraft:soul_lantern"),
                        obj -> obj instanceof String);

        DISABLE_OFFHAND_USAGE = BUILDER
                .comment("是否同时禁用副手物品的使用(默认可以使用)")
                .define("disableOffhandUsage", false);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}