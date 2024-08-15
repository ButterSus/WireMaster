package com.buttersus.wiremaster.mixin;

import com.buttersus.wiremaster.client.camera.WireDesigner;
import com.buttersus.wiremaster.client.keybinding.Keybindings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Inject(at = @At("HEAD"), method = "joinWorld(Lnet/minecraft/client/world/ClientWorld;)V")
    private void onBeforeJoinWorld(ClientWorld world, CallbackInfo ci) {
        if (world != null) WireDesigner.INSTANCE.onWorldUnload();
    }

    @Inject(at = @At("HEAD"), method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V")
    private void onBeforeDisconnect(Screen screen, CallbackInfo ci) {
        MinecraftClient mc = (MinecraftClient) (Object) this;
        if (mc.world != null) WireDesigner.INSTANCE.onWorldUnload();
    }

    @Inject(at = @At("TAIL"), method = "handleInputEvents()V")
    private void onHandleInputEvents(CallbackInfo ci) {
        Keybindings.INSTANCE.onHandleKeybindings();
    }
}

