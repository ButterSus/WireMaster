package com.buttersus.wiremaster.mixin;

import com.buttersus.wiremaster.client.input.KeyBindings;
import com.buttersus.wiremaster.client.input.SprintKeyBinding;
import com.buttersus.wiremaster.client.input.ToggleCursorModeKeyBinding;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Mixin applied after Amecs API
@Mixin(value = Keyboard.class, priority = 1100)
public abstract class MixinAmecsExtensionKeyboard {
    @Inject(method = "onKey", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;", ordinal = 0, shift = At.Shift.BEFORE), cancellable = true)
    private void onKeyPriority(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        MinecraftClient mc = MinecraftClient.getInstance();
        KeyBindings keyBindings = KeyBindings.INSTANCE;
        ToggleCursorModeKeyBinding toggleCursorModeKeyBinding = keyBindings.getTOGGLE_CURSOR_MODE();
        SprintKeyBinding sprintKeyBinding = keyBindings.getSPRINT();

        // Drop key override
        if (mc.options.dropKey.matchesKey(key, scancode) && action == 1 && toggleCursorModeKeyBinding.isUnbound()) {
            if (toggleCursorModeKeyBinding.onPressedPriority()) ci.cancel();
        }

        // Sprint key listener
        if (mc.options.sprintKey.matchesKey(key, scancode) && action == 1 && sprintKeyBinding.isUnbound()) {
            sprintKeyBinding.onPressed();
        } else if (mc.options.sprintKey.matchesKey(key, scancode) && action == 0 && sprintKeyBinding.isUnbound()) {
            sprintKeyBinding.onReleased();
        }
    }
}
