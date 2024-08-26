package com.buttersus.wiremaster.mixin;

import com.buttersus.wiremaster.client.input.KeyBindings;
import com.buttersus.wiremaster.client.input.MovementControlKeyBinding;
import com.buttersus.wiremaster.client.input.ScrollKeyBinding;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

// Mixin applied after Amecs API
@Mixin(value = Mouse.class, priority = 1100)
public abstract class MixinAmecsExtensionMouse {
    @Inject(method = "onMouseButton", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;", ordinal = 0), cancellable = true)
    private void onMouseButtonPriority(long window, int button, int action, int mods, CallbackInfo ci) {
        MinecraftClient mc = MinecraftClient.getInstance();
        KeyBindings keyBindings = KeyBindings.INSTANCE;
        MovementControlKeyBinding movementControlKeyBinding = keyBindings.getMOVEMENT_CONTROL();

        // Pick block override
        if (mc.options.pickItemKey.matchesMouse(button) && action == 1 && movementControlKeyBinding.isUnbound()) {
            if (movementControlKeyBinding.onPressedPriority()) ci.cancel();
        } else if (mc.options.pickItemKey.matchesMouse(button) && action == 0 && movementControlKeyBinding.isUnbound()) {
            if (movementControlKeyBinding.onReleasedPriority()) ci.cancel();
        }
    }

    @Inject(method = "onMouseScroll", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void onMouseScroll(long window, double horizontal, double vertical, CallbackInfo ci, double deltaY) {
        KeyBindings keyBindings = KeyBindings.INSTANCE;
        ScrollKeyBinding scrollUpKeyBinding = keyBindings.getSCROLL_UP();
        ScrollKeyBinding scrollDownKeyBinding = keyBindings.getSCROLL_DOWN();

        // Scroll override
        if (deltaY > 0 && scrollUpKeyBinding.isUnbound()) {
            if (scrollUpKeyBinding.onPressedPriority()) ci.cancel();
        } else if (deltaY < 0 && scrollDownKeyBinding.isUnbound()) {
            if (scrollDownKeyBinding.onPressedPriority()) ci.cancel();
        }
    }
}
