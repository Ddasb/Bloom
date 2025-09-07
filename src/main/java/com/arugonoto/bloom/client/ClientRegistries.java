package com.arugonoto.bloom.client;

import com.arugonoto.bloom.Bloom;
import com.arugonoto.bloom.client.renderers.MyceliumNucleusRenderer;
import com.arugonoto.bloom.common.registries.ModBlockEntities;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = Bloom.MODID)
public class ClientRegistries {
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.MYCELIUM_NUCLEUS_BLOCK_ENTITY.get(), MyceliumNucleusRenderer::new);
    }
}
