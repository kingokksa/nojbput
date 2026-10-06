package com.kingokksa.noput.util;

import java.util.List;

import com.kingokksa.noput.config.Config;
import com.kingokksa.noput.config.InterceptMode;
import com.kingokksa.noput.config.ListMode;

import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;

/**
 * 纯逻辑的拦截判定，不依赖任何事件/平台 API。
 *
 * <p>最终判定：{@code 总开关 && 配置允许拦该动作 && 玩家状态里有该拦截位 && (放置时副手必须是方块)
 * && 主手命中 && 副手命中}。
 */
public final class InterceptRules {

    /** 被拦截的动作类型。 */
    public enum Action {
        /** 副手放置方块。 */
        PLACEMENT,
        /** 副手使用物品。 */
        USAGE
    }

    private InterceptRules() {
    }

    /**
     * @param action   要判定的动作
     * @param mainHand 主手物品
     * @param offHand  副手物品
     * @param mode     玩家当前的拦截状态（服务端权威值）
     * @return true 表示应该拦截
     */
    public static boolean isBlocked(Action action, ItemStack mainHand, ItemStack offHand, InterceptMode mode) {
        if (!Config.masterSwitch()) {
            return false;
        }
        if (action == Action.PLACEMENT) {
            // 配置允许 + 玩家状态打开，两个都要
            if (!Config.disableOffhandPlacement() || !mode.blocksPlacement()) {
                return false;
            }
            if (!(offHand.getItem() instanceof BlockItem)) {
                return false;
            }
        } else {
            if (!Config.disableOffhandUsage() || !mode.blocksUsage()) {
                return false;
            }
            if (offHand.isEmpty()) {
                // 空手没有「使用」可言，别把开箱子之类也拦了
                return false;
            }
        }
        return handMatches(Config.mainHandListMode(), Config.mainHandItems(), mainHand)
                && handMatches(Config.offHandListMode(), Config.offHandItems(), offHand);
    }

    /**
     * 判定某个手的物品是否「命中拦截条件」。
     *
     * @return true = 命中（该手不提供例外）；false = 不命中（该手提供例外，放行）
     */
    public static boolean handMatches(ListMode mode, List<? extends String> list, ItemStack stack) {
        if (list == null || list.isEmpty()) {
            // 空名单不参与过滤：不产生任何例外
            return true;
        }
        // 条目支持精确 ID、glob 通配（* 与 ?）以及 #标签，见 ItemFilter
        boolean inList = ItemFilter.matches(list, stack);
        return mode == ListMode.BLACKLIST ? inList : !inList;
    }
}
