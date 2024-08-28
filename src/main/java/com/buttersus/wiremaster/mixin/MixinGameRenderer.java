package com.buttersus.wiremaster.mixin;

import com.buttersus.wiremaster.client.camera.WireDesigner;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer {
    @Inject(at = @At("HEAD"), method = "render(FJZ)V")
    private void onRender(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        WireDesigner.INSTANCE.onRenderTickStart();
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

    @Unique
    private static PlayerAbilities playerAbilitiesBuffer;
    @Unique
    private static boolean oldAllowModifyWorld;

    @Inject(method = "shouldRenderBlockOutline", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getAbilities()Lnet/minecraft/entity/player/PlayerAbilities;"))
    private void beforeShouldRenderBlockOutlineCondition(CallbackInfoReturnable<Boolean> cir, @Local Entity entityLocalRef) {
        if (WireDesigner.INSTANCE.isCursorMode()) {
            playerAbilitiesBuffer = ((PlayerEntity) entityLocalRef).getAbilities();
            oldAllowModifyWorld = playerAbilitiesBuffer.allowModifyWorld;
            playerAbilitiesBuffer.allowModifyWorld = false;
        }
    }

    @Inject(method = "shouldRenderBlockOutline", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getAbilities()Lnet/minecraft/entity/player/PlayerAbilities;", shift = At.Shift.AFTER))
    private void afterShouldRenderBlockOutlineCondition(CallbackInfoReturnable<Boolean> cir) {
        if (WireDesigner.INSTANCE.isCursorMode()) {
            playerAbilitiesBuffer.allowModifyWorld = oldAllowModifyWorld;
        }
    }

    @Inject(method = "shouldRenderBlockOutline", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/hit/HitResult;getType()Lnet/minecraft/util/hit/HitResult$Type;"))
    private void changeHitResult(CallbackInfoReturnable<Boolean> cir, @Local LocalRef<HitResult> hitResultLocalRef) {
        HitResult hitResult = WireDesigner.INSTANCE.getMouseTargetedHitResult();
        if (hitResult != null) hitResultLocalRef.set(hitResult);
    }

    @Redirect(method = "updateTargetedEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;raycast(DFZ)Lnet/minecraft/util/hit/HitResult;"))
    private HitResult onRaycast(Entity instance, double maxDistance, float tickDelta, boolean includeFluids) {
        HitResult hitResult = WireDesigner.INSTANCE.getMouseTargetedHitResult();
        if (hitResult != null) return hitResult;
        else return instance.raycast(maxDistance, tickDelta, includeFluids);
    }
}
