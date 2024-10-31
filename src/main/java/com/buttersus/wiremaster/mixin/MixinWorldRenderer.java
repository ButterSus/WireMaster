package com.buttersus.wiremaster.mixin;

import com.buttersus.wiremaster.client.designer.Designer;
import com.buttersus.wiremaster.util.VectorMathUtils;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.render.*;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.OptionalDouble;

import static net.minecraft.client.render.RenderPhase.*;

@Mixin(WorldRenderer.class)
public abstract class MixinWorldRenderer {
    @Shadow
    protected abstract boolean isOutsideViewDistance(BlockPos pos, ChunkBuilder.BuiltChunk chunk);

    @Shadow
    @Nullable
    private BuiltChunkStorage chunks;

    @Shadow
    @Nullable
    private ClientWorld world;

    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;setupTerrain(Lnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/Frustum;ZZ)V"), index = 3)
    private boolean onCallSetupRender(boolean isSpectator) {
        if (Designer.cameraController.isActive()) {
            return true;
        } else {
            return isSpectator;
        }
    }

    @ModifyVariable(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;crosshairTarget:Lnet/minecraft/util/hit/HitResult;", ordinal = 1), ordinal = 0, argsOnly = true)
    private boolean changeRenderBlockOutline(boolean renderBlockOutline) {
        return Designer.cursorController.getHoverHitResult() == null && renderBlockOutline;
    }

    @Inject(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;crosshairTarget:Lnet/minecraft/util/hit/HitResult;", ordinal = 1))
    private void onRender(
            MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f positionMatrix, CallbackInfo ci,
            @Local VertexConsumerProvider.Immediate immediate
    ) {
        HitResult hitResult = Designer.cursorController.getHoverHitResult();
        if (this.chunks == null || hitResult == null || this.world == null) return;

        // Get block pos && camera pos
        if (hitResult.getType() != HitResult.Type.BLOCK) return;
        BlockHitResult blockHitResult = (BlockHitResult) hitResult;
        BlockPos blockPos = blockHitResult.getBlockPos();
        BlockState blockState = this.world.getBlockState(blockPos);
        Vec3d cameraPos = VectorMathUtils.toVec3d(Designer.cameraController.getPosition());

        // Check if block is withing render distance
        ChunkBuilder.BuiltChunk builtChunk = ((BuiltChunkStorageInvoker) chunks).invokeGetRenderedChunk(blockPos);
        if (builtChunk == null || isOutsideViewDistance(blockPos, builtChunk)) return;

        // Draw block outline
        VertexConsumer vertexConsumer = immediate.getBuffer(RenderLayer.of(
                "lines_no_depth_test",
                VertexFormats.LINES,
                VertexFormat.DrawMode.LINES,
                256,
                RenderLayer.MultiPhaseParameters.builder()
                        .program(LINES_PROGRAM)
                        .lineWidth(new RenderPhase.LineWidth(OptionalDouble.of(3.0f)))
                        .layering(VIEW_OFFSET_Z_LAYERING)
                        .transparency(TRANSLUCENT_TRANSPARENCY)
                        .target(ITEM_TARGET)
                        .writeMaskState(ALL_MASK)
                        .cull(DISABLE_CULLING)
                        .depthTest(ALWAYS_DEPTH_TEST)  // Always pass depth test
                        .build(false)
        ));
        VoxelShape voxelShape = blockState.getOutlineShape(this.world, blockPos, ShapeContext.of(camera.getFocusedEntity()));
        double offsetX = blockPos.getX() - cameraPos.x;
        double offsetY = blockPos.getY() - cameraPos.y;
        double offsetZ = blockPos.getZ() - cameraPos.z;
        float r = 0.0f;
        float g = 0.0f;
        float b = 0.0f;
        float a = 0.6f;
        MatrixStack.Entry entry = matrices.peek();
        voxelShape.forEachEdge(((minX, minY, minZ, maxX, maxY, maxZ) -> {
            float k = (float) (maxX - minX);
            float l = (float) (maxY - minY);
            float m = (float) (maxZ - minZ);
            float n = MathHelper.sqrt(k * k + l * l + m * m);
            k /= n;
            l /= n;
            m /= n;
            vertexConsumer.vertex(entry.getPositionMatrix(), (float) (minX + offsetX), (float) (minY + offsetY), (float) (minZ + offsetZ)).color(r, g, b, a).normal(entry.getNormalMatrix(), k, l, m).next();
            vertexConsumer.vertex(entry.getPositionMatrix(), (float) (maxX + offsetX), (float) (maxY + offsetY), (float) (maxZ + offsetZ)).color(r, g, b, a).normal(entry.getNormalMatrix(), k, l, m).next();
        }));
    }
}