package com.buttersus.wiremaster.mixin;

import com.buttersus.wiremaster.client.designer.Designer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class MixinInGameHud {
    @Redirect(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/Perspective;isFirstPerson()Z"))
    private boolean onRenderCrosshairIsFirstPerson(Perspective cameraType) {
        return Designer.rendererController.onRenderCrosshairShouldOverrideIsFirstPerson(cameraType);
    }

    @Inject(method = "renderCrosshair", at = @At(value = "HEAD"), cancellable = true)
    private void onRenderCrosshair(MatrixStack matrices, CallbackInfo ci) {
        if (!Designer.rendererController.shouldHideCrosshair()) ci.cancel();
    }
}

