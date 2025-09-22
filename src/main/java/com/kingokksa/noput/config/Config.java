package com.kingokksa.noput.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class Config {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> WHITELIST_ITEMS;
    public static final ForgeConfigSpec.BooleanValue DISABLE_OFFHAND_USAGE;

    static {
        BUILDER.push("NoJBPut Configuration");

        WHITELIST_ITEMS = BUILDER
                .comment("主手持有这些物品时允许副手放置方块 (格式: modid:item_name;如第一项)")
                .defineList("whitelistItems",
                        List.of("minecraft:example"),
                        obj -> obj instanceof String);

        DISABLE_OFFHAND_USAGE = BUILDER
                .comment("是否同时禁用副手物品的使用(默认可以使用)")
                .define("disableOffhandUsage", false);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}