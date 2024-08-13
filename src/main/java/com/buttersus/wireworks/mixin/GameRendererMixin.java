package com.buttersus.wireworks.mixin;

import com.buttersus.wireworks.client.camera.OrthographicViewMode;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
abstract class GameRendererMixin {
    @Inject(method = "renderWorld", at = @At("HEAD"))
    private void renderWorld(float tickDelta, long limitTime, MatrixStack matrices, CallbackInfo ci) {
        if (OrthographicViewMode.INSTANCE.isActive()) {
            MinecraftClient client = MinecraftClient.getInstance();
            float width = (float) client.getWindow().getFramebufferWidth();
            float height = (float) client.getWindow().getFramebufferHeight();
            float aspect = width / height;
            float size = 20f;
            matrices.peek().getPositionMatrix().ortho(
                    -size * aspect, size * aspect,
                    -size, size,
                    0.05f, 1000f
            );
        }
    }
}
