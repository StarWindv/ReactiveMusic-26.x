package circuitlord.reactivemusic.neoforge;

import circuitlord.reactivemusic.platform.PlatformHelper;
import net.neoforged.fml.ModList;
//? if >=26 {
import net.neoforged.fml.loading.FMLEnvironment;
//?} else {
/*import net.neoforged.fml.loading.FMLLoader;
*///?}
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;

public class NeoForgePlatformHelper implements PlatformHelper {
    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public Path getGameDir() {
        return FMLPaths.GAMEDIR.get();
    }

    @Override
    public Path getConfigDir() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public boolean isDedicatedServer() {
        //? if >=26 {
        return FMLEnvironment.getDist().isDedicatedServer();
        //?} else {
        /*return FMLLoader.getDist().isDedicatedServer();
        *///?}
    }
}
