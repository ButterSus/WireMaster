package com.buttersus.wiremaster.mixin;

import com.buttersus.wiremaster.client.designer.Designer;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameOptions.class)
public abstract class MixinGameOptions {
    @Inject(at = @At("HEAD"), method = "getBobView()Lnet/minecraft/client/option/SimpleOption;", cancellable = true)
    private void onBobView(CallbackInfoReturnable<SimpleOption<Boolean>> cir) {
        // Disable the view bobbing effect when wire designer is active
        if (Designer.cameraController.isActive()) {
            cir.setReturnValue(SimpleOption.ofBoolean("", false));
        }
    }
}
