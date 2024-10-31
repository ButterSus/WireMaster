package com.buttersus.wiremaster.mixin;

import com.buttersus.wiremaster.client.event.MouseEvents;
import com.buttersus.wiremaster.util.MixinPriority;
import net.minecraft.client.Mouse;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = Mouse.class, priority = MixinPriority.AFTER_AMECS_API)
public abstract class MixinMouse {
    @Redirect(method = "updateMouse()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;changeLookDirection(DD)V"))
    private void onPlayerTurn(ClientPlayerEntity player, double yRot, double xRot) {
        MouseEvents.PLAYER_TURN.invoker().onPlayerTurn(player, yRot, xRot);
    }
}
