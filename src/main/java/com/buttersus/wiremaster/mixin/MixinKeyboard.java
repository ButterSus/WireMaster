package com.buttersus.wiremaster.mixin;

import com.buttersus.wiremaster.client.input.KeyBindings;
import com.buttersus.wiremaster.client.input.SprintKeyBinding;
import com.buttersus.wiremaster.client.input.ToggleCursorModeKeyBinding;
import de.siphalor.amecs.api.AmecsKeyBinding;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public abstract class MixinKeyboard {
    @Inject(method = "onKey", at= @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;", ordinal = 0, shift = At.Shift.BEFORE), cancellable = true)
    private void onKeyPressed(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        MinecraftClient mc = MinecraftClient.getInstance();
        ToggleCursorModeKeyBinding toggleCursorModeKeyBinding = KeyBindings.INSTANCE.getTOGGLE_CURSOR_MODE();
        SprintKeyBinding sprintKeyBinding = KeyBindings.INSTANCE.getSPRINT();

        // Drop key override
        if (mc.options.dropKey.matchesKey(key, scancode) && action == 1 && isKeyUnbound(toggleCursorModeKeyBinding)) {
            if (toggleCursorModeKeyBinding.onPressedPriority()) ci.cancel();
        }

        // Sprint key override
        if (mc.options.sprintKey.matchesKey(key, scancode) && action == 1 && isKeyUnbound(sprintKeyBinding)) {
            sprintKeyBinding.onPressed();
        } else if (mc.options.sprintKey.matchesKey(key, scancode) && action == 0 && isKeyUnbound(sprintKeyBinding)) {
            sprintKeyBinding.onReleased();
        }
    }

    @Unique
    private boolean isKeyUnbound(AmecsKeyBinding keyBinding) {
        return keyBinding.isUnbound();
    }
}
