package com.kingokksa.noput.config;

/** 名单模式。 */
public enum ListMode {
    /** 白名单：名单内的物品【放行】（名单是例外清单），名单外的照常拦截。 */
    WHITELIST,
    /** 黑名单：名单内的物品【拦截】（名单是禁止清单），名单外的放行。 */
    BLACKLIST
}
