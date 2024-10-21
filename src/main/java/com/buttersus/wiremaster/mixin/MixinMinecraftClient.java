package com.buttersus.wiremaster.mixin;

import com.buttersus.wiremaster.client.camera.WireDesigner;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.client.gui.screen.ProgressScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient {
    @Unique
    private Screen currentScreen = null;
    @Unique
    private int screenLevel = 0;

    @Inject(at = @At("HEAD"), method = "joinWorld(Lnet/minecraft/client/world/ClientWorld;)V")
    private void onBeforeJoinWorld(ClientWorld world, CallbackInfo ci) {
        if (world != null) WireDesigner.INSTANCE.onWorldUnload();
    }

    @Inject(at = @At("HEAD"), method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V")
    private void onBeforeDisconnect(Screen screen, CallbackInfo ci) {
        MinecraftClient mc = (MinecraftClient) (Object) this;
        if (mc.world != null) WireDesigner.INSTANCE.onWorldUnload();
    }

    @Inject(at = @At("HEAD"), method = "setScreen")
    private void onSetScreenChange(Screen screen, CallbackInfo ci) {
        MinecraftClient client = (MinecraftClient) (Object) this;
        Set<Class<? extends Screen>> screens = Set.of(
                ProgressScreen.class,
                DownloadingTerrainScreen.class
        );

        boolean isInGame = client.world != null
                && (screen == null || !screens.contains(screen.getClass()))
                && (client.currentScreen == null || !screens.contains(client.currentScreen.getClass()));

        if (currentScreen != null) {
//            onClose(currentScreen, isInGame, screenLevel);
            if (screenLevel == 1 && isInGame) WireDesigner.INSTANCE.onScreenClose();
            screenLevel = Math.max(0, screenLevel - 1);
        }

        if (screen != null) {
//            onOpen(screen, isInGame, screenLevel);
            if (screenLevel == 0 && isInGame) WireDesigner.INSTANCE.onScreenOpen();
            screenLevel++;
        } else {
            screenLevel = 0;
        }

        currentScreen = screen;
    }
}

