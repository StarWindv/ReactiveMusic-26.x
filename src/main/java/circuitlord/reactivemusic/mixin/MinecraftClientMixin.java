package circuitlord.reactivemusic.mixin;


import circuitlord.reactivemusic.ReactiveMusic;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
//? if >=26 {
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.multiplayer.ClientLevel;
//?} else {
/*import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
*///?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
//? if >=26 {
@Mixin(Minecraft.class)
//?} else {
/*@Mixin(MinecraftClient.class)
*///?}
public class MinecraftClientMixin {

    //? if >=26 {
    @Shadow
    public LocalPlayer player;

    @Shadow
    public ClientLevel level;
    //?} else {
    /*@Shadow
    public ClientPlayerEntity player;

    @Shadow
    public ClientWorld world;
    *///?}

    @Inject(method = "tick", at = @At("RETURN"))
    private void reactivemusic$tick(CallbackInfo ci) {

        ReactiveMusic.newTick();

    }



}
