package com.arugonoto.bloom.client;

import org.joml.Matrix4f;

import com.arugonoto.bloom.Bloom;
import com.arugonoto.bloom.common.core.AbstractNucleusBlockEntity;
import com.arugonoto.bloom.common.items.EcoGogglesItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = Bloom.MODID)
public class NucleusOverlay {
    private static final float EDGE_FADE = 1f;

    private static final float EPS_TOP = 0.0015f;
    private static final float EPS_SIDE = 0.0015f;

    private static final RenderType ECO_RT = RenderType.debugQuads();

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent.AfterParticles event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        ClientLevel level = mc.level;

        if(player == null || level == null) return;
        if (!EcoGogglesItem.isActive(player)) return;

        final PoseStack pose = event.getPoseStack();
        final Camera cam = mc.gameRenderer.getMainCamera();
        pose.pushPose();
        pose.translate(-cam.getPosition().x, -cam.getPosition().y, -cam.getPosition().z);

        final MultiBufferSource.BufferSource buffers = mc.renderBuffers().bufferSource();
        final VertexConsumer vc = buffers.getBuffer(ECO_RT);

        event.getLevelRenderer().iterateVisibleBlockEntities(be -> {
            if (!(be instanceof AbstractNucleusBlockEntity nucleus)) return;

            final int radius = nucleus.getRadius();
            final BlockPos center = be.getBlockPos();
            final double cx = center.getX() + 0.5;
            final double cz = center.getZ() + 0.5;

            int argb = nucleus.getOverlayColor();
            final float baseA = ((argb >>> 24) & 0xFF) / 255f;
            final float baseR = ((argb >>> 16) & 0xFF) / 255f;
            final float baseG = ((argb >>>  8) & 0xFF) / 255f;
            final float baseB = (argb & 0xFF) / 255f;

            AABB haloBox = new AABB(
                center.getX() - radius, center.getY() - 1, center.getZ() - radius,
                center.getX() + 1 + radius, center.getY() + 2, center.getZ() + 1 + radius
            );
            if (!event.getFrustum().isVisible(haloBox)) return;

            int minX = center.getX() - radius;
            int maxX = center.getX() + radius;
            int minZ = center.getZ() - radius;
            int maxZ = center.getZ() + radius;

            Matrix4f m = pose.last().pose();

            for (int x = minX; x <= maxX; x++) {
                double dx = (x + 0.5) - cx;
                double dx2 = dx * dx;

                for (int z = minZ; z <= maxZ; z++) {
                    double dz = (z + 0.5) - cz;
                    if (dx2 + dz * dz > radius * radius) continue;

                    // Hauteur de surface
                    int ySurf = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z) - 1;

                    BlockPos pos = new BlockPos(x, ySurf, z);
                    
                    if (pos.equals(center)) continue;

                    BlockState state = level.getBlockState(pos);
                    if (state.isAir()) continue;

                    VoxelShape shape = state.getShape(level, pos);
                    if (shape.isEmpty()) continue;

                    // Dégradé bord
                    float falloff = 1.0f;
                    if (EDGE_FADE > 0f) {
                        float dist = (float) Math.sqrt(dx2 + dz * dz);
                        falloff = Math.max(0f, Math.min(1f, (radius - dist) / EDGE_FADE));
                    }

                    float a = baseA * falloff;
                    if (a <= 0.005f) continue;

                    int packedLight = LevelRenderer.getLightColor(level, pos);

                    for (AABB box : shape.toAabbs()) {
                        // coords locaux dans le bloc -> monde
                        float x1 = (float) (pos.getX() + box.minX);
                        float x2 = (float) (pos.getX() + box.maxX);
                        float y1 = (float) (pos.getY() + box.minY);
                        float y2 = (float) (pos.getY() + box.maxY);
                        float z1 = (float) (pos.getZ() + box.minZ);
                        float z2 = (float) (pos.getZ() + box.maxZ);

                        // FACE TOP uniquement si bloc au-dessus n’occulte pas
                        BlockPos above = pos.above();
                        if (level.isEmptyBlock(above)) {
                            putFaceTop(vc, m, x1, y2 + EPS_TOP, z1, x2, z2, packedLight, baseR, baseG, baseB, a);
                        }

                        // NORTH (vers -Z)
                        BlockPos nbN = pos.north();
                        if (!level.getBlockState(nbN).isFaceSturdy(level, nbN, Direction.SOUTH)) {
                            putFaceNorth(vc, m, x1, y1, z1 + EPS_SIDE, x2, y2, packedLight, baseR, baseG, baseB, a);
                        }

                        // SOUTH (vers +Z)
                        BlockPos nbS = pos.south();
                        if (!level.getBlockState(nbS).isFaceSturdy(level, nbS, Direction.NORTH)) {
                            putFaceSouth(vc, m, x1, y1, z2 - EPS_SIDE, x2, y2, packedLight, baseR, baseG, baseB, a);
                        }

                        // WEST (vers -X)
                        BlockPos nbW = pos.west();
                        if (!level.getBlockState(nbW).isFaceSturdy(level, nbW, Direction.EAST)) {
                            putFaceWest(vc, m, x1 + EPS_SIDE, y1, z1, y2, z2, packedLight, baseR, baseG, baseB, a);
                        }

                        // EAST (vers +X)
                        BlockPos nbE = pos.east();
                        if (!level.getBlockState(nbE).isFaceSturdy(level, nbE, Direction.WEST)) {
                            putFaceEast(vc, m, x2 - EPS_SIDE, y1, z1, y2, z2, packedLight, baseR, baseG, baseB, a);
                        }
                    }
                }
            }
        });

        buffers.endLastBatch();
        pose.popPose();
    }

    private static void v(VertexConsumer vc, org.joml.Matrix4f m, float x, float y, float z, float u, float v, int light, float nx, float ny, float nz, float r, float g, float b, float a) {
        vc.addVertex(m, x, y, z);
        vc.setColor(r, g, b, a);
        vc.setUv(u, v);
        vc.setOverlay(OverlayTexture.NO_OVERLAY);
        vc.setLight(light);
        vc.setNormal(nx, ny, nz);
    }

    private static void putFaceTop(VertexConsumer vc, org.joml.Matrix4f m, float x1, float y, float z1, float x2, float z2, int light, float r, float g, float b, float a) {
        float nx = 0, ny = 1, nz = 0;
        
        v(vc, m, x1, y, z1, 0, 0, light, nx, ny, nz, r, g, b, a);
        v(vc, m, x2, y, z1, 1, 0, light, nx, ny, nz, r, g, b, a);
        v(vc, m, x2, y, z2, 1, 1, light, nx, ny, nz, r, g, b, a);
        v(vc, m, x1, y, z2, 0, 1, light, nx, ny, nz, r, g, b, a);
    }

    private static void putFaceNorth(VertexConsumer vc, org.joml.Matrix4f m, float x1, float y1, float z, float x2, float y2, int light, float r, float g, float b, float a) {
        float nx = 0, ny = 0, nz = -1;
        
        v(vc, m, x1, y1, z, 0, 0, light, nx, ny, nz, r, g, b, a);
        v(vc, m, x2, y1, z, 1, 0, light, nx, ny, nz, r, g, b, a);
        v(vc, m, x2, y2, z, 1, 1, light, nx, ny, nz, r, g, b, a);
        v(vc, m, x1, y2, z, 0, 1, light, nx, ny, nz, r, g, b, a);
    }

    private static void putFaceSouth(VertexConsumer vc, org.joml.Matrix4f m, float x1, float y1, float z, float x2, float y2, int light, float r, float g, float b, float a) {
        float nx = 0, ny = 0, nz = 1;
        
        v(vc, m, x2, y1, z, 1, 0, light, nx, ny, nz, r, g, b, a);
        v(vc, m, x1, y1, z, 0, 0, light, nx, ny, nz, r, g, b, a);
        v(vc, m, x1, y2, z, 0, 1, light, nx, ny, nz, r, g, b, a);
        v(vc, m, x2, y2, z, 1, 1, light, nx, ny, nz, r, g, b, a);
    }

    private static void putFaceWest(VertexConsumer vc, org.joml.Matrix4f m, float x, float y1, float z1, float y2, float z2, int light, float r, float g, float b, float a) {
        float nx = -1, ny = 0, nz = 0;
        
        v(vc, m, x, y1, z2, 1, 0, light, nx, ny, nz, r, g, b, a);
        v(vc, m, x, y1, z1, 0, 0, light, nx, ny, nz, r, g, b, a);
        v(vc, m, x, y2, z1, 0, 1, light, nx, ny, nz, r, g, b, a);
        v(vc, m, x, y2, z2, 1, 1, light, nx, ny, nz, r, g, b, a);
    }

    private static void putFaceEast(VertexConsumer vc, org.joml.Matrix4f m, float x, float y1, float z1, float y2, float z2, int light, float r, float g, float b, float a) {
        float nx = 1, ny = 0, nz = 0;
        
        v(vc, m, x, y1, z1, 0, 0, light, nx, ny, nz, r, g, b, a);
        v(vc, m, x, y1, z2, 1, 0, light, nx, ny, nz, r, g, b, a);
        v(vc, m, x, y2, z2, 1, 1, light, nx, ny, nz, r, g, b, a);
        v(vc, m, x, y2, z1, 0, 1, light, nx, ny, nz, r, g, b, a);
    }
}
