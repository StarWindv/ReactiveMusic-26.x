package circuitlord.reactivemusic.mixin;

//import circuitlord.reactivemusic.SongLoader;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
//? if >=26 {
import net.minecraft.client.sounds.MusicManager;
//?} else {
/*import net.minecraft.client.sound.MusicTracker;
*///?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
//? if >=26 {
@Mixin(MusicManager.class)
//?} else {
/*@Mixin(MusicTracker.class)
*///?}
public class MusicTrackerMixin {

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void reactivemusic$tick(CallbackInfo ci) {

        // Disable minecraft music if we're doing our stuff
        //if (SongLoader.enabled) ci.cancel();
        // TODO: new disabler
        ci.cancel();

    }

}
