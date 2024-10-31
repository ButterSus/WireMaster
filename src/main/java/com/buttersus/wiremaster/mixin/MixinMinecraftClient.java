package com.buttersus.wiremaster.mixin;

import com.buttersus.wiremaster.client.event.MinecraftClientEvents;
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
        MinecraftClientEvents.WORLD_LOAD.invoker().onWorldLoad(world);
    }

    @Inject(at = @At("HEAD"), method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V")
    private void onBeforeDisconnect(Screen screen, CallbackInfo ci) {
        MinecraftClientEvents.WORLD_UNLOAD.invoker().onWorldUnload(screen);
    }

    @Inject(at = @At("HEAD"), method = "setScreen")
    private void onSetScreenChange(Screen screen, CallbackInfo ci) {
        MinecraftClient client = (MinecraftClient) (Object) this;
        Set<Class<? extends Screen>> forbiddenScreens = Set.of(
                ProgressScreen.class,
                DownloadingTerrainScreen.class
        );

        boolean isInGame = client.world != null
                && (screen == null || !forbiddenScreens.contains(screen.getClass()))
                && (client.currentScreen == null || !forbiddenScreens.contains(client.currentScreen.getClass()));

        if (currentScreen != null) {
            MinecraftClientEvents.SCREEN_CLOSE.invoker().onScreenClose(currentScreen, isInGame, screenLevel);
            screenLevel = Math.max(0, screenLevel - 1);
        }

        if (screen != null) {
            MinecraftClientEvents.SCREEN_OPEN.invoker().onScreenOpen(screen, isInGame, screenLevel);
            screenLevel++;
        } else {
            screenLevel = 0;
        }

        currentScreen = screen;
    }
}

