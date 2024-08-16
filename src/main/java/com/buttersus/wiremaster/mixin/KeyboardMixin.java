package com.buttersus.wiremaster.mixin;

import com.buttersus.wiremaster.client.input.KeyBindings;
import de.siphalor.amecs.api.AmecsKeyBinding;
import de.siphalor.amecs.api.PriorityKeyBinding;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public abstract class KeyboardMixin {
    @Inject(method = "onKey", at= @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;", ordinal = 0, shift = At.Shift.BEFORE), cancellable = true)
    private void onKeyPressed(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        MinecraftClient mc = MinecraftClient.getInstance();
        AmecsKeyBinding keyBinding = KeyBindings.INSTANCE.getTOGGLE_CURSOR_MODE();

        // Drop key override
        if (mc.options.dropKey.matchesKey(key, scancode) && action == 1 && isKeyUnbound(keyBinding)) {
            if (((PriorityKeyBinding)keyBinding).onPressedPriority()) ci.cancel();
        }
    }

    @Unique
    private boolean isKeyUnbound(AmecsKeyBinding keyBinding) {
        return keyBinding.isUnbound();
    }
}
