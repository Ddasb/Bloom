package com.arugonoto.bloom;

import org.slf4j.Logger;

import com.arugonoto.bloom.common.registries.ModBlockEntities;
import com.arugonoto.bloom.common.registries.ModBlocks;
import com.arugonoto.bloom.common.registries.ModCreativeTabs;
import com.arugonoto.bloom.common.registries.ModItems;
import com.arugonoto.bloom.config.Config;
import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;

@Mod(Bloom.MODID)
public class Bloom {
    public static final String MODID = "bloom";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Bloom(IEventBus modEventBus, ModContainer modContainer) {
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModItems.register(modEventBus);
        ModCreativeTabs.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
}
