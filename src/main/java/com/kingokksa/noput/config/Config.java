package com.kingokksa.noput.config;

import java.util.Collections;
import java.util.List;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * No JB Put 的服务端权威配置（COMMON）。
 *
 * <p>COMMON 配置在客户端与服务端都会加载，客户端只用来渲染按钮 / 提示，真正的拦截判定在服务端执行。
 */
public final class Config {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    /** 全局总开关：关掉后本 mod 完全不做任何拦截，背包徽标也会隐藏。 */
    public static final ForgeConfigSpec.BooleanValue MASTER_SWITCH;
    /** 是否允许本 mod 拦截副手放置方块（false 时玩家的循环里不会出现蓝色/红色）。 */
    public static final ForgeConfigSpec.BooleanValue DISABLE_OFFHAND_PLACEMENT;
    /** 是否允许本 mod 拦截副手使用物品（false 时玩家的循环里不会出现黄色/红色）。 */
    public static final ForgeConfigSpec.BooleanValue DISABLE_OFFHAND_USAGE;

    /** 主手名单模式。 */
    public static final ForgeConfigSpec.EnumValue<ListMode> MAIN_HAND_LIST_MODE;
    /** 主手名单（格式 {@code modid:item_name}）。 */
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> MAIN_HAND_ITEMS;

    /** 副手名单模式。 */
    public static final ForgeConfigSpec.EnumValue<ListMode> OFF_HAND_LIST_MODE;
    /** 副手名单（格式 {@code modid:item_name}）。 */
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> OFF_HAND_ITEMS;

    static {
        BUILDER.push("NoJBPut Configuration");

        MASTER_SWITCH = BUILDER
                .comment("全局总开关。false 时本 mod 完全不拦截任何东西，背包里的状态徽标也会隐藏。")
                .define("masterSwitch", true);

        DISABLE_OFFHAND_PLACEMENT = BUILDER
                .comment("是否允许拦截副手放置方块。",
                        "true: 玩家可以在【绿 / 蓝(只拦放置) / 红】之间循环。",
                        "false: 所有含「拦放置」的状态都不可达，循环里不会出现蓝色。")
                .define("disableOffhandPlacement", true);

        DISABLE_OFFHAND_USAGE = BUILDER
                .comment("是否允许拦截副手使用物品（例如副手吃东西、射箭、点火）。",
                        "true: 玩家可以在【绿 / 黄(只拦使用) / 红】之间循环。",
                        "false: 所有含「拦使用」的状态都不可达，循环里不会出现黄色。")
                .define("disableOffhandUsage", false);

        BUILDER.comment("主手名单：根据主手拿着的物品决定是否拦截副手操作。")
                .push("mainHandList");
        MAIN_HAND_LIST_MODE = BUILDER
                .comment("WHITELIST: 主手拿着名单内的物品时【放行】（名单是例外清单）。",
                        "BLACKLIST: 主手拿着名单内的物品时【拦截】（名单是禁止清单）。",
                        "空名单表示本名单不参与过滤。")
                .defineEnum("mode", ListMode.WHITELIST);
        MAIN_HAND_ITEMS = BUILDER
                .comment("主手物品名单，支持四种写法：",
                        "  minecraft:torch      精确物品 ID（省略 minecraft: 也能匹配）",
                        "  minecraft:*_planks   glob 通配，* 匹配任意长度、? 匹配单个字符",
                        "  create:*             整个模组",
                        "  #minecraft:logs      物品标签，含所有继承该标签的物品",
                        "默认值等价于旧版的 whitelistItems：主手拿着火把/灯笼时允许副手放置方块。")
                .defineList("items",
                        java.util.Arrays.asList("minecraft:torch", "minecraft:redstone_torch", "minecraft:lantern", "minecraft:soul_lantern"),
                        obj -> obj instanceof String);
        BUILDER.pop();

        BUILDER.comment("副手名单：根据副手拿着的物品决定是否拦截副手操作。")
                .push("offHandList");
        OFF_HAND_LIST_MODE = BUILDER
                .comment("WHITELIST: 副手拿着名单内的物品时【放行】（只有名单内的物品能被副手使用/放置）。",
                        "BLACKLIST: 副手拿着名单内的物品时【拦截】（只拦名单内的物品）。",
                        "空名单表示本名单不参与过滤。")
                .defineEnum("mode", ListMode.BLACKLIST);
        OFF_HAND_ITEMS = BUILDER
                .comment("副手物品名单，写法同主手名单：精确 ID / glob（* 与 ?）/ #标签。")
                .defineListAllowEmpty(java.util.Collections.singletonList("items"),
                        () -> Collections.<String>emptyList(),
                        obj -> obj instanceof String);
        BUILDER.pop();

        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    private Config() {
    }

    // --- 容错读取：配置未加载时回退到默认值（配置加载前就有代码路径会读） ---

    public static boolean masterSwitch() {
        return get(MASTER_SWITCH, true);
    }

    public static boolean disableOffhandPlacement() {
        return get(DISABLE_OFFHAND_PLACEMENT, true);
    }

    public static boolean disableOffhandUsage() {
        return get(DISABLE_OFFHAND_USAGE, false);
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

    private static <T> T get(ForgeConfigSpec.ConfigValue<T> value, T fallback) {
        try {
            T v = value.get();
            return v == null ? fallback : v;
        } catch (IllegalStateException e) {
            return fallback;
        }
    }

    private static List<? extends String> getList(ForgeConfigSpec.ConfigValue<List<? extends String>> value) {
        try {
            List<? extends String> v = value.get();
            return v == null ? Collections.<String>emptyList() : v;
        } catch (IllegalStateException e) {
            return Collections.emptyList();
        }
    }
}
