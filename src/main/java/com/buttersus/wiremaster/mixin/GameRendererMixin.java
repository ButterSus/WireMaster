package com.buttersus.wiremaster.mixin;

import com.buttersus.wiremaster.client.camera.WireDesigner;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Inject(at = @At("HEAD"), method = "render(FJZ)V")
    private void onRender(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        WireDesigner.INSTANCE.onRenderTickStart(tickDelta);
    }

    @Inject(at = @At("HEAD"), method = "updateTargetedEntity(F)V")
    private void onBeforeUpdateTargetedEntity(float tickDelta, CallbackInfo info) {
        WireDesigner.INSTANCE.onBeforeGameRendererPick();
    }

    @Inject(at = @At("TAIL"), method = "updateTargetedEntity(F)V")
    private void onAfterUpdateTargetedEntity(float tickDelta, CallbackInfo info) {
        WireDesigner.INSTANCE.onAfterGameRendererPick();
    }

    @Redirect(method = "renderHand(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/Camera;F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/Perspective;isFirstPerson()Z", ordinal = 0))
    private boolean onRenderItemInHandIsFirstPerson(Perspective cameraType) {
        return WireDesigner.INSTANCE.onRenderItemInHandIsFirstPerson(cameraType);
    }
}
