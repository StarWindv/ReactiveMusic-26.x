package circuitlord.reactivemusic.forge;

import circuitlord.reactivemusic.ReactiveMusic;
import circuitlord.reactivemusic.SongPicker;
import circuitlord.reactivemusic.config.ModConfig;
import com.mojang.brigadier.CommandDispatcher;
//? if >=26 {
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
//?} else {
/*import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
*///?}
//? if >=26 {
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
//?} else {
/*import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
*///?}

//? if >=26 {
import static net.minecraft.commands.Commands.literal;
//?} else {
/*import static net.minecraft.server.command.CommandManager.literal;
*///?}

@Mod("reactivemusic")
public class ReactiveMusicForge {

    public ReactiveMusicForge() {
        ReactiveMusic.init();
        //? if >=26 {
        FMLClientSetupEvent.getBus(FMLJavaModLoadingContext.get().getModBusGroup())
                .addListener(this::onClientSetup);
        MinecraftForge.EVENT_BUS.register(this);

        MinecraftForge.registerConfigScreen(
                (mc, parent) -> ModConfig.createScreen(parent)
        );
        //?} else {
        /*FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
        MinecraftForge.EVENT_BUS.register(this);

        ModLoadingContext.get().registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory(
                        (mc, parent) -> ModConfig.createScreen(parent)
                )
        );
        *///?}
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(ReactiveMusic::initClient);
    }

    @SubscribeEvent
    public void onRegisterClientCommands(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(literal("reactivemusic")
                .executes(context -> {
                    //? if >=26 {
                    Minecraft mc = Minecraft.getInstance();
                    Screen screen = ModConfig.createScreen(mc.gui.screen());
                    mc.execute(() -> mc.gui.setScreen(screen));
                    //?} else {
                    /*MinecraftClient mc = MinecraftClient.getInstance();
                    Screen screen = ModConfig.createScreen(mc.currentScreen);
                    mc.send(() -> mc.setScreen(screen));
                    *///?}
                    return 1;
                })

                .then(literal("logBlockCounter")
                        .executes(context -> {
                            SongPicker.queuedToPrintBlockCounter = true;
                            return 1;
                        })
                )

                .then(literal("toggleSoundEventLogging")
                        .executes(context -> {
                            ReactiveMusic.printSoundEvents = !ReactiveMusic.printSoundEvents;
                            return 1;
                        })
                )

                .then(literal("blacklistDimension")
                        .executes(context -> {
                            //? if >=26 {
                            Minecraft mc = Minecraft.getInstance();
                            String key = mc.level.dimension().identifier().toString();

                            if (ReactiveMusic.config.blacklistedDimensions.contains(key)) {
                                mc.player.sendSystemMessage(Component.literal("[ReactiveMusic]: " + key + " was already in blacklist."));
                                return 1;
                            }

                            mc.player.sendSystemMessage(Component.literal("[ReactiveMusic]: Added " + key + " to blacklist."));
                            //?} else {
                            /*MinecraftClient mc = MinecraftClient.getInstance();
                            String key = mc.world.getRegistryKey().getValue().toString();

                            if (ReactiveMusic.config.blacklistedDimensions.contains(key)) {
                                mc.player.sendMessage(Text.literal("[ReactiveMusic]: " + key + " was already in blacklist."), false);
                                return 1;
                            }

                            mc.player.sendMessage(Text.literal("[ReactiveMusic]: Added " + key + " to blacklist."), false);
                            *///?}
                            ReactiveMusic.config.blacklistedDimensions.add(key);
                            ModConfig.saveConfig();
                            return 1;
                        })
                )

                .then(literal("unblacklistDimension")
                        .executes(context -> {
                            //? if >=26 {
                            Minecraft mc = Minecraft.getInstance();
                            String key = mc.level.dimension().identifier().toString();

                            if (!ReactiveMusic.config.blacklistedDimensions.contains(key)) {
                                mc.player.sendSystemMessage(Component.literal("[ReactiveMusic]: " + key + " was not in blacklist."));
                                return 1;
                            }

                            mc.player.sendSystemMessage(Component.literal("[ReactiveMusic]: Removed " + key + " from blacklist."));
                            //?} else {
                            /*MinecraftClient mc = MinecraftClient.getInstance();
                            String key = mc.world.getRegistryKey().getValue().toString();

                            if (!ReactiveMusic.config.blacklistedDimensions.contains(key)) {
                                mc.player.sendMessage(Text.literal("[ReactiveMusic]: " + key + " was not in blacklist."), false);
                                return 1;
                            }

                            mc.player.sendMessage(Text.literal("[ReactiveMusic]: Removed " + key + " from blacklist."), false);
                            *///?}
                            ReactiveMusic.config.blacklistedDimensions.remove(key);
                            ModConfig.saveConfig();
                            return 1;
                        })
                )

                .then(literal("toggleLogging")
                        .executes(context -> {
                            ReactiveMusic.chatLoggingEnabled = !ReactiveMusic.chatLoggingEnabled;
                            //? if >=26 {
                            Minecraft.getInstance().player.sendSystemMessage(
                                    Component.literal("[ReactiveMusic]: Logging enabled: " + ReactiveMusic.chatLoggingEnabled));
                            //?} else {
                            /*MinecraftClient.getInstance().player.sendMessage(
                                    Text.literal("[ReactiveMusic]: Logging enabled: " + ReactiveMusic.chatLoggingEnabled), false);
                            *///?}
                            return 1;
                        })
                )
        );
    }
}
