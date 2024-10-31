package com.buttersus.wiremaster.mixin;

import com.buttersus.wiremaster.client.keybinding.KeyBindingController;
import com.buttersus.wiremaster.util.MixinPriority;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Mixin applied after Amecs API
@Mixin(value = Mouse.class, priority = MixinPriority.AFTER_AMECS_API)
public abstract class MixinAmecsExtensionMouse {
    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "onMouseButton", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;", ordinal = 0), cancellable = true)
    private void onMouseButtonPriority(long window, int button, int action, int mods, CallbackInfo ci) {
        // Use key override
        if (client.options.useKey.matchesMouse(button) && action == 1 && KeyBindingController.CURSOR_MOVEMENT.isUnbound()) {
            if (KeyBindingController.CURSOR_MOVEMENT.onPressedPriority()) ci.cancel();
        } else if (client.options.useKey.matchesMouse(button) && action == 0 && KeyBindingController.CURSOR_MOVEMENT.isUnbound()) {
            if (KeyBindingController.CURSOR_MOVEMENT.onReleasedPriority()) ci.cancel();
        }
    }

    @Inject(method = "onMouseScroll", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;", ordinal = 0), cancellable = true)
    private void onMouseScroll(long window, double horizontal, double vertical, CallbackInfo ci, @Local(ordinal = 2) double deltaY) {
        // Scroll override
        if (deltaY > 0 && KeyBindingController.SCROLL_UP.isUnbound()) {
            if (KeyBindingController.SCROLL_UP.onPressedPriority()) ci.cancel();
        } else if (deltaY < 0 && KeyBindingController.SCROLL_DOWN.isUnbound()) {
            if (KeyBindingController.SCROLL_DOWN.onPressedPriority()) ci.cancel();
        }
    }
}
