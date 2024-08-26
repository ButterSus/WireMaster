package com.buttersus.wiremaster.mixin;

import com.buttersus.wiremaster.client.camera.WireDesigner;
import com.buttersus.wiremaster.extensions.VectorExtensionsKt;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
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
                cir.setReturnValue(getCustomRotationVector());
            else
                cir.setReturnValue(this.getRotationVector((float) wireDesigner.getXRot(), (float) wireDesigner.getYRot()));
        }
    }

    @Unique
    private static Vec3d getCustomRotationVector() {
        MinecraftClient mc = MinecraftClient.getInstance();

        // Compute NDC vector from plain space
        Window window = mc.getWindow();
        Vector3f vector = new Vector3f(
                (float) (mc.mouse.getX() / window.getWidth() * 2.0 - 1.0),
                (float) (mc.mouse.getY() / window.getHeight() * 2.0 - 1.0),
                1.0f
        );

        assert mc.player != null;
        new Matrix4f()
                .set(mc.gameRenderer.getBasicProjectionMatrix(mc.player.getFovMultiplier() * mc.options.getFov().getValue())).invert()  // Compute view vector from NDC space
                .transformPosition(vector);
        vector.normalize();
        new Matrix4f()
                .rotate(mc.gameRenderer.getCamera().getRotation())  // Rotate vector
                .transformPosition(vector);
        vector.negate();

        // Return direction vector
        return VectorExtensionsKt.toVec3d(vector);
    }
}
