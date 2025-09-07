package com.arugonoto.bloom.common.registries;

import java.util.function.Supplier;

import com.arugonoto.bloom.Bloom;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Bloom.MODID);
    
    public static final Supplier<CreativeModeTab> bloom_tab =
        CREATIVE_MODE_TABS.register(
            "bloom_tab", 
            () -> CreativeModeTab.builder()
                .title(Component.translatable("itemGroup." + Bloom.MODID + ".tab"))
                .icon(() -> new ItemStack(ModItems.SPORES.get()))
                .displayItems((pParameters, pOutput) -> {
                    pOutput.accept(ModItems.SPORES.get());
                    pOutput.accept(ModItems.ECO_GOGGLES.get());
                    pOutput.accept(ModItems.MYCELIUM_NUCLEUS.get());
                }).build()
        );

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
