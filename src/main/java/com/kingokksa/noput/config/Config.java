package com.kingokksa.noput.config;

import java.util.Collections;
import java.util.List;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class Config {

    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.BooleanValue MASTER_SWITCH;
    public static final ModConfigSpec.BooleanValue DISABLE_OFFHAND_PLACEMENT;
    public static final ModConfigSpec.BooleanValue DISABLE_OFFHAND_USAGE;

    public static final ModConfigSpec.EnumValue<ListMode> MAIN_HAND_LIST_MODE;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> MAIN_HAND_ITEMS;

    public static final ModConfigSpec.EnumValue<ListMode> OFF_HAND_LIST_MODE;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> OFF_HAND_ITEMS;

    static {
        BUILDER.translation("nojbput.configuration.section").push("NoJBPut Configuration");

        MASTER_SWITCH = BUILDER
                .comment("Master switch. When false this mod intercepts nothing at all and the inventory badge is hidden.",
                        "全局总开关。false 时本 mod 完全不拦截任何东西，背包里的状态徽标也会隐藏。")
                .define("masterSwitch", true);

        DISABLE_OFFHAND_PLACEMENT = BUILDER
                .comment("Whether this mod may intercept offhand block placement.",
                        "是否允许拦截副手放置方块。",
                        "true: the player can cycle through [green / blue (placement only) / red].",
                        "true: 玩家可以在【绿 / 蓝(只拦放置) / 红】之间循环。",
                        "false: every state that blocks placement becomes unreachable, so blue never appears.",
                        "false: 所有含「拦放置」的状态都不可达，循环里不会出现蓝色。")
                .define("disableOffhandPlacement", true);

        DISABLE_OFFHAND_USAGE = BUILDER
                .comment("Whether this mod may intercept offhand item usage (eating, shooting, lighting a fire, ...).",
                        "是否允许拦截副手使用物品（例如副手吃东西、射箭、点火）。",
                        "true: the player can cycle through [green / yellow (usage only) / red].",
                        "true: 玩家可以在【绿 / 黄(只拦使用) / 红】之间循环。",
                        "false: every state that blocks usage becomes unreachable, so yellow never appears.",
                        "false: 所有含「拦使用」的状态都不可达，循环里不会出现黄色。")
                .define("disableOffhandUsage", true);

        BUILDER.comment("Main-hand list: decides whether offhand actions are intercepted, based on the item held in the main hand.",
                "主手名单：根据主手拿着的物品决定是否拦截副手操作。")
                .push("mainHandList");
        MAIN_HAND_LIST_MODE = BUILDER
                .comment("WHITELIST: holding a listed item in the main hand ALLOWS the offhand action (the list is an exception list).",
                        "WHITELIST: 主手拿着名单内的物品时【放行】（名单是例外清单）。",
                        "BLACKLIST: holding a listed item in the main hand BLOCKS the offhand action (the list is a block list).",
                        "BLACKLIST: 主手拿着名单内的物品时【拦截】（名单是禁止清单）。",
                        "An empty list means this filter is not applied at all.",
                        "空名单表示本名单不参与过滤。")
                .defineEnum("mode", ListMode.WHITELIST);
        MAIN_HAND_ITEMS = BUILDER
                .comment("Main-hand item list. Four notations are supported:",
                        "主手物品名单，支持四种写法：",
                        "  minecraft:torch      exact item id (the minecraft: prefix may be omitted)",
                        "  minecraft:torch      精确物品 ID（省略 minecraft: 也能匹配）",
                        "  minecraft:*_planks   glob wildcard: * = any length, ? = exactly one character",
                        "  minecraft:*_planks   glob 通配，* 匹配任意长度、? 匹配单个字符",
                        "  create:*             every item of that mod",
                        "  create:*             整个模组",
                        "  #minecraft:logs      item tag, matches every item in that tag",
                        "  #minecraft:logs      物品标签，含所有继承该标签的物品",
                        "Empty by default, so nothing is exempted out of the box.",
                        "默认为空，开箱即用时不豁免任何物品。")
                .defineListAllowEmpty(List.of("items"),
                        () -> Collections.<String>emptyList(),
                        () -> "minecraft:torch",
                        obj -> obj instanceof String);
        BUILDER.pop();

        BUILDER.comment("Offhand list: decides whether offhand actions are intercepted, based on the item held in the offhand.",
                "副手名单：根据副手拿着的物品决定是否拦截副手操作。")
                .push("offHandList");
        OFF_HAND_LIST_MODE = BUILDER
                .comment("WHITELIST: only listed items may be used or placed by the offhand; everything else is blocked.",
                        "WHITELIST: 副手拿着名单内的物品时【放行】（只有名单内的物品能被副手使用/放置）。",
                        "BLACKLIST: only listed items are blocked; everything else may be used or placed by the offhand.",
                        "BLACKLIST: 副手拿着名单内的物品时【拦截】（只拦名单内的物品）。",
                        "An empty list means this filter is not applied at all.",
                        "空名单表示本名单不参与过滤。")
                .defineEnum("mode", ListMode.BLACKLIST);
        OFF_HAND_ITEMS = BUILDER
                .comment("Offhand item list. Same notation as the main-hand list: exact id / glob (* and ?) / #tag.",
                        "副手物品名单，写法同主手名单：精确 ID / glob（* 与 ?）/ #标签。")
                .defineListAllowEmpty(List.of("items"), () -> Collections.<String>emptyList(), () -> "minecraft:torch",
                        obj -> obj instanceof String);
        BUILDER.pop();

        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    private Config() {
    }

    public static boolean masterSwitch() {
        return get(MASTER_SWITCH, true);
    }

    public static boolean disableOffhandPlacement() {
        return get(DISABLE_OFFHAND_PLACEMENT, true);
    }

    public static boolean disableOffhandUsage() {
        return get(DISABLE_OFFHAND_USAGE, true);
    }

    public static ListMode mainHandListMode() {
        return get(MAIN_HAND_LIST_MODE, ListMode.WHITELIST);
    }

    public static List<? extends String> mainHandItems() {
        return getList(MAIN_HAND_ITEMS);
    }

    public static ListMode offHandListMode() {
        return get(OFF_HAND_LIST_MODE, ListMode.BLACKLIST);
    }

    public static List<? extends String> offHandItems() {
        return getList(OFF_HAND_ITEMS);
    }

    private static <T> T get(ModConfigSpec.ConfigValue<T> value, T fallback) {
        try {
            T v = value.get();
            return v == null ? fallback : v;
        } catch (IllegalStateException e) {
            return fallback;
        }
    }

    private static List<? extends String> getList(ModConfigSpec.ConfigValue<List<? extends String>> value) {
        try {
            List<? extends String> v = value.get();
            return v == null ? Collections.<String>emptyList() : v;
        } catch (IllegalStateException e) {
            return Collections.emptyList();
        }
    }
}
