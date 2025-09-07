package com.arugonoto.bloom.common.items;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class EcoGogglesItem extends Item {
    public EcoGogglesItem(Properties properties) {
        super(properties.equippable(EquipmentSlot.HEAD));
    }

    public static boolean isActive(Player player) {
        if (player == null) return false;

        ItemStack head = player.getItemBySlot(EquipmentSlot.HEAD);
        if (head.getItem() instanceof EcoGogglesItem) return true;
        
        return false;
    }
    
}
