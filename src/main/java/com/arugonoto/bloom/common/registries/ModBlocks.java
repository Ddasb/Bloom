package com.arugonoto.bloom.common.registries;

import com.arugonoto.bloom.Bloom;
import com.arugonoto.bloom.common.blocks.MyceliumNucleusBlock;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Bloom.MODID);

    public static final DeferredBlock<Block> MYCELIUM_NUCLEUS_BLOCK = BLOCKS.register(
        "mycelium_nucleus_block", 
        registryName -> new MyceliumNucleusBlock(
            BlockBehaviour.Properties.of()
            .sound(SoundType.MUD)
            .setId(ResourceKey.create(Registries.BLOCK, registryName))
            .strength(2.0f).noOcclusion() 
        )
    );

    public static void register(IEventBus event) {
        BLOCKS.register(event);
    }
}
