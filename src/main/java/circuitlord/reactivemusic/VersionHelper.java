package circuitlord.reactivemusic;

//? if >=26 {
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;

public class VersionHelper {

    public static Entity GetRidingEntity(LocalPlayer player) {
        return player.getVehicle();
    }
}
//?} else {
/*import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;

public class VersionHelper {

    public static Entity GetRidingEntity(ClientPlayerEntity player) {
        return player.getVehicle();
    }
}
*///?}
