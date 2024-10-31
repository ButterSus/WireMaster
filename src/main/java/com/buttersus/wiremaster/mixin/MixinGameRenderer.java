package com.buttersus.wiremaster.mixin;

import com.buttersus.wiremaster.client.designer.Designer;
import com.buttersus.wiremaster.client.event.GameRendererTickEvents;
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
    @Inject(at = @At("HEAD"), method = "render")
    private void onRenderStart(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        GameRendererTickEvents.START_RENDER_TICK.invoker().onStartRenderTick(tickDelta, startTime, tick);
        if (GameRendererTickEvents.isValidFrameTime()) {
            double frameTime = GameRendererTickEvents.getFrameTime();
            GameRendererTickEvents.START_RENDER_WITH_FRAME_TIME_TICK.invoker().onStartRenderWithFrameTimeTick(tickDelta, startTime, tick, frameTime);
        }
        GameRendererTickEvents.updateFrameTime();
    }

    @Inject(at = @At("TAIL"), method = "render")
    private void onRenderEnd(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        GameRendererTickEvents.END_RENDER_TICK.invoker().onEndRenderTick(tickDelta, startTime, tick);
    }

    @Inject(at = @At("HEAD"), method = "updateTargetedEntity(F)V")
    private void onBeforeUpdateTargetedEntity(float tickDelta, CallbackInfo info) {
        Designer.cameraController.setGameRendererPicking(true);
    }

    @Inject(at = @At("TAIL"), method = "updateTargetedEntity(F)V")
    private void onAfterUpdateTargetedEntity(float tickDelta, CallbackInfo info) {
        Designer.cameraController.setGameRendererPicking(false);
    }

    @Redirect(method = "renderHand(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/Camera;F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/Perspective;isFirstPerson()Z", ordinal = 0))
    private boolean onRenderItemInHandIsFirstPerson(Perspective cameraType) {
        return Designer.rendererController.onRenderItemInHandShouldOverrideIsFirstPerson(cameraType);
    }

    @Unique
    private static PlayerAbilities playerAbilitiesBuffer;
    @Unique
    private static boolean oldAllowModifyWorld;

    @Inject(method = "shouldRenderBlockOutline", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getAbilities()Lnet/minecraft/entity/player/PlayerAbilities;"))
    private void beforeShouldRenderBlockOutlineCondition(CallbackInfoReturnable<Boolean> cir, @Local Entity entityLocalRef) {
        if (Designer.rendererController.shouldOverrideRenderBlockOutline()) {
            playerAbilitiesBuffer = ((PlayerEntity) entityLocalRef).getAbilities();
            oldAllowModifyWorld = playerAbilitiesBuffer.allowModifyWorld;
            playerAbilitiesBuffer.allowModifyWorld = false;
        }
    }

    @Inject(method = "shouldRenderBlockOutline", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getAbilities()Lnet/minecraft/entity/player/PlayerAbilities;", shift = At.Shift.AFTER))
    private void afterShouldRenderBlockOutlineCondition(CallbackInfoReturnable<Boolean> cir) {
        if (Designer.rendererController.shouldOverrideRenderBlockOutline()) {
            playerAbilitiesBuffer.allowModifyWorld = oldAllowModifyWorld;
        }
    }

    @Inject(method = "shouldRenderBlockOutline", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/hit/HitResult;getType()Lnet/minecraft/util/hit/HitResult$Type;"))
    private void changeHitResult(CallbackInfoReturnable<Boolean> cir, @Local LocalRef<HitResult> hitResultLocalRef) {
        HitResult hitResult = Designer.cursorController.getHoverHitResult();
        if (hitResult != null) hitResultLocalRef.set(hitResult);
    }

    @Redirect(method = "updateTargetedEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;raycast(DFZ)Lnet/minecraft/util/hit/HitResult;"))
    private HitResult onRaycast(Entity instance, double maxDistance, float tickDelta, boolean includeFluids) {
        HitResult hitResult = Designer.cursorController.getHoverHitResult();
        if (hitResult != null) return hitResult;
        else return instance.raycast(maxDistance, tickDelta, includeFluids);
    }
}
