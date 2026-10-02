package circuitlord.reactivemusic.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
//? if >=26 {
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.components.LerpingBossEvent;
//?} else {
/*import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.gui.hud.ClientBossBar;
*///?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import java.util.UUID;

@Environment(EnvType.CLIENT)
//? if >=26 {
@Mixin(BossHealthOverlay.class)
//?} else {
/*@Mixin(BossBarHud.class)
*///?}
public interface BossBarHudAccessor {

    //? if >=26 {
    @Accessor("events")
    Map<UUID, LerpingBossEvent> getBossBars();
    //?} else {
    /*@Accessor
    Map<UUID, ClientBossBar> getBossBars();
    *///?}

}
