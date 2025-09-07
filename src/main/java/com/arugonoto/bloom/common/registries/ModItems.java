package com.arugonoto.bloom.common.registries;

import com.arugonoto.bloom.Bloom;
import com.arugonoto.bloom.common.items.EcoGogglesItem;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Bloom.MODID);

    public static final DeferredItem<Item> SPORES = ITEMS.registerItem("spores", Item::new, new Item.Properties());

    public static final DeferredItem<Item> ECO_GOGGLES = ITEMS.register(
        "eco_goggles", 
        registryName -> new EcoGogglesItem(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, registryName)))
    );

    public static final DeferredHolder<Item, Item> MYCELIUM_NUCLEUS = ITEMS.register(
        "mycelium_nucleus", 
        registryName -> new BlockItem(ModBlocks.MYCELIUM_NUCLEUS_BLOCK.get(), new Item.Properties().setId(ResourceKey.create(Registries.ITEM, registryName)))
    );

    public static void register(IEventBus event) {
        ITEMS.register(event);
    }
}
