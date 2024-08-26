package com.buttersus.wiremaster.mixin;

import com.buttersus.wiremaster.client.camera.WireDesigner;
import net.minecraft.client.Mouse;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = Mouse.class, priority = 1100)
public abstract class MixinMouse {
    @Redirect(
            method = "updateMouse()V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;changeLookDirection(DD)V"))
    private void onLocalPlayerTurn(ClientPlayerEntity player, double yRot, double xRot) {
        WireDesigner.INSTANCE.onPlayerTurn(player, yRot, xRot);
    }
}
