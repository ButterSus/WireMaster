package com.buttersus.wiremaster.mixin;

import com.buttersus.wiremaster.client.camera.WireDesigner;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class MixinAbstractClientPlayerEntity {
    @Inject(method = "getFovMultiplier", at = @At("HEAD"), cancellable = true)
    public void onGetFovMultiplier(CallbackInfoReturnable<Float> cir) {
        if (WireDesigner.INSTANCE.isActive()) {
            cir.setReturnValue(1.0f);
        }
    }
}
