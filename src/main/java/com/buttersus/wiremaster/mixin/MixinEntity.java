package com.buttersus.wiremaster.mixin;

import com.buttersus.wiremaster.client.camera.WireDesigner;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
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
            if (wireDesigner.getCursorMode())
                cir.setReturnValue(getCursorRotationVec());
            else
                cir.setReturnValue(this.getRotationVector((float) wireDesigner.getXRot(), (float) wireDesigner.getYRot()));
        }
    }

    @Unique
    private static Vec3d getCursorRotationVec() {
        WireDesigner wireDesigner = WireDesigner.INSTANCE;
        MinecraftClient mc = MinecraftClient.getInstance();

        // Get camera attributes
        double pitch = Math.toRadians(wireDesigner.getXRot());
        double yaw = Math.toRadians(wireDesigner.getYRot());

        // Aspect ratio
        Window window = mc.getWindow();
        double aspectRatio = (double) window.getWidth() / window.getHeight();

        // Get mouse position
        double normalizedMouseY = 1.0 - mc.mouse.getY() / window.getHeight() * 2.0;
        double normalizedMouseX = 1.0 - mc.mouse.getX() / window.getWidth() * 2.0;

        // Get view vector
        double fov = Math.toRadians(((GameRendererInvoker) mc.gameRenderer).invokeGetFov(mc.gameRenderer.getCamera(), mc.getTickDelta(), false)) * Math.sqrt(2.0);
        double tanHalfFov = Math.tan(fov / 2);
        Vec3d viewVector = new Vec3d(normalizedMouseX * aspectRatio * tanHalfFov, normalizedMouseY * tanHalfFov, 1).normalize();

        // Return rotation vector
        return viewVector.rotateX((float) -pitch).rotateY((float) -yaw);
    }
}
