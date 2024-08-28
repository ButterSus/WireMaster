package com.buttersus.wiremaster.mixin;

import com.buttersus.wiremaster.client.camera.WireDesigner;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class MixinEntity {
    @Shadow(aliases = "Lnet/minecraft/entity/Entity;getRotationVector(FF)Lnet/minecraft/util/math/Vec3d;")
    protected abstract Vec3d getRotationVector(float pitch, float yaw);

    @Inject(method = "getCameraPosVec(F)Lnet/minecraft/util/math/Vec3d;", at = @At("HEAD"), cancellable = true)
    private void onGetCameraPosVec(float tickDelta, CallbackInfoReturnable<Vec3d> cir) {
        WireDesigner wireDesigner = WireDesigner.INSTANCE;
        if (wireDesigner.shouldOverrideCameraEntityPosition((Entity) (Object) this)) {
            cir.setReturnValue(wireDesigner.getVec3dPos());
        }
    }

    @Inject(method = "getRotationVec(F)Lnet/minecraft/util/math/Vec3d;", at = @At("HEAD"), cancellable = true)
    private void onGetRotationVec(float p_20253_, CallbackInfoReturnable<Vec3d> cir) {
        WireDesigner wireDesigner = WireDesigner.INSTANCE;
        if (wireDesigner.shouldOverrideCameraEntityPosition((Entity) (Object) this)) {
            if (wireDesigner.isCursorMode())
                cir.setReturnValue(wireDesigner.getCursorVector(wireDesigner.getMouseVector()));
            else
                cir.setReturnValue(this.getRotationVector((float) wireDesigner.getXRot(), (float) wireDesigner.getYRot()));
        }
    }
}
