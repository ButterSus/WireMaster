package com.buttersus.wiremaster.mixin;

import com.buttersus.wiremaster.client.camera.WireDesigner;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow(aliases = "Lnet/minecraft/client/render/Camera;setRotation(FF)V")
    protected abstract void setRotation(float yaw, float pitch);

    @Shadow(aliases = "Lnet/minecraft/client/render/Camera;setPos(DDD)V")
    protected abstract void setPos(double p_90585_, double p_90586_, double p_90587_);

    @Inject(at = @At("TAIL"), method = "update(Lnet/minecraft/world/BlockView;Lnet/minecraft/entity/Entity;ZZF)V")
    private void onUpdate(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo info) {
        WireDesigner wireDesigner = WireDesigner.INSTANCE;
        if (wireDesigner.isActive()) {
            setRotation(wireDesigner.getYRot(), wireDesigner.getXRot());
            setPos(wireDesigner.getX(), wireDesigner.getY(), wireDesigner.getZ());
        }
    }
}
