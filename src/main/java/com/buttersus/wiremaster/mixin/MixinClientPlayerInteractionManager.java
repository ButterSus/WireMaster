package com.buttersus.wiremaster.mixin;

import com.buttersus.wiremaster.WireMaster;
import com.buttersus.wiremaster.client.camera.WireDesigner;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class MixinClientPlayerInteractionManager {
    @Inject(method = "getReachDistance", at = @At("HEAD"), cancellable = true)
    private void onGetReachDistance(CallbackInfoReturnable<Float> cir) {
        if (WireDesigner.INSTANCE.isCursorMode()) cir.setReturnValue((float) WireMaster.INSTANCE.getREACH_DISTANCE());
    }
}
