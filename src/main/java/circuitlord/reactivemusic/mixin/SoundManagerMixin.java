package circuitlord.reactivemusic.mixin;

import circuitlord.reactivemusic.ReactiveMusic;
//? if >=26 {
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//?} else if >=1.21.9 {
/*import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
*///?} else {
/*import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
*///?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(SoundManager.class)
public class SoundManagerMixin {

    //? if >=26 {
    @Inject(method = "play(Lnet/minecraft/client/resources/sounds/SoundInstance;)Lnet/minecraft/client/sounds/SoundEngine$PlayResult;", at = @At("HEAD"), cancellable = true)
    private void play(SoundInstance soundInstance, CallbackInfoReturnable<SoundEngine.PlayResult> cir) {
    //?} else if >=1.21.9 {
    /*@Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;)Lnet/minecraft/client/sound/SoundSystem$PlayResult;", at = @At("HEAD"), cancellable = true)
    private void play(SoundInstance soundInstance, CallbackInfoReturnable<SoundSystem.PlayResult> cir) {
    *///?} else {
    /*@Inject(method = "Lnet/minecraft/client/sound/SoundManager;play(Lnet/minecraft/client/sound/SoundInstance;)V", at = @At("HEAD"), cancellable = true)
    private void play(SoundInstance soundInstance, CallbackInfo ci) {
    *///?}

        //? if >=26 {
        String path = soundInstance.getIdentifier().getPath();

        Minecraft mc = Minecraft.getInstance();

        if (mc.player != null && ReactiveMusic.printSoundEvents) {
            mc.player.sendSystemMessage(Component.literal("[ReactiveMusic]: Sound: " + path + " Attenuation: " + soundInstance.getAttenuation()));
        }
        //?} else {
        /*String path = soundInstance.getId().getPath();

        MinecraftClient mc = MinecraftClient.getInstance();

        if (mc.player != null && ReactiveMusic.printSoundEvents) {
            mc.player.sendMessage(Text.of("[ReactiveMusic]: Sound: " + path + " Attenuation: " + soundInstance.getAttenuationType()), false);
        }
        *///?}

        if (path.contains("music_disc")) {
            ReactiveMusic.trackSoundMuteMusic(soundInstance, false);
        }

        // cobblemon resource pack uses:
        //"battle.pvn.default"
        //"battle.pvp.default"
        //"battle.pvw.default"
        else if (path.contains("battle.pv")) {
            ReactiveMusic.trackSoundMuteMusic(soundInstance, false);

            ReactiveMusic.LOGGER.info("Detected cobblemon battle event, adding to list!");
        }

        for (String muteSound : ReactiveMusic.config.soundsMuteMusic) {
            if (path.contains(muteSound)) {
                ReactiveMusic.trackSoundMuteMusic(soundInstance, false);
                break;
            }
        }

        for (String muteSound : ReactiveMusic.config.soundsMuteMusicIgnoreDistance) {
            if (path.contains(muteSound)) {
                ReactiveMusic.trackSoundMuteMusic(soundInstance, true);
                break;
            }
        }
    }
}
