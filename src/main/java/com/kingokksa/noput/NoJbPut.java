package com.kingokksa.noput;

import com.kingokksa.noput.config.Config;
import com.kingokksa.noput.util.ToggleHandler;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.ForgeRegistries;


@Mod(NoJbPut.MODID)
public class NoJbPut {
    public static final String MODID = "nojbput";
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(MODID, "toggle_sync"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);

    static {
        CHANNEL.registerMessage(
                0,
                ToggleSyncMessage.class,
                ToggleSyncMessage::encode,
                ToggleSyncMessage::decode,
                ToggleSyncMessage::handle);
    }

    public NoJbPut() {
        MinecraftForge.EVENT_BUS.register(this);
        ModLoadingContext.get().registerConfig(Type.COMMON, Config.SPEC);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!ToggleHandler.isEnabled())
            return;

        Player player = event.getEntity();
        
        // 检查主手物品是否在白名单中
        if (isWhitelisted(player.getMainHandItem())) {
            return;
        }
        
        if (event.getHand() == InteractionHand.OFF_HAND &&
                player.getOffhandItem().getItem() instanceof BlockItem) {
            event.setCanceled(true);
        }
    }
    
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (!ToggleHandler.isEnabled())
            return;
            
        // 如果配置中启用了禁用副手使用，则阻止副手物品使用
        if (Config.DISABLE_OFFHAND_USAGE.get() && 
            event.getHand() == InteractionHand.OFF_HAND) {
            // 检查主手物品是否在白名单中
            if (isWhitelisted(event.getEntity().getMainHandItem())) {
                return;
            }
            
            event.setCanceled(true);
            return;
        }

        Player player = event.getEntity();
        // 检查主手物品是否在白名单中
        if (isWhitelisted(player.getMainHandItem())) {
            return;
        }
        
        if (event.getHand() == InteractionHand.OFF_HAND &&
                player.getOffhandItem().getItem() instanceof BlockItem) {
            event.setCanceled(true);
        }
    }
    
    /**
     * 检查给定的物品是否在白名单中
     * @param itemStack 要检查的物品堆
     * @return 如果物品在白名单中返回true，否则返回false
     */
    private boolean isWhitelisted(ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return false;
        }
        
        Item item = itemStack.getItem();
        ResourceLocation itemKey = ForgeRegistries.ITEMS.getKey(item);
        if (itemKey == null) {
            return false;
        }
        
        return Config.WHITELIST_ITEMS.get().contains(itemKey.toString());
    }
}