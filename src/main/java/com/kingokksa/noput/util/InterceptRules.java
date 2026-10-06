package com.kingokksa.noput.util;

import java.util.List;

import com.kingokksa.noput.config.Config;
import com.kingokksa.noput.config.InterceptMode;
import com.kingokksa.noput.config.ListMode;

import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;

public final class InterceptRules {

    public enum Action {
        PLACEMENT,
        USAGE
    }

    private InterceptRules() {
    }

    public static boolean isBlocked(Action action, ItemStack mainHand, ItemStack offHand, InterceptMode mode) {
        if (!Config.masterSwitch()) {
            return false;
        }
        if (action == Action.PLACEMENT) {
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
                return false;
            }
        }
        return handMatches(Config.mainHandListMode(), Config.mainHandItems(), mainHand)
                && handMatches(Config.offHandListMode(), Config.offHandItems(), offHand);
    }

    public static boolean isBlockedOnBlock(ItemStack mainHand, ItemStack offHand, InterceptMode mode) {
        if (isBlocked(Action.PLACEMENT, mainHand, offHand, mode)) {
            return true;
        }
        return !(offHand.getItem() instanceof BlockItem) && isBlocked(Action.USAGE, mainHand, offHand, mode);
    }

    public static boolean handMatches(ListMode mode, List<? extends String> list, ItemStack stack) {
        if (list == null || list.isEmpty()) {
            return true;
        }
        boolean inList = ItemFilter.matches(list, stack);
        return mode == ListMode.BLACKLIST ? inList : !inList;
    }
}
