package com.arugonoto.bloom.client.renderers;

import com.arugonoto.bloom.common.block_entities.MyceliumNucleusBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class MyceliumNucleusRenderer implements BlockEntityRenderer<MyceliumNucleusBlockEntity> {
    public MyceliumNucleusRenderer(BlockEntityRendererProvider.Context ctx) { }

    @Override
    public void render(MyceliumNucleusBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, Vec3 cameraPos) { }
    
    @Override
    public AABB getRenderBoundingBox(MyceliumNucleusBlockEntity be) {
        var p = be.getBlockPos();
        int r = be.getRadius();
        return new AABB(p.getX() - r, p.getY() - r, p.getZ() - r, p.getX() + 1 + r, p.getY() + 1 + r, p.getZ() + 1 + r);
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }
}
