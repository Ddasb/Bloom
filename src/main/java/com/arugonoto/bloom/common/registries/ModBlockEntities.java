package com.arugonoto.bloom.common.registries;

import java.util.function.Supplier;

import com.arugonoto.bloom.Bloom;
import com.arugonoto.bloom.common.block_entities.MyceliumNucleusBlockEntity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Bloom.MODID);

    public static final Supplier<BlockEntityType<MyceliumNucleusBlockEntity>> MYCELIUM_NUCLEUS_BLOCK_ENTITY = BLOCK_ENTITIES.register(
        "boiler_block_entity",
        () -> new BlockEntityType<MyceliumNucleusBlockEntity>(
            MyceliumNucleusBlockEntity::new,
            false,
            ModBlocks.MYCELIUM_NUCLEUS_BLOCK.get()
        )
    );

    public static void register(IEventBus event) {
        BLOCK_ENTITIES.register(event);
    }
}
