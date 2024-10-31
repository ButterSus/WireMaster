package com.buttersus.wiremaster.mixin;

import com.buttersus.wiremaster.client.keybinding.KeyBindingController;
import com.buttersus.wiremaster.util.MixinPriority;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Mixin applied after Amecs API
@Mixin(value = Keyboard.class, priority = MixinPriority.AFTER_AMECS_API)
public abstract class MixinAmecsExtensionKeyboard {
    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "onKey", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;", ordinal = 0), cancellable = true)
    private void onKeyPriority(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        // Drop key override
        if (client.options.dropKey.matchesKey(key, scancode) && action == 1 && KeyBindingController.TOGGLE_CURSOR_MODE.isUnbound()) {
            if (KeyBindingController.TOGGLE_CURSOR_MODE.onPressedPriority()) ci.cancel();
        }

        // Sprint key listener
        if (client.options.sprintKey.matchesKey(key, scancode) && action == 1 && KeyBindingController.SPRINT.isUnbound()) {
            KeyBindingController.SPRINT.onPressed();
        } else if (client.options.sprintKey.matchesKey(key, scancode) && action == 0 && KeyBindingController.SPRINT.isUnbound()) {
            KeyBindingController.SPRINT.onReleased();
        }
    }
}
